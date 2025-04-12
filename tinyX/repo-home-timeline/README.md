# Service `repo-home-timeline`
## Description
This service is used to add, delete and modify user timelines of users followed by a querying user that are stocked in a MongoDB database.

## Exposure
**This service is exposed externally.**  
You can interact with its REST API through the following Swagger interface:  
**Swagger UI:** http://localhost:8091/q/swagger-ui

## Redis Listener
repo-home-timeline receive queries from Redis:
- Follow & unfollow
- Create & delete user

## Environment Variables

**Quarkus Configuration**
```
HTTP_PORT:8091
```

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
MONGODB_PORT:27017
MONGODB_DATABASE:Tinyx
```

**Other Variables**
```
TRIM_INTERVAL:10m
CLAIM_INTERVAL:5s
```


## Database
For each user of the database, there is a list of all the id of the users followed by this user. This list is modify depending on if the user as followed a new user or unfollow another one.

## Structutre

```bash
repo-home-timeline/
├── src/
│   └── main/
│       ├── java/com/tinyx/
│       │   ├── controller/          # RedisStreamReader extensions
│       │   ├── repository/          # Access and modification of the MongoDB database
│       │   └── service/             # Core Logic
│       └── resources/
│           └── application.properties
├── pom.xml
└── README.md
```


