# Service: `srvc-search`
## Description
The `srvc-search` service can be used to search for created posts by using a text phrase and/or a list of hashtags.   

## Exposure
**This service is exposed externally.**  
You can interact with its REST API through the following Swagger interface:  
**Swagger UI:** `http://localhost:8084/q/swagger-ui`


## Environment Variables

**Quarkus Configuration**
```
HTTP_PORT:8084
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

**External Services URLs**
```
REST_HOST:localhost
REST_PORT:8085
```

## Database
For more information, check out the [repo-search](../repo-search/README.md) page.


## Structure
```bash
srvc-search/
├── src/
│   └── main/
│       ├── java/com/tinyx/
│       │   ├── controller/          # REST endpoints
│       │   ├── repository/          # Data access (ElasticSearch)
│       │   └── service/             # Core logic
│       └── resources/
│           └── application.properties
├── Dockerfile
├── pom.xml
└── README.md
```

