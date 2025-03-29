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
