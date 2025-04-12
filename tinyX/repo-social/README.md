# Service `repo-social`

## Summary
`repo-social` is the service that is use to modify the Neo4j database that compile the social interactions between the users and their posts.

## Exposure
**This service is not exposed externally.**  

## Redis Listener

repo-social receive queries from Redis:
- Create & Delete Post
- Create & Delete User
- Follow & Unfollow
- Block & Unblock 
- Like & Unlike

## Redis Publisher
`repo-social` service publishes events on Redis to notify the following services:
- repo-social

The events published include:
- Unfollow

## Environment Variables

**Quarkus Configuration**
```
HTTP_PORT:8090
```

**Swagger UI**
```
quarkus.swagger-ui.theme=original
quarkus.console.color=true
quarkus.log.console.json=false
```
**Redis and Neo4j configuration**
```
NEO4J_HOST:localhost
NEO4J_CONNECTION_STRING:bolt
NEO4J_PORT:7687
REDIS_CONNECTION_STRING:redis
REDIS_HOST:localhost
REDIS_PORT:6379
```
**Other variables**
```
TRIM_INTERVAL:10m
CLAIM_INTERVAL:5s
```
## Database Neo4j
### Nodes

#### User
A unique user of Tinyx, identified with an id.

#### Post
A unique post published by a user, identified with an id and contains the id of the author.

### Relation

#### LIKE (User->Post)  
Unidirectional relation between a User and a Post, where an existing User liked an existing Post.

#### FOLLOW (User->User)
Unidirectional relation between two Users, where an existing User decide to follow another existing User.

#### BLOCK (User->User)
Unidirectional relation between txo Users, where an existing User decide to block another existing User.
When the relation is initialized, all existing FOLLOW and LIKE relations between the two Users and their Posts are deleted.
If there is a BLOCK relation between two Users, no LIKE or FOLLOW relations can be created between those Users and their Posts.

The modifications of the neo4j database are made by using two repositories, one for the nodes User and Post (SocialRepository) and the other for the LIKE, FOLLOW and BLOCK relationships (RelationsRepository).



## Structure
```bash
repo-social/
├── src/
│   └── main/
│       ├── java/com/tinyx/
│       │   ├── controller/         # RedisStreamReader extensions
│       │   ├── converter/          # Enable conversion between query and entity
│       │   ├── service/            # Core logic
│       │   └── repository/         # Data access and modification (Neo4j/Redis)
│       │        └── entity         # Modelisation of database data
│       └── resources/
│           └── application.properties
├── Dockerfile
├── pom.xml
└── README.md
```

