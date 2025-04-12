# TinyX

This repository is a monorepo containing 13 services.
Below is a simplified schema illustrating the global architecture:

![Services](assets/service.png)

---

# Services

```bash
├── README.md
├── common              # Shared code: converters, contracts, utilities
├── pom.xml
├── repo-home-timeline
├── repo-media
├── repo-post
├── repo-search
├── repo-social
├── repo-user-timeline
├── srvc-home-timeline
├── srvc-media
├── srvc-post
├── srvc-search
├── srvc-social
├── srvc-user
├── srvc-user-timeline
└── testsuite           # Load testing and functional test suite
```

Each service has its own detailed `README` for usage and implementation notes.
