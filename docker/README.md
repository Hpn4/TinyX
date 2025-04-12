# Docker

This folder contains the Docker Compose setup used to launch all the databases required by the project: Redis, Elasticsearch, MongoDB, and Neo4j.

```bash
.
├── README.md
├── docker-compose.yml
└── init_mongo.js      # Initializes MongoDB with a default user and collections
```

To start all databases in the background:

```bash
docker compose up -d
```
