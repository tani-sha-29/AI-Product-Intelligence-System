"""
config.py
---------
Central configuration for the AI Product Intelligence System.

Update DATA_DIR / IMAGES_DIR / STYLES_CSV to point at wherever you have the
Kaggle "Fashion Product Images (Small)" dataset extracted, e.g.:

    /kaggle/input/fashion-product-images-small/
        images/
            15970.jpg
            39386.jpg
            ...
        styles.csv

If you are running this on Kaggle, the defaults below already match the
standard mount path for that dataset.
"""

import os

# ---------------------------------------------------------------------------
# Paths
# ---------------------------------------------------------------------------
DATA_DIR = os.environ.get(
    "FASHION_DATA_DIR",
    "/kaggle/input/fashion-product-images-small",
)
IMAGES_DIR = os.path.join(DATA_DIR, "images")
STYLES_CSV = os.path.join(DATA_DIR, "styles.csv")

OUTPUT_DIR = os.environ.get("OUTPUT_DIR", "./outputs")
EMBEDDINGS_CACHE = os.path.join(OUTPUT_DIR, "embeddings_cache.npz")
CLEAN_CATALOG_CSV = os.path.join(OUTPUT_DIR, "clean_catalog.csv")
DUPLICATE_MAP_CSV = os.path.join(OUTPUT_DIR, "duplicate_map.csv")

# ---------------------------------------------------------------------------
# Model
# ---------------------------------------------------------------------------
# Any HuggingFace CLIP checkpoint works. ViT-B/32 is a good speed/quality
# tradeoff for a homework-scale dataset (~44k images).
CLIP_MODEL_NAME = os.environ.get("CLIP_MODEL_NAME", "openai/clip-vit-base-patch32")
DEVICE = os.environ.get("DEVICE", "cuda")  # falls back to cpu automatically

# ---------------------------------------------------------------------------
# Task 1: Recommendation engine
# ---------------------------------------------------------------------------
# How many candidate complementary products to keep before re-ranking
RECO_CANDIDATE_POOL = 200
RECO_TOP_K_DEFAULT = 5

# ---------------------------------------------------------------------------
# Task 2: Deduplication
# ---------------------------------------------------------------------------
# Cosine-similarity threshold above which two products are considered
# near-duplicates. 0.97+ on CLIP embeddings is typically "basically the same
# photo" / same product re-listed by another seller.
DEDUP_SIMILARITY_THRESHOLD = 0.97

# ---------------------------------------------------------------------------
# Task 3: Reverse (text) search
# ---------------------------------------------------------------------------
SEARCH_TOP_K_DEFAULT = 5

# Sample size to use when running the whole pipeline as a quick demo
# (set to None to use the full dataset)
DEMO_SAMPLE_SIZE = int(os.environ.get("DEMO_SAMPLE_SIZE", "2000"))

RANDOM_SEED = 42
