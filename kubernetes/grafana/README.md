# Grafana

Provides alerts for CPU and Memory Utilization(%).

## How to setup

For unit in visualisations select `Percent (0-100)`.

### Queries for container

CPU Utilization percentage:

```
100 * (
  sum(rate(container_cpu_usage_seconds_total{pod=~"python-app-test-deploy-.*"}[5m]))
  /
  sum(kube_pod_container_resource_limits{pod=~"python-app-test-deploy-.*", resource="cpu", unit="core"})
)
```

Memory Utilization percentage:

```
100 * (
  sum(container_memory_usage_bytes{pod=~"python-app-test-deploy.*"})
  /
  sum(kube_pod_container_resource_limits{pod=~"python-app-test-deploy-.*", resource="memory", unit="byte"})
)
```

### Queries for Kubernetes Node

CPU Utilization percentage:

```
100 * (1 - avg(rate(node_cpu_seconds_total{mode="idle"}[1m])) by (instance))
```

Memory Utilization percentage:

```
100 * (1 - (node_memory_MemAvailable_bytes / node_memory_MemTotal_bytes))
```

### Alerts

When creating alert for panel in 'Define query and alert condition' set the following expressions:
- A(query): leave without changes
- B(Reduce):
    - Input: A
    - Function: Last
    - Mode: Strict
- C(Threshol):
    - Input: B
    - Is bove: 80
