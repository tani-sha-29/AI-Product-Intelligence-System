# Day 2 Homework — AI Product Intelligence System
## Report

**Dataset:** Fashion Product Images (Small)
**Shared backbone:** CLIP (`openai/clip-vit-base-patch32`) image & text embeddings

---

## Task 1: Smart Product Recommendation Engine

**Problem:** the base system finds visually *similar* products. This task instead
recommends *complementary* products (things bought together), e.g. a running shoe
should suggest socks, not other shoes.

**Approach — hybrid rule + embedding re-ranking:**

1. **Rule layer.** A hand-authored category-compatibility map (`COMPLEMENTARY_MAP` in
   `recommendation_engine.py`) encodes outfit knowledge, e.g.
   `"Casual Shoes" -> ["Socks", "Watches", "Backpacks", "Sunglasses", "Belts"]`.
   This dataset has no purchase/basket logs, so this map stands in for real
   co-occurrence statistics. The engine also exposes `fit_cooccurrence(baskets)` so
   real transaction data can replace the rule map later with zero other code changes.
2. **Filter layer.** Candidates are restricted to products matching the seed
   product's gender (and optionally season), so we don't recommend a winter scarf
   for summer sandals.
3. **Ranking layer.** Within the filtered candidate pool, items are ranked by CLIP
   image-embedding cosine similarity to the seed product — a rough style/colour
   compatibility proxy (e.g. a black watch ranks above a neon one for black shoes).

**Why this design:** it stays fully explainable — every recommendation reports
*which* category rule fired and *what* similarity score justified the specific item
chosen within that category.

**Deliverables produced:** `RecommendationEngine.recommend()` / `.explain()`,
`visualization.plot_recommendations()` (seed product + ranked recommendations,
side by side).

*(Insert screenshot: `task1_recommendations.png`)*

---

## Task 2: Unique Product Catalog Creation

**Problem:** marketplaces accumulate near-duplicate listings of the same physical
product uploaded by different sellers. Build a clean catalog with one entry per
product.

**Approach — similarity graph + connected components:**

1. Compute CLIP image embeddings for every product.
2. Build a similarity graph: connect two products whenever their cosine similarity
   exceeds a threshold (default `0.97`).
3. Take **connected components** (via Union-Find) rather than naive pairwise
   matching — this correctly merges duplicate *chains* (A similar to B, B similar to
   C, even if A and C alone fall just under the threshold).
4. Within each group, keep the product with the **longest/most descriptive
   `productDisplayName`** as the representative; the rest are reported as removed
   duplicates with their similarity score to the representative.

**Why this design:** connected components handle multi-seller duplicate groups of
any size, and choosing the most descriptive title as representative is a simple,
defensible heuristic for picking the "best" listing to keep without needing extra
seller/rating metadata that isn't in this dataset.

**Deliverables produced:** `catalog_dedup.build_clean_catalog()` → `clean_catalog.csv`,
`duplicate_map.csv`; `visualization.plot_dedup_group()`.

*(Insert screenshot: `task2_duplicate_group.png`, plus before/after product counts)*

---

## Task 3: Reverse Product Search (text → image)

**Problem:** allow users to search with text instead of an image.

**Approach:** CLIP embeds images and text into the **same** vector space. We embed
the catalog's images once (already needed for Tasks 1 & 2), then for any text query
we embed the query string and rank all product images by cosine similarity to it —
no separate keyword index required.

**Why CLIP specifically:** it was trained contrastively on (image, caption) pairs, so
"blue casual shirt" lands close to images of blue casual shirts in the same space,
even though no OCR/keyword text exists on the product photos themselves.

**Deliverables produced:** `search_engine.ReverseSearchEngine.search()` /
`.pretty_print()`, `visualization.plot_search_results()`.

*(Insert screenshot: `task3_search_results.png`)*

---

## Architecture notes

- `clip_embeddings.py` is the single shared embedding backbone used by all three
  tasks — image embeddings are computed once and cached (`outputs/embeddings_cache.npz`),
  then reused for recommendation re-ranking, duplicate detection, *and* as the search
  index for Task 3.
- `app.py` exposes the same three engines as a FastAPI backend
  (`GET /recommend/{id}`, `GET /search`, `POST /catalog/dedup`) for use outside the
  notebook.
- `main.py` runs the full pipeline end-to-end from the command line and writes all
  plots/CSVs to `outputs/`.

## How to reproduce

```bash
pip install -r requirements.txt
export FASHION_DATA_DIR=/path/to/fashion-product-images-small
python main.py --sample 2000     # or omit --sample to use the full dataset
```

Or open `notebook/Day2_Homework.ipynb` on Kaggle with the dataset attached.
