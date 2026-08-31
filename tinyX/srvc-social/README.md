# **Service `srvc-social`**

## Description
`srvc-social` is a microservice that manages the social relationships between users: follow, block, likes of posts, and retrieval of social interactions.
It encapsulates the logic related to connections between users and their interactions with content.

This service is exposed externally, via a REST API documented in Swagger.

## Exposure
**This service is exposed externally.**  
You can interact with its REST API through the following Swagger interface:  
**Swagger UI**: `http://localhost:8083/q/swagger-ui`

## Redis Publisher
The `srvc-social` service publishes events on Redis to notify the following services:
- `repo-social`

The events published include:
- Like & Unlike
- Follow & Unfollow
- Block & Unblock

## Environment Variables
**Quarkus Configuration**
```
HTTP_PORT:8083
```

**To make Swagger UI look cool**  
```
quarkus.console.enabled=false
```

**Redis & Neo4j configuration**
```
NEO4J_HOST:localhost
NEO4J_PORT:7687
REDIS_HOST:localhost
REDIS_PORT:6379
```

**External services URLs**
```
POST_REST_HOST:localhost
POST_REST_PORT:8085
USER_REST_HOST:localhost
USER_REST_PORT:8086
```

---

## Project Structure
```bash
srvc-social/
├── src/
│   └── main/
│       ├── java/com/tinyx/
│       │   ├── controller/          # REST endpoints
│       │   ├── repository/          # Data access (Neo4j/Redis)
│       │   └── service/             # Core logic
│       └── resources/
│           └── application.properties
├── Dockerfile
├── pom.xml
└── README.md
```
