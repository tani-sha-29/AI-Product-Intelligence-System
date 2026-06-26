"""
visualization.py
------------------
Matplotlib-based visualizations for the deliverables:
  - plot_recommendations: seed product + its complementary recommendations
  - plot_dedup_group: a duplicate group side by side
  - plot_search_results: text query + its top matching product photos
"""

from __future__ import annotations

from typing import List

import matplotlib.pyplot as plt
from PIL import Image

from data_loader import ProductCatalog
from recommendation_engine import Recommendation
from search_engine import SearchResult


def _load_img(path: str):
    try:
        return Image.open(path).convert("RGB")
    except Exception:
        return Image.new("RGB", (224, 224), color=(230, 230, 230))


def plot_recommendations(
    catalog: ProductCatalog, seed_product_id: str, recommendations: List[Recommendation]
):
    seed = catalog.get(seed_product_id)
    n = len(recommendations) + 1
    fig, axes = plt.subplots(1, n, figsize=(3 * n, 3.5))

    axes[0].imshow(_load_img(seed.image_path))
    axes[0].set_title(f"INPUT\n{seed.display_name[:25]}", fontsize=9, fontweight="bold")
    axes[0].axis("off")

    for ax, rec in zip(axes[1:], recommendations):
        product = catalog.get(rec.product_id)
        ax.imshow(_load_img(product.image_path))
        ax.set_title(f"{rec.display_name[:22]}\nscore={rec.score:.2f}", fontsize=8)
        ax.axis("off")

    fig.suptitle("Complementary Product Recommendations", fontsize=12)
    plt.tight_layout()
    return fig


def plot_dedup_group(catalog: ProductCatalog, member_ids: List[str], representative_id: str):
    fig, axes = plt.subplots(1, len(member_ids), figsize=(3 * len(member_ids), 3.5))
    if len(member_ids) == 1:
        axes = [axes]
    for ax, pid in zip(axes, member_ids):
        product = catalog.get(pid)
        ax.imshow(_load_img(product.image_path))
        label = "REPRESENTATIVE" if pid == representative_id else "duplicate (removed)"
        ax.set_title(f"{label}\n{product.display_name[:22]}", fontsize=8)
        ax.axis("off")
    fig.suptitle("Detected Duplicate Group", fontsize=12)
    plt.tight_layout()
    return fig


def plot_search_results(query: str, results: List[SearchResult]):
    fig, axes = plt.subplots(1, len(results), figsize=(3 * len(results), 3.5))
    if len(results) == 1:
        axes = [axes]
    for ax, r in zip(axes, results):
        ax.imshow(_load_img(r.image_path))
        ax.set_title(f"{r.display_name[:22]}\nscore={r.score:.3f}", fontsize=8)
        ax.axis("off")
    fig.suptitle(f"Reverse Search: \"{query}\"", fontsize=12)
    plt.tight_layout()
    return fig
