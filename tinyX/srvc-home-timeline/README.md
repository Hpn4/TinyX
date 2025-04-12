# Service: `srvc-home-timeline`

## Description
This service is use to get a list of all the post made by a specific user, in chronological order, by checking a MongoDB database.

## Exposure
**This service is exposed externally.**  
You can interact with its REST API through the following Swagger interface:  
**Swagger UI:** http://localhost:8082/q/swagger-ui

## Environment Variables

**Swagger UI**
```
quarkus.swagger-ui.theme=original
quarkus.log.console.json=false
quarkus.console.color=true
quarkus.console.enabled=false
```

**Redis and MongoDB configuration**
```
REDIS_CONNECTION_STRING:redis
REDIS_HOST:localhost
REDIS_PORT:6379
MONGODB_CONNECTION_STRING:mongodb
MONGODB_USER:admin
MONGODB_PASSWD:admin
MONGODB_HOST:localhost
MONGODB_DATABASE:Tinyx
```

**External Services URLs**
```
REST_HOST:localhost
REST_PORT:8081
```

## Database
For each user of the database, there is a list of all the id of the users followed by this user. This list is modify depending on if the user as followed a new user or unfollow another one.

## Structure
```bash
srvc-home-timeline/
├── src/
│   └── main/
│       ├── java/com/tinyx/
│       │   ├── controller/          # Endpoints
│       │   ├── repository/          # Data access (MongoDB)
│       │   └── service/             # Core logic
│       └── resources/
│           └── application.properties
├── Dockerfile
├── pom.xml
└── README.md
```
