"""
recommendation_engine.py
-------------------------
Task 1: Smart Product Recommendation Engine.

The base system already does *visual similarity* ("show me more shirts like
this shirt"). This module instead answers a different question: "what goes
WITH this product?" (a running shoe pairs with socks, not with other shoes).

Approach (hybrid rule-based + embedding re-ranking):

1. RULE LAYER ("what category combos make sense")
   A hand-authored outfit/usage compatibility map encodes domain knowledge
   about what is commonly bought together with each article type, e.g.
       "Casual Shoes" -> ["Socks", "Watches", "Backpacks", "Sunglasses", ...]
   This plays the role that, in a production system, would normally be
   learned from co-purchase / basket data -- the dataset we were given has
   no transaction logs, so we encode the same prior knowledge explicitly
   and make it easy to swap in real co-occurrence statistics later
   (see `RecommendationEngine.fit_cooccurrence` below).

2. FILTER LAYER ("only sensible candidates")
   Candidates are restricted to products that also match the seed
   product's gender and (loosely) season/usage, so we don't recommend a
   winter scarf for a pair of summer sandals.

3. RANKING LAYER ("which specific items look best together")
   Within the filtered candidate pool, items are ranked by CLIP embedding
   similarity to the seed product -- this acts as a rough style/colour
   compatibility score (e.g. preferring a black watch to match black shoes)
   even though the categories themselves are *different*.

This keeps the system explainable: every recommendation is "category X is a
known complement of category Y, matched on gender/season, and ranked by
visual style similarity."
"""

from __future__ import annotations

import logging
from collections import defaultdict
from dataclasses import dataclass
from typing import Dict, List, Optional

import numpy as np
import pandas as pd

import config
from data_loader import ProductCatalog

logger = logging.getLogger(__name__)


# ---------------------------------------------------------------------------
# Domain knowledge: which article types complement which.
# Keys/values are matched against the `articleType` column. Extend freely.
# ---------------------------------------------------------------------------
COMPLEMENTARY_MAP: Dict[str, List[str]] = {
    "Casual Shoes": ["Socks", "Watches", "Backpacks", "Sunglasses", "Belts"],
    "Sports Shoes": ["Socks", "Sports Sandals", "Watches", "Track Pants", "Sports Sandals"],
    "Running Shoe": ["Socks", "Watches", "Track Pants", "Sports Sandals"],
    "Formal Shoes": ["Belts", "Ties", "Wallets", "Watches", "Socks"],
    "Flip Flops": ["Shorts", "Sunglasses", "Backpacks"],
    "Sandals": ["Shorts", "Sunglasses", "Backpacks"],
    "Heels": ["Clutches", "Earrings", "Necklace and Chains", "Bracelet"],
    "Tshirts": ["Jeans", "Casual Shoes", "Backpacks", "Sunglasses", "Caps"],
    "Shirts": ["Trousers", "Belts", "Formal Shoes", "Watches", "Ties"],
    "Jeans": ["Tshirts", "Shirts", "Casual Shoes", "Belts"],
    "Trousers": ["Shirts", "Belts", "Formal Shoes"],
    "Shorts": ["Tshirts", "Flip Flops", "Sandals", "Sunglasses", "Caps"],
    "Track Pants": ["Sports Shoes", "Sweatshirts", "Sports Sandals"],
    "Sweatshirts": ["Track Pants", "Jeans", "Casual Shoes", "Caps"],
    "Jackets": ["Jeans", "Trousers", "Casual Shoes", "Scarves"],
    "Dresses": ["Heels", "Clutches", "Earrings", "Sunglasses", "Necklace and Chains"],
    "Skirts": ["Tops", "Heels", "Earrings", "Handbags"],
    "Watches": ["Belts", "Wallets", "Sunglasses"],
    "Sunglasses": ["Caps", "Watches", "Backpacks"],
    "Backpacks": ["Casual Shoes", "Sunglasses", "Watches"],
    "Handbags": ["Heels", "Sunglasses", "Earrings"],
    "Wallets": ["Belts", "Watches"],
    "Ties": ["Shirts", "Formal Shoes", "Belts"],
}

# Fallback used whenever the seed product's exact article type is not in
# COMPLEMENTARY_MAP: fall back to its sub-category siblings being treated as
# "same outfit slot", and recommend from a generic accessory pool instead.
GENERIC_ACCESSORY_FALLBACK = ["Watches", "Belts", "Sunglasses", "Backpacks", "Wallets"]


@dataclass
class Recommendation:
    product_id: str
    display_name: str
    article_type: str
    score: float
    reason: str


