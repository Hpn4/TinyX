# **Service `common`**

## Description
`common` is a service that manages all others microservices. He is in charge of stocking all enum, utils for databases, and contracts, entity and converter for each entity.

---

## Project Structure
```bash
common/
├── src/
│   └── main/
│       ├── java/com/tinyx/
│       │   ├── ErrorCodes.java   # Enum of Status
│       │   ├── codec/            # Zoned Date Time
│       │   ├── home.entity/      # Home Timeline Mongo Entity
│       │   ├── media/            # Contracts, Entity, Converter of Medias
│       │   ├── mongo/            # Utils for MongoDB
│       │   ├── post/             # Contracts, Entity, Converter of Posts
│       │   ├── redis/            # Utils for Redis
│       │   ├── search.entity/    # Search Entity
│       │   ├── timeline/         # Contracts, Entity, Converter of Timelines
│       │   └── user/             # Contracts, Entity, Converter of Users
│       └── resources/META-INF
│           └── beans.xml
└── README.md
```
