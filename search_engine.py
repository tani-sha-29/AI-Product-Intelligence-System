"""
search_engine.py
------------------
Task 3: Reverse Product Search.

The base system finds products from an *image* query. This module flips
that: given free-text (e.g. "blue casual shirt"), find the best-matching
product photos. This works because CLIP embeds images and text into the
*same* vector space, so cosine similarity between a text embedding and an
image embedding is a meaningful relevance score.
"""

from __future__ import annotations

import logging
from dataclasses import dataclass
from typing import List

import numpy as np

import config
from clip_embeddings import CLIPEmbedder
from data_loader import ProductCatalog

logger = logging.getLogger(__name__)


@dataclass
class SearchResult:
    product_id: str
    display_name: str
    image_path: str
    score: float


class ReverseSearchEngine:
    def __init__(
        self,
        catalog: ProductCatalog,
        image_embeddings: np.ndarray,
        product_ids: List[str],
        embedder: "CLIPEmbedder | None" = None,
    ):
        self.catalog = catalog
        self.embeddings = image_embeddings  # (N, D), L2-normalized
        self.product_ids = product_ids
        self.embedder = embedder  # lazily created if None and search() is called

    def search(self, query: str, top_k: int = config.SEARCH_TOP_K_DEFAULT) -> List[SearchResult]:
        if self.embedder is None:
            self.embedder = CLIPEmbedder()

        text_vec = self.embedder.encode_text([query])[0]  # (D,)
        scores = self.embeddings @ text_vec  # cosine similarity, (N,)

        top_idx = np.argsort(-scores)[:top_k]
        results = []
        for idx in top_idx:
            pid = self.product_ids[idx]
            product = self.catalog.get(pid)
            results.append(
                SearchResult(
                    product_id=pid,
                    display_name=product.display_name,
                    image_path=product.image_path,
                    score=float(scores[idx]),
                )
            )
        return results

    def pretty_print(self, query: str, top_k: int = config.SEARCH_TOP_K_DEFAULT) -> str:
        results = self.search(query, top_k=top_k)
        lines = [f"Input Query: {query!r}", "Top Matching Products:"]
        for i, r in enumerate(results, start=1):
            lines.append(f"  {i}. {r.display_name}  (score={r.score:.3f})")
        return "\n".join(lines)
