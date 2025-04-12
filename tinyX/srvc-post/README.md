# **Service `srvc-post`**

## Description
`srvc-post` is a microservice that manages posts within the system. He is in charge of the logic in creating, deleting and managing (get, replies) posts from a MongoDB database.

## Exposure
**This service is exposed externally.**  
You can interact with its REST API through the following Swagger interface:  
**Swagger UI**: `http://localhost:8085/q/swagger-ui`

## Redis Publisher
`srvc-post` service publishes events on Redis to notify the following services:
- `repo-post`
- `repo-home-timeline`
- `repo-user-timeline`

The events published include:
- Create & Delete posts


## Environment Variables
**Quarkus Configuration**
```
HTTP_PORT:8085
```

**To make Swagger UI look cool**
```bash
quarkus.log.console.json=false
quarkus.console.color=true
quarkus.console.enabled=false
```

**Redis & MongoDB configuration**
```bash
REDIS_HOST:localhost
REDIS_PORT:6379
MONGODB_USER:admin
MONGODB_PASSWD:admin
MONGODB_HOST:localhost
MONGODB_PORT:27017
MONGODB_DATABASE:Tinyx
```

**External services URLs**
```bash
MEDIA_REST_HOST:localhost
MEDIA_REST_PORT:8087
USER_REST_HOST:localhost
USER_REST_PORT:8086
```

---

## Project Structure
```bash
srvc-post/
├── src/
│   └── main/
│       ├── java/com/tinyx/
│       │   ├── controller/          # REST endpoints
│       │   ├── converter/           # Contracts converters
│       │   ├── repository/          # Data access (MongoDB, Redis)
│       │   └── service/             # Core logic
│       └── resources/
│           └── application.properties
├── Dockerfile
└── README.md
```

