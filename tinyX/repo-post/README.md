# **Service `repo-post`**

## Description
`repo-post` is a microservice that manages posts within the system. He is in charge of creating and deleting posts from a MongoDB database.

## Exposure
**This service is not exposed externally.**  

## Redis Listener
`repo-post` service listens events on Redis for these events:
- Create & Delete posts

## Environment Variables
**Quarkus Configuration**
```
HTTP_PORT:8093
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
TRIM_INTERVAL:10m  #Time before delete all 
CLAIM_INTERVAL:5s  #Time before get data
```

---

## Project Structure
```bash
repo-post/
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
