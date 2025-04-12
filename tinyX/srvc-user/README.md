# **Service `srvc-user`**

## Description
`srvc-user` is a microservice that manages users within the system. It is in charge of creating and fetching users. This service encapsulates the logic related to user management, including creating, updating, and retrieving user information from a MongoDB database.

The service is exposed via a REST API documented in Swagger.

## Exposure
**This service is exposed externally.**  
You can interact with its REST API through the following Swagger interface:  
**Swagger UI**: `http://localhost:8086/q/swagger-ui`

## Environment Variables
**Quarkus Configuration**
```
HTTP_PORT:8086
```

**To make Swagger UI look cool**
```
quarkus.swagger-ui.theme=original
quarkus.console.color=true
quarkus.log.console.json=false
quarkus.console.enabled=false
```

**Redis & MongoDB configuration**
```bash
REDIS_HOST: localhost 
REDIS_PORT: 6379
MONGODB_USER:admin
MONGODB_PASSWD:admin
MONGODB_HOST:localhost
MONGODB_PORT:27017
MONGODB_DATABASE:Tinyx
MONGODB_COLLECTION:Users
TRIM_INTERVAL:10m  #Time before delete all 
CLAIM_INTERVAL:5s  #Time before get data
```

## Redis Publisher
The `srvc-user` service publishes events on Redis to notify the following services:
- `repo-social`
- `repo-home-timeline`
- `repo-user-timeline`

The events published include:
- Create user

---


## Project Structure
```bash
srvc-user/
├── src/
│   └── main/
│       ├── java/com/tinyx/
│       │   ├── controller/          # REST endpoints
│       │   ├── repository/          # Data access (MongoDB)
│       │   └── service/             # Core logic
│       └── resources/
│           └── application.properties
├── Dockerfile
└── README.md
```
