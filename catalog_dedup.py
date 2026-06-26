"""
catalog_dedup.py
------------------
Task 2: Unique Product Catalog Creation.

Marketplaces end up with many near-duplicate listings (the same physical
product, photographed slightly differently, re-uploaded by multiple
sellers). We detect these using CLIP image embeddings:

  1. Embed every product image into the shared CLIP space.
  2. Build a similarity graph: connect two products if their cosine
     similarity exceeds DEDUP_SIMILARITY_THRESHOLD.
  3. Connected components of that graph = duplicate groups (this is just a
     Union-Find / single-link clustering, which is robust to choosing an
     arbitrary "first" duplicate and naturally handles groups of 3+).
  4. Within each group, pick one representative: the product with the
     longest/most descriptive `productDisplayName` (a decent proxy for
     "most complete metadata"), and report the rest as duplicates removed.

This is intentionally a similarity-graph approach rather than naive
nearest-neighbour matching, since duplicate listings often form chains
(A~B~C even if A and C alone are just under the threshold).
"""

from __future__ import annotations

import logging
from dataclasses import dataclass
from typing import Dict, List

import numpy as np
import pandas as pd
from sklearn.metrics.pairwise import cosine_similarity

import config
from data_loader import ProductCatalog

logger = logging.getLogger(__name__)


@dataclass
class DedupResult:
    clean_catalog: pd.DataFrame
    duplicate_map: pd.DataFrame  # columns: representative_id, duplicate_id, similarity
    n_original: int
    n_unique: int
    n_duplicate_groups: int


class _UnionFind:
    def __init__(self, items: List[str]):
        self.parent = {item: item for item in items}

    def find(self, x: str) -> str:
        while self.parent[x] != x:
            self.parent[x] = self.parent[self.parent[x]]
            x = self.parent[x]
        return x

    def union(self, a: str, b: str) -> None:
        ra, rb = self.find(a), self.find(b)
        if ra != rb:
            self.parent[ra] = rb


def find_duplicate_groups(
    product_ids: List[str],
    embeddings: np.ndarray,
    threshold: float = config.DEDUP_SIMILARITY_THRESHOLD,
    block_size: int = 2000,
) -> Dict[str, List[str]]:
    """
    Returns {representative_id: [all ids in that duplicate group, incl. rep]}
    using connected components over the similarity graph.

    Computed in blocks to avoid materializing a full (N, N) matrix for large
    catalogs.
    """
    n = len(product_ids)
    uf = _UnionFind(product_ids)

    for start in range(0, n, block_size):
        end = min(start + block_size, n)
        block = embeddings[start:end]
        sims = cosine_similarity(block, embeddings)  # (block_size, n)
        # Only consider j > global index of i to avoid double work / self-pairs.
        for local_i, global_i in enumerate(range(start, end)):
            row = sims[local_i]
            matches = np.where(row >= threshold)[0]
            for j in matches:
                if j == global_i:
                    continue
                uf.union(product_ids[global_i], product_ids[j])

    groups: Dict[str, List[str]] = {}
    for pid in product_ids:
        root = uf.find(pid)
        groups.setdefault(root, []).append(pid)
    return groups


def build_clean_catalog(
    catalog: ProductCatalog,
    embeddings: np.ndarray,
    product_ids: List[str],
    threshold: float = config.DEDUP_SIMILARITY_THRESHOLD,
) -> DedupResult:
    groups = find_duplicate_groups(product_ids, embeddings, threshold=threshold)
    id_to_idx = {pid: i for i, pid in enumerate(product_ids)}

    dup_rows = []
    representative_ids = []

    for _root, members in groups.items():
        if len(members) == 1:
            representative_ids.append(members[0])
            continue

        # Choose representative = longest display name (most descriptive),
        # ties broken by lowest id for determinism.
        members_sorted = sorted(
            members,
            key=lambda pid: (
                -len(catalog.get(pid).display_name or ""),
                pid,
            ),
        )
        rep = members_sorted[0]
        representative_ids.append(rep)

        rep_vec = embeddings[id_to_idx[rep]]
        for dup in members_sorted[1:]:
            sim = float(np.dot(rep_vec, embeddings[id_to_idx[dup]]))
            dup_rows.append(
                {
                    "representative_id": rep,
                    "representative_name": catalog.get(rep).display_name,
                    "duplicate_id": dup,
                    "duplicate_name": catalog.get(dup).display_name,
                    "similarity": sim,
                }
            )

    clean_df = catalog.df[catalog.df["id"].isin(representative_ids)].reset_index(drop=True)
    dup_df = pd.DataFrame(
        dup_rows,
        columns=["representative_id", "representative_name", "duplicate_id", "duplicate_name", "similarity"],
    )

    n_groups_with_dups = sum(1 for m in groups.values() if len(m) > 1)
    logger.info(
        "Dedup complete: %d -> %d products (%d duplicate groups, %d duplicates removed).",
        len(product_ids),
        len(clean_df),
        n_groups_with_dups,
        len(dup_df),
    )

    return DedupResult(
        clean_catalog=clean_df,
        duplicate_map=dup_df,
        n_original=len(product_ids),
        n_unique=len(clean_df),
        n_duplicate_groups=n_groups_with_dups,
    )
