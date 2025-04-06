# TinyX


# Installation
## Pre-commit
```
pip install pre-commit
pre-commit install
```
## Linter JAVA

In order to run checks you must use
```
mvn spotless:check # or mvn -pl srvc-search spotless:check
```
To fix linter's errors
```
mvn spotless:apply # or mvn -pl srvc-search spotless:apply
```

## Variables

```
export MONGODB_CONNECTION_STRING="mongodb://admin:admin@localhost:27017/?authSource=admin&retryWrites=true&w=majority&uuidRepresentation=STANDARD"
export REDIS_CONNECTION_STRING="redis://localhost:6379"
export NEO4J_CONNECTION_STRIN="bolt://localhost:7687"
```
