# AI Product Intelligence System 

Extends the base product-intelligence system with three advanced features, all
built on a shared CLIP embedding backbone:

| Task | Module | What it does |
|---|---|---|
| 1 | `recommendation_engine.py` | Recommends **complementary** products (bought together), not just visually similar ones |
| 2 | `catalog_dedup.py` | Detects near-duplicate listings via image embeddings and builds a clean, unique catalog |
| 3 | `search_engine.py` | Lets users search products using **text** instead of an image (CLIP text↔image) |

## Project structure

```
ai_product_intelligence/
├── config.py                 # paths, model name, thresholds
├── data_loader.py            # loads styles.csv + resolves image paths
├── clip_embeddings.py        # CLIP wrapper (image & text encoding) + on-disk caching
├── recommendation_engine.py  # Feature 1
├── catalog_dedup.py          # Feature 2
├── search_engine.py          # Feature 3
├── visualization.py          # matplotlib visualizations for all 3 tasks
├── main.py                   # CLI: runs the full pipeline end-to-end
├── app.py                    # FastAPI backend exposing all 3 tasks as HTTP endpoints
├── requirements.txt
├── notebook/
│   └── Day2_Homework.ipynb   # submission notebook (run on Kaggle with the dataset attached)
└── report/
    └── REPORT.md             # write-up for submission (add screenshots after running)
```


Upcoming

✔ Spring Boot Backend

✔ REST APIs

✔ React Frontend

✔ User Authentication

✔ Wishlist

✔ Deployment