class RecommendationEngine:
    def __init__(
        self,
        catalog: ProductCatalog,
        image_embeddings: Optional[np.ndarray] = None,
        embedding_ids: Optional[List[str]] = None,
    ):
        """
        image_embeddings / embedding_ids: optional aligned (N, D) embeddings
        and the product ids they correspond to. If omitted, the engine
        still works using rules + metadata filtering only (ranking simply
        falls back to "most recent" order), which is handy for unit tests
        that don't want to load CLIP.
        """
        self.catalog = catalog
        self._embeddings = image_embeddings
        self._id_to_row = (
            {pid: i for i, pid in enumerate(embedding_ids)} if embedding_ids else None
        )
        self._cooccurrence: Optional[Dict[str, Dict[str, int]]] = None

    # ------------------------------------------------------------------
    # Optional: learn real co-occurrence from transaction/session logs.
    # Drop-in replacement for the hand-authored COMPLEMENTARY_MAP once such
    # data exists; not required for this dataset.
    # ------------------------------------------------------------------
    def fit_cooccurrence(self, baskets: List[List[str]]) -> None:
        counts: Dict[str, Dict[str, int]] = defaultdict(lambda: defaultdict(int))
        for basket in baskets:
            types = {self.catalog.get(pid).article_type for pid in basket if pid}
            for a in types:
                for b in types:
                    if a != b:
                        counts[a][b] += 1
        self._cooccurrence = {k: dict(v) for k, v in counts.items()}

    # ------------------------------------------------------------------
    def _complementary_types(self, article_type: str) -> List[str]:
        if self._cooccurrence and article_type in self._cooccurrence:
            ranked = sorted(
                self._cooccurrence[article_type].items(), key=lambda kv: -kv[1]
            )
            return [t for t, _ in ranked]
        if article_type in COMPLEMENTARY_MAP:
            return COMPLEMENTARY_MAP[article_type]
        return GENERIC_ACCESSORY_FALLBACK

    def _style_score(self, seed_id: str, candidate_id: str) -> float:
        if self._embeddings is None or self._id_to_row is None:
            return 0.0
        if seed_id not in self._id_to_row or candidate_id not in self._id_to_row:
            return 0.0
        seed_vec = self._embeddings[self._id_to_row[seed_id]]
        cand_vec = self._embeddings[self._id_to_row[candidate_id]]
        return float(np.dot(seed_vec, cand_vec))  # cosine, vectors are normalized

    # ------------------------------------------------------------------
    def recommend(
        self,
        product_id: str,
        top_k: int = config.RECO_TOP_K_DEFAULT,
        match_gender: bool = True,
        match_season: bool = False,
    ) -> List[Recommendation]:
        seed = self.catalog.get(product_id)
        complementary_types = self._complementary_types(seed.article_type)

        df = self.catalog.df
        mask = df["articleType"].isin(complementary_types) & (df["id"] != seed.id)
        if match_gender and seed.gender:
            mask &= df["gender"].isin([seed.gender, "Unisex"])
        if match_season and seed.season:
            mask &= df["season"] == seed.season

        candidates = df[mask]
        if candidates.empty:
            logger.info("No metadata-filtered candidates for %s; relaxing filters.", product_id)
            candidates = df[df["articleType"].isin(complementary_types) & (df["id"] != seed.id)]

        # Cap pool size before the (more expensive) embedding re-ranking.
        if len(candidates) > config.RECO_CANDIDATE_POOL:
            candidates = candidates.sample(
                n=config.RECO_CANDIDATE_POOL, random_state=config.RANDOM_SEED
            )

        scored: List[Recommendation] = []
        for _, row in candidates.iterrows():
            score = self._style_score(seed.id, row["id"])
            # Preserve category priority as a tie-breaker / base score so
            # results are sensible even without embeddings: earlier entries
            # in the complementary list rank slightly higher by default.
            category_rank = complementary_types.index(row["articleType"]) if row[
                "articleType"
            ] in complementary_types else len(complementary_types)
            base = 1.0 - (category_rank / max(len(complementary_types), 1)) * 0.05
            scored.append(
                Recommendation(
                    product_id=row["id"],
                    display_name=row["productDisplayName"],
                    article_type=row["articleType"],
                    score=score + base,
                    reason=(
                        f"'{row['articleType']}' is a known complement of "
                        f"'{seed.article_type}'"
                        + (
                            f"; style-matched via CLIP image similarity ({score:.2f})"
                            if self._embeddings is not None
                            else ""
                        )
                    ),
                )
            )

        scored.sort(key=lambda r: -r.score)
        return scored[:top_k]

    # ------------------------------------------------------------------
    def explain(self, product_id: str, top_k: int = config.RECO_TOP_K_DEFAULT) -> str:
        seed = self.catalog.get(product_id)
        recs = self.recommend(product_id, top_k=top_k)
        lines = [
            f"Input Product: {seed.display_name} (articleType={seed.article_type}, "
            f"gender={seed.gender})",
            "Recommended Products:",
        ]
        for r in recs:
            lines.append(f"  - {r.display_name}  [{r.reason}]")
        return "\n".join(lines)
