# Service `repo-search`

## Description
`repo-search` is used to add and delete the content of the created posts with the list of hashtags present in them. This is done so that they can be searched and found later with the srvc-search service.

## Exposition
**This service is not exposed externally.**   

## Redis Listener
repo-search receive queries from Redis:
- Create and delete posts

## Environment Variables

**Quarkus Configuration**
```
HTTP_PORT:8092
```

**Swagger UI**
```
quarkus.swagger-ui.theme=original
quarkus.log.console.json=false
quarkus.console.color=true
quarkus.console.enabled=false
```

**Redis and ElasticSearch configuration**
```
ELASTIC_HOST:localhost
ELASTIC_PORT:9200
REDIS_CONNECTION_STRING:redis
REDIS_HOST:localhost
REDIS_PORT:6379
ELASTIC_INDEX:posts
```
**Other variables**
```
TRIM_INTERVAL:10m
CLAIM_INTERVAL:5s
```
## Database
It's a ElasticSearch database with the index "posts" that memorize created posts and the elements necessary to search them. For this purpose, a post is represented by a object containing it's id, content and a list of all the hashtags present in the post's content.


## Structure
```bash
repo-search/
├── src/
│   └── main/
│       ├── java/com/tinyx/
│       │   ├── controller/          # RedisStreamReader extensions
│       │   ├── repository/          # Data access and modification (elasticSearch)
│       │   ├── converter/           # Convert Queries to entities that can be store by the database
│       │   └── service/             # Core logic
│       └── resources/
│           └── application.properties
├── Dockerfile
├── pom.xml
└── README.md
```

