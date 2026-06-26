"""
app.py
------
FastAPI backend that exposes the three tasks as HTTP endpoints, sitting on
top of the same engines used in main.py / the notebook.

Run:
    uvicorn app:app --reload --port 8000

Then e.g.:
    GET  /products/{product_id}
    GET  /recommend/{product_id}?top_k=5
    GET  /search?query=blue casual shirt&top_k=5
    POST /catalog/dedup            (rebuilds the deduplicated catalog)
    GET  /catalog/stats

Embeddings are built once at startup (or loaded from cache) and held in
memory for the lifetime of the process.
"""

from __future__ import annotations

import logging
from typing import Optional

from fastapi import FastAPI, HTTPException
from pydantic import BaseModel

import config
from data_loader import ProductCatalog
from clip_embeddings import CLIPEmbedder, build_or_load_image_embeddings
from recommendation_engine import RecommendationEngine
from catalog_dedup import build_clean_catalog, DedupResult
from search_engine import ReverseSearchEngine

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("app")

app = FastAPI(
    title="AI Product Intelligence API",
    description="Recommendation engine, catalog deduplication, and reverse "
    "(text-to-image) product search powered by CLIP embeddings.",
    version="1.0.0",
)

# ---------------------------------------------------------------------------
# Global state, built once at startup.
# ---------------------------------------------------------------------------
_state = {
    "catalog": None,
    "embedder": None,
    "embeddings": None,
    "product_ids": None,
    "reco_engine": None,
    "search_engine": None,
    "dedup_result": None,
}


@app.on_event("startup")
def startup():
    logger.info("Loading catalog and embeddings...")
    catalog = ProductCatalog.load(sample_size=config.DEMO_SAMPLE_SIZE)
    product_ids = catalog.all_ids()
    image_paths = catalog.df["image_path"].tolist()

    embedder = CLIPEmbedder()
    embeddings = build_or_load_image_embeddings(product_ids, image_paths, embedder=embedder)

    _state["catalog"] = catalog
    _state["embedder"] = embedder
    _state["embeddings"] = embeddings
    _state["product_ids"] = product_ids
    _state["reco_engine"] = RecommendationEngine(
        catalog, image_embeddings=embeddings, embedding_ids=product_ids
    )
    _state["search_engine"] = ReverseSearchEngine(
        catalog, embeddings, product_ids, embedder=embedder
    )
    logger.info("Startup complete: %d products loaded.", len(product_ids))


# ---------------------------------------------------------------------------
# Schemas
# ---------------------------------------------------------------------------
class ProductOut(BaseModel):
    id: str
    display_name: str
    article_type: str
    gender: str
    season: str
    image_path: str


class RecommendationOut(BaseModel):
    product_id: str
    display_name: str
    article_type: str
    score: float
    reason: str


class SearchResultOut(BaseModel):
    product_id: str
    display_name: str
    image_path: str
    score: float


class DedupStatsOut(BaseModel):
    n_original: int
    n_unique: int
    n_duplicate_groups: int


# ---------------------------------------------------------------------------
# Routes
# ---------------------------------------------------------------------------
@app.get("/products/{product_id}", response_model=ProductOut)
def get_product(product_id: str):
    catalog: ProductCatalog = _state["catalog"]
    try:
        p = catalog.get(product_id)
    except KeyError:
        raise HTTPException(status_code=404, detail="Product not found")
    return ProductOut(
        id=p.id,
        display_name=p.display_name,
        article_type=p.article_type,
        gender=p.gender,
        season=p.season,
        image_path=p.image_path,
    )


@app.get("/recommend/{product_id}", response_model=list[RecommendationOut])
def recommend(product_id: str, top_k: int = config.RECO_TOP_K_DEFAULT):
    """Task 1: complementary product recommendations for a given product id."""
    engine: RecommendationEngine = _state["reco_engine"]
    try:
        recs = engine.recommend(product_id, top_k=top_k)
    except KeyError:
        raise HTTPException(status_code=404, detail="Product not found")
    return [RecommendationOut(**r.__dict__) for r in recs]


@app.get("/search", response_model=list[SearchResultOut])
def search(query: str, top_k: int = config.SEARCH_TOP_K_DEFAULT):
    """Task 3: free-text reverse product search."""
    engine: ReverseSearchEngine = _state["search_engine"]
    results = engine.search(query, top_k=top_k)
    return [SearchResultOut(**r.__dict__) for r in results]


@app.post("/catalog/dedup", response_model=DedupStatsOut)
def rebuild_catalog(threshold: Optional[float] = None):
    """Task 2: (re)build the deduplicated catalog and cache the result."""
    catalog: ProductCatalog = _state["catalog"]
    embeddings = _state["embeddings"]
    product_ids = _state["product_ids"]

    result: DedupResult = build_clean_catalog(
        catalog,
        embeddings,
        product_ids,
        threshold=threshold or config.DEDUP_SIMILARITY_THRESHOLD,
    )
    _state["dedup_result"] = result
    result.clean_catalog.to_csv(config.CLEAN_CATALOG_CSV, index=False)
    result.duplicate_map.to_csv(config.DUPLICATE_MAP_CSV, index=False)
    return DedupStatsOut(
        n_original=result.n_original,
        n_unique=result.n_unique,
        n_duplicate_groups=result.n_duplicate_groups,
    )


@app.get("/catalog/stats", response_model=DedupStatsOut)
def catalog_stats():
    result: Optional[DedupResult] = _state["dedup_result"]
    if result is None:
        raise HTTPException(
            status_code=400, detail="Run POST /catalog/dedup first to build the clean catalog."
        )
    return DedupStatsOut(
        n_original=result.n_original,
        n_unique=result.n_unique,
        n_duplicate_groups=result.n_duplicate_groups,
    )


@app.get("/health")
def health():
    return {"status": "ok", "products_loaded": len(_state["product_ids"] or [])}
