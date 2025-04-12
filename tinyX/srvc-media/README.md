# Service `srvc-media`

## Description
This service communicate with a MongoDB database and expose API REST to interact with the users in order to check if a media with a specific id exist or to retrieve its InputStream.

## Exposure
**This service is exposed externally.**  
You can interact with its REST API through the following Swagger interface:  
**Swagger UI**: http://localhost:8087/q/swagger-ui

## Environment Variables

**Swagger UI**
```
quarkus.swagger-ui.theme=original
quarkus.console.color=true
quarkus.log.console.json=false
 ```
**Redis and MongoDB configuration**
```
MONGODB_DATABASE:Tinyx
REDIS_CONNECTION_STRING:redis
REDIS_HOST:localhost
REDIS_PORT:6379
MONGODB_CONNECTION_STRING:mongodb
MONGODB_USER:admin
MONGODB_PASSWD:admin
MONGODB_HOST:localhost
```
**Other Variables**
```
MEDIA_BUCKET:Media
MAX_MEDIA_SIZE:32M
```
## Database
It's a MongoDB database named "quarkus.mongodb.database" and it managed medias by using a GridFSBucket a that stock their id, InputStream and the list of the post's id associated to them.

## Structure
```bash
srvc-media/
├── src/
│   └── main/
│       ├── java/com/tinyx/
│       │   ├── controller/          # Rest-API Endpoints
│       │   ├── repository/          # Data access (MongoDB)
│       │   └── service/             # Core logic
│       └── resources/
│           └── application.properties
├── Dockerfile
├── pom.xml
└── README.md
```

