# Service `srvc-media`

## Description
This service exposes a REST API to get information from a MongoDB database containing medias.  
Medias can be any chain of bytes and can represent images, text, videos, sound, anything really.  
  
Only read operations are available in this service.  

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
The database used to store these medias is mongodb, using the GridFS driver. For more information, check out the [repo-media](https://gitlab.cri.epita.fr/ing/majeures/tc/info/student/2026/2025-epitweet-tinyx-14/-/blob/main/tinyX/repo-media/README.md) page.  

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

