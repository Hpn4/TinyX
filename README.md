# TinyX

TinyX is a proof-of-concept (POC) of a Twitter-like social network, developed in Java using the Quarkus framework.

# Architecture

```bash
├── README.md      # Project overview
├── assets         # Images for documentation
├── docker         # Docker Compose setup
├── k8s            # Kubernetes deployment manifests
└── tinyX          # Source code for all services
```

# Installation

## Compile the Project

This will run tests (requires databases to be up):
```bash
mvn clean install
```

To skip tests:
```bash
mvn clean install -DskipTests
```

## Pre-commit Setup

```bash
pip install pre-commit
pre-commit install
```

## Java Linter (Spotless)

To check for formatting issues:
```bash
mvn spotless:check           # Check all modules
mvn -pl srvc-search spotless:check  # Check a specific module
```

To automatically fix formatting:
```bash
mvn spotless:apply           # Fix all modules
mvn -pl srvc-search spotless:apply  # Fix a specific module
```

# Testing

You can try the project by either:
- Running the services locally
- Pulling prebuilt images from the [container registry](https://gitlab.cri.epita.fr/ing/majeures/tc/info/student/2026/2025-epitweet-tinyx-14/container_registry)

The `docker` folder contains a `docker-compose.yml` file that launches all required databases.

# Documentation

Each service is documented individually.

A detailed explanation of the architecture, design choices, service roles, and CI/CD pipelines is available in the project wiki.

Read the architecture documentation [here](https://gitlab.cri.epita.fr/ing/majeures/tc/info/student/2026/2025-epitweet-tinyx-14/-/wikis/Architecture).
