"""
main.py
-------
End-to-end pipeline: load data -> build/load CLIP embeddings -> run all
three tasks -> save outputs (CSVs + plots) to OUTPUT_DIR.

Usage:
    python main.py --sample 2000

Run with no --sample to use the full dataset (~44k images; slower).
"""

from __future__ import annotations

import argparse
import logging
import os

import config
from data_loader import ProductCatalog
from clip_embeddings import CLIPEmbedder, build_or_load_image_embeddings
from recommendation_engine import RecommendationEngine
from catalog_dedup import build_clean_catalog
from search_engine import ReverseSearchEngine
import visualization as viz

logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s")
logger = logging.getLogger("main")


def run(sample_size: int | None):
    os.makedirs(config.OUTPUT_DIR, exist_ok=True)

    logger.info("Loading product catalog...")
    catalog = ProductCatalog.load(sample_size=sample_size)
    product_ids = catalog.all_ids()
    image_paths = catalog.df["image_path"].tolist()

    logger.info("Building/loading CLIP embeddings for %d products...", len(product_ids))
    embedder = CLIPEmbedder()
    embeddings = build_or_load_image_embeddings(product_ids, image_paths, embedder=embedder)

    # ---------------- Task 1: Recommendations ----------------
    logger.info("=== Task 1: Smart Product Recommendation Engine ===")
    reco_engine = RecommendationEngine(catalog, image_embeddings=embeddings, embedding_ids=product_ids)
    demo_seed = catalog.filter(articleType="Casual Shoes")
    if demo_seed.empty:
        demo_seed_id = product_ids[0]
    else:
        demo_seed_id = demo_seed.iloc[0]["id"]
    recs = reco_engine.recommend(demo_seed_id, top_k=5)
    print(reco_engine.explain(demo_seed_id))
    fig1 = viz.plot_recommendations(catalog, demo_seed_id, recs)
    fig1.savefig(os.path.join(config.OUTPUT_DIR, "task1_recommendations.png"), dpi=150)

    # ---------------- Task 2: Deduplication ----------------
    logger.info("=== Task 2: Unique Product Catalog Creation ===")
    dedup_result = build_clean_catalog(catalog, embeddings, product_ids)
    dedup_result.clean_catalog.to_csv(config.CLEAN_CATALOG_CSV, index=False)
    dedup_result.duplicate_map.to_csv(config.DUPLICATE_MAP_CSV, index=False)
    print(
        f"Original products: {dedup_result.n_original} | "
        f"Unique products: {dedup_result.n_unique} | "
        f"Duplicate groups found: {dedup_result.n_duplicate_groups}"
    )
    if not dedup_result.duplicate_map.empty:
        first_rep = dedup_result.duplicate_map.iloc[0]["representative_id"]
        members = [first_rep] + dedup_result.duplicate_map[
            dedup_result.duplicate_map["representative_id"] == first_rep
        ]["duplicate_id"].tolist()
        fig2 = viz.plot_dedup_group(catalog, members, first_rep)
        fig2.savefig(os.path.join(config.OUTPUT_DIR, "task2_duplicate_group.png"), dpi=150)
    else:
        logger.info("No duplicate groups found at threshold=%.2f", config.DEDUP_SIMILARITY_THRESHOLD)

    # ---------------- Task 3: Reverse Search ----------------
    logger.info("=== Task 3: Reverse Product Search ===")
    search_engine = ReverseSearchEngine(catalog, embeddings, product_ids, embedder=embedder)
    demo_query = "blue casual shirt"
    print(search_engine.pretty_print(demo_query, top_k=5))
    results = search_engine.search(demo_query, top_k=5)
    fig3 = viz.plot_search_results(demo_query, results)
    fig3.savefig(os.path.join(config.OUTPUT_DIR, "task3_search_results.png"), dpi=150)

    logger.info("All outputs written to %s", config.OUTPUT_DIR)


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--sample", type=int, default=config.DEMO_SAMPLE_SIZE)
    args = parser.parse_args()
    run(sample_size=args.sample)
