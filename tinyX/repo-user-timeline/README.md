# **Service `repo-user-timeline`**

## Description
`repo-user-timeline` is a microservice that manages user's timeline the system. He is in charge of creating, deleting and changing posts in the user's timeline by interacting with MongoDB and Redis.

## Exposure
**This service is not exposed externally.**  

## Redis Listener
`repo-user-timeline` service listens events on Redis for these events:
- Create posts in user's timeline
- Like/Unlike posts in user's timeline
- Block users

## Environment Variables
**Quarkus Configuration**
```
HTTP_PORT:8091
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
repo-user-timeline/
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
