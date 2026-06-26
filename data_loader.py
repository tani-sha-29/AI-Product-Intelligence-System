"""
data_loader.py
--------------
Loads the Kaggle "Fashion Product Images (Small)" metadata (styles.csv) and
resolves image paths. Handles the few malformed rows the raw CSV is known
to contain.

Expected columns in styles.csv:
    id, gender, masterCategory, subCategory, articleType, baseColour,
    season, year, usage, productDisplayName
"""

from __future__ import annotations

import os
import logging
from dataclasses import dataclass
from typing import Optional

import pandas as pd

import config

logger = logging.getLogger(__name__)

REQUIRED_COLUMNS = [
    "id",
    "gender",
    "masterCategory",
    "subCategory",
    "articleType",
    "baseColour",
    "season",
    "year",
    "usage",
    "productDisplayName",
]


@dataclass
class Product:
    id: str
    gender: str
    master_category: str
    sub_category: str
    article_type: str
    base_colour: str
    season: str
    usage: str
    display_name: str
    image_path: str


class ProductCatalog:
    """Thin wrapper around the styles dataframe + image path resolution."""

    def __init__(self, df: pd.DataFrame):
        self.df = df

    # ------------------------------------------------------------------
    # Construction
    # ------------------------------------------------------------------
    @classmethod
    def load(
        cls,
        styles_csv: str = config.STYLES_CSV,
        images_dir: str = config.IMAGES_DIR,
        sample_size: Optional[int] = None,
        random_state: int = config.RANDOM_SEED,
    ) -> "ProductCatalog":
        if not os.path.exists(styles_csv):
            raise FileNotFoundError(
                f"Could not find styles.csv at '{styles_csv}'. "
                "Set FASHION_DATA_DIR or pass styles_csv= explicitly."
            )

        # The raw file has a handful of rows with stray commas in
        # productDisplayName, so we use the python engine + error skipping.
        df = pd.read_csv(styles_csv, on_bad_lines="skip", engine="python")

        missing = set(REQUIRED_COLUMNS) - set(df.columns)
        if missing:
            raise ValueError(f"styles.csv is missing expected columns: {missing}")

        df = df.dropna(subset=["id", "productDisplayName"]).copy()
        df["id"] = df["id"].astype(int).astype(str)

        df["image_path"] = df["id"].apply(
            lambda pid: os.path.join(images_dir, f"{pid}.jpg")
        )

        # Drop rows whose image file genuinely does not exist on disk so
        # downstream embedding code never has to special-case missing files.
        exists_mask = df["image_path"].apply(os.path.exists)
        n_missing = (~exists_mask).sum()
        if n_missing:
            logger.warning(
                "%d products reference a missing image file and will be dropped.",
                n_missing,
            )
        df = df[exists_mask].reset_index(drop=True)

        if sample_size is not None and sample_size < len(df):
            df = df.sample(n=sample_size, random_state=random_state).reset_index(
                drop=True
            )

        logger.info("Loaded %d products.", len(df))
        return cls(df)

    # ------------------------------------------------------------------
    # Access helpers
    # ------------------------------------------------------------------
    def __len__(self) -> int:
        return len(self.df)

    def get(self, product_id: str) -> Product:
        row = self.df.loc[self.df["id"] == str(product_id)]
        if row.empty:
            raise KeyError(f"Product id '{product_id}' not found in catalog.")
        row = row.iloc[0]
        return self._row_to_product(row)

    def all_ids(self) -> list[str]:
        return self.df["id"].tolist()

    def filter(self, **kwargs) -> pd.DataFrame:
        """Filter the underlying dataframe by exact-match column values."""
        mask = pd.Series(True, index=self.df.index)
        for key, value in kwargs.items():
            mask &= self.df[key] == value
        return self.df[mask]

    @staticmethod
    def _row_to_product(row: pd.Series) -> Product:
        return Product(
            id=row["id"],
            gender=row.get("gender", ""),
            master_category=row.get("masterCategory", ""),
            sub_category=row.get("subCategory", ""),
            article_type=row.get("articleType", ""),
            base_colour=row.get("baseColour", ""),
            season=row.get("season", ""),
            usage=row.get("usage", ""),
            display_name=row.get("productDisplayName", ""),
            image_path=row["image_path"],
        )

    def iter_products(self):
        for _, row in self.df.iterrows():
            yield self._row_to_product(row)
