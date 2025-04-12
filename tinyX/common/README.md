# **Service `common`**

## Description
The `common` module is not a runtime service, it acts more like a dependency for all the other services. It houses all resources that are shared between modules, like classes, utils and enumerations.  
Its usefulness comes clear regarding the database side of things: having the various entities, contracts and converters in one place is not only clean but simple to use; it also avoids code duplication.


---

## Project Structure
Regarding the architecture of the module, it was simplest and made more sense to group all resources based on their module/database.  
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

## Contents
### Entity classes and Others
The main goal of the common module was to remove duplicate code regarding all the classes used to manipulate data in the layered architecture.  
These classes, entities and converters are now in one place only, which makes it easy to reuse them from anywhere.  

### Utils
The common module seemed like an appropriate place to fit utility functionalities that were used in multiple places, especially for tests.  

### ErrorCodes
This class allowed to centralise endpoint error responses and make them easier to use. It establishes an enumeration of error 'codes' which can be used to return HTTP error messages in endpoints.
Not only is this system modular and simple to modify, the errors can be triggered from anywhere in the code, which makes the code a lot cleaner.
