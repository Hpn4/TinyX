# Service: `repo-media`

## Description
The service `repo-media` is used to insert medias in a MongoDB database. This service exposes a REST API that allows to upload a media that will be assigned to their posts. Medias are represented as a chain of bytes.  
This service also interacts with other services by receiving Queries using Redis. It allows medias to be linked to posts; when a media is no longer linked, it will be signalled to be deleted.  


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
This service uses mongodb to store medias, more precisely the GridFS tool. GridFS is powerful for storing large files  
in quite a simple manner. By handling not usual mongodb `collections`, but a `bucket`, you can store big files in two  
'hidden' collections: the 'files' collection stores the metadata related to an object, and the 'chunks' collection  
holds the raw data. The `bucket` simply allows to access those two collections seamlessly.  

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
