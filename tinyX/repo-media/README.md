# Service: `repo-media`

## Description
The service `repo-media` is used to manage media in the MongoDB database. This service expose API REST to interact with the users to upload a media that will be assigned to their posts.

This service also interact with other services by receiving PostQuery using Redis.

## Exposure
**This service is exposed externally.**  
You can interact with its REST API through the following Swagger interface:  
**Swagger UI:** http://localhost:8088/q/swagger-ui

## Redis Listener
repo-media receive queries from Redis:
- Create & Delete posts

## Environment Variable

**Swagger UI**
```
quarkus.swagger-ui.theme=original
quarkus.console.color=true
quarkus.log.console.json=false
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

**Other Variables**
```
TRIM_INTERVAL:10m
CLAIM_INTERVAL:5s
MEDIA_BUCKET:Media
MAX_MEDIA_SIZE:32M
```

## Database
It's a MongoDB database named "quarkus.mongodb.database" and it managed medias by using a GridFSBucket a that stock their id, InputStream and the list of the post's id associated to them.

## Structure
```bash
repo-media/
├── src/
│   └── main/
│       ├── java/com/tinyx/
│       │   ├── controller/          # Rest API endpoints and RedisStreamReader extensions
│       │   ├── repository/          # Access and modification of the MongoDB database
│       │   └── service/             # Core logic
│       └── resources/
│           └── application.properties
├── Dockerfile
├── pom.xml
└── README.md
```
