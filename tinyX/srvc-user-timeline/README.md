# **Service `srvc-user-timeline`**

## Description
`srvc-user-timeline` is a microservice that manages user's timeline the system. He is in charge of creating the timeline for one or more users.

## Exposure
**This service is exposed externally.**  
You can interact with its REST API through the following Swagger interface:  
**Swagger UI**: `http://localhost:8081/q/swagger-ui` 

## Redis Listener
`srvc-user-timeline` service does not use Redis

## Environment Variables
**Quarkus Configuration**
```
HTTP_PORT:8081
```

**To make Swagger UI look cool**
```
quarkus.log.console.json=false
quarkus.console.color=true
quarkus.console.enabled=false
```

**Redis & MongoDB configuration**
```bash
MONGODB_DATABASE:Tinyx
REDIS_HOST:localhost
REDIS_PORT:6379
MONGODB_USER:admin
MONGODB_PASSWD:admin
MONGODB_HOST:localhost
MONGODB_PORT:27017
MONGODB_DATABASE:Tinyx
TRIM_INTERVAL:10m
CLAIM_INTERVAL:5s
```

**External services URLs**
```bash
POST_REST_HOST:localhost
POST_REST_PORT:8085
```

---

## Project Structure
```bash
srvc-user-timeline/
├── src/
│   └── main/
│       ├── java/com/tinyx/
│       │   ├── controller/          # REST endpoints
│       │   ├── repository/          # Data access (MongoDB, Redis)
│       │   └── service/             # Core logic
│       └── resources/
│           └── application.properties
├── Dockerfile
└── README.md
```
