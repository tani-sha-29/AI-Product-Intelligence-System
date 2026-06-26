"""
clip_embeddings.py
-------------------
Thin wrapper around a HuggingFace CLIP model that gives us a single shared
embedding space for both product images and free-text queries.

Used for:
1. Recommendation System
2. Product Deduplication
3. Reverse Image/Text Search

Embeddings are L2-normalized so dot product == cosine similarity.
"""

from __future__ import annotations

import logging
import os
from typing import Iterable, List

import numpy as np

import config

logger = logging.getLogger(__name__)


class CLIPEmbedder:
    def __init__(
        self,
        model_name: str = config.CLIP_MODEL_NAME,
        device: str = config.DEVICE,
    ):
        import torch
        from transformers import CLIPModel, CLIPProcessor

        self.torch = torch
        self.device = device if torch.cuda.is_available() else "cpu"

        logger.info(
            "Loading CLIP model '%s' on %s",
            model_name,
            self.device,
        )

        self.model = (
            CLIPModel.from_pretrained(model_name)
            .to(self.device)
            .eval()
        )

        self.processor = CLIPProcessor.from_pretrained(model_name)

    # -------------------------------------------------------------
    @staticmethod
    def _normalize(vectors: np.ndarray) -> np.ndarray:
        norms = np.linalg.norm(
            vectors,
            axis=-1,
            keepdims=True,
        )
        norms[norms == 0] = 1e-8
        return vectors / norms

    # -------------------------------------------------------------
    def encode_images(
        self,
        image_paths: List[str],
        batch_size: int = 64,
    ) -> np.ndarray:

        from PIL import Image

        all_embeddings = []

        with self.torch.no_grad():

            for start in range(
                0,
                len(image_paths),
                batch_size,
            ):

                batch_paths = image_paths[
                    start : start + batch_size
                ]

                images = []

                for path in batch_paths:

                    try:
                        images.append(
                            Image.open(path).convert("RGB")
                        )

                    except Exception as exc:

                        logger.warning(
                            "Failed to open %s (%s). Using blank image.",
                            path,
                            exc,
                        )

                        images.append(
                            Image.new(
                                "RGB",
                                (224, 224),
                                color=(255, 255, 255),
                            )
                        )

                inputs = self.processor(
                    images=images,
                    return_tensors="pt",
                )

                inputs = {
                    k: v.to(self.device)
                    for k, v in inputs.items()
                }

                feats = self.model.get_image_features(
                    **inputs
                )

                # Compatibility with old/new Transformers
                if not isinstance(feats, self.torch.Tensor):
                    if hasattr(feats, "pooler_output"):
                        feats = feats.pooler_output
                    elif hasattr(feats, "last_hidden_state"):
                        feats = feats.last_hidden_state[:, 0]
                    else:
                        raise TypeError(
                            f"Unexpected output type: {type(feats)}"
                        )

                all_embeddings.append(
                    feats.detach().cpu().numpy()
                )

        embeddings = np.concatenate(
            all_embeddings,
            axis=0,
        )

        return self._normalize(
            embeddings
        )

    # -------------------------------------------------------------
    def encode_text(
        self,
        texts: Iterable[str],
    ) -> np.ndarray:

        texts = list(texts)

        with self.torch.no_grad():

            inputs = self.processor(
                text=texts,
                return_tensors="pt",
                padding=True,
                truncation=True,
            )

            inputs = {
                k: v.to(self.device)
                for k, v in inputs.items()
            }

            feats = self.model.get_text_features(
                **inputs
            )

            if not isinstance(feats, self.torch.Tensor):
                if hasattr(feats, "pooler_output"):
                    feats = feats.pooler_output
                elif hasattr(feats, "last_hidden_state"):
                    feats = feats.last_hidden_state[:, 0]
                else:
                    raise TypeError(
                        f"Unexpected output type: {type(feats)}"
                    )

        return self._normalize(
            feats.detach().cpu().numpy()
        )


# -----------------------------------------------------------------
# Cache helper
# -----------------------------------------------------------------
def build_or_load_image_embeddings(
    product_ids: List[str],
    image_paths: List[str],
    embedder: "CLIPEmbedder | None" = None,
    cache_path: str = config.EMBEDDINGS_CACHE,
    force_rebuild: bool = False,
) -> np.ndarray:

    if (
        not force_rebuild
        and os.path.exists(cache_path)
    ):

        cached = np.load(
            cache_path,
            allow_pickle=True,
        )

        cached_ids = cached["ids"].tolist()

        if cached_ids == list(product_ids):

            logger.info(
                "Loaded %d cached embeddings from %s",
                len(cached_ids),
                cache_path,
            )

            return cached["embeddings"]

        logger.info(
            "Cache exists but IDs changed. Rebuilding."
        )

    if embedder is None:
        embedder = CLIPEmbedder()

    embeddings = embedder.encode_images(
        image_paths
    )

    os.makedirs(
        os.path.dirname(cache_path) or ".",
        exist_ok=True,
    )

    np.savez_compressed(
        cache_path,
        ids=np.array(
            product_ids,
            dtype=object,
        ),
        embeddings=embeddings,
    )

    logger.info(
        "Cached %d embeddings to %s",
        len(product_ids),
        cache_path,
    )

    return embeddings