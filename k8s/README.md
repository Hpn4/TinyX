# Kubernetes

All services and databases run under the same namespace: `tinyx`.

To deploy the entire setup:

```bash
kubectl apply -k .
```

---

# Architecture

```bash
.
├── README.md
├── configmap.yml
├── kustomization.yml
├── mongodb
├── namespace.yml
├── redis
├── secrets.yml
├── tinyx-home-timeline
├── tinyx-media
├── tinyx-post
├── tinyx-search
├── tinyx-social
├── tinyx-user
└── tinyx-user-timeline
```

Below is a diagram representing the global service architecture:

![services](assets/k8s.png)
