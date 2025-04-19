# Kubernetes Deployment


## Process

Explanation of the commands used in `deploy.sh` script:

- `kubectl create ns devops-tools` - Creates  Kubernetes namespace called 'devops-tools'
- **Jenkins related commands:**
    - `kubectl apply -f pv.yaml`                - Creates Persistent Volume and Persistent Volume Claim for Jenkins
    - `kubectl apply -f sa-master.yaml`         - Creates SA (Service Account), Cluster Role and Cluster Role Binding for Jenkins Master Node
    - `kubectl apply -f sa-agent.yaml`          - Creates SA, Cluster Role and Cluster Role Binding for Jenkins Worker Node
    - `kubectl apply -f deploy.yaml`            - Creates Deployment for Jenkins Master Node
    - `kubectl apply -f svc-internal.yaml`      - Creates Service of type ClusterIP for internal Jenkins communication
    - `kubectl apply -f svc-nodeport.yaml`      - Creates Service of type NodePort to expose the Jenkins UI outside the cluster
- **Python application related commands:**
    - `kubectl apply -f python-app/deploy.yaml`         - Creates Deployment for Python Application which runs intial(created manually) docker image from my dockerhub public repository
    - `kubectl apply -f python-app/svc-internal.yaml`   - Creates Service of type ClusterIP for internal Python Application communication
    - `kubectl apply -f python-app/svc-nodeport.yaml`   - Creates Service of type NodePort to expose Python Application outside the cluster
- **Prometheus and Grafana related commands:**
    - `helm repo add prometheus-community https://prometheus-community.github.io/helm-charts` - Add the Prometheus Community Helm charts repository
    - `helm repo update`                                                                      - Update local Helm chart list to fetch the latest versions
    - `helm install prometheus prometheus-community/prometheus -n devops-tools`               - Install Prometheus into the 'devops-tools' namespace using the Helm chart
    - `kubectl apply -f prometheus/svc-monitor.yaml`                                          - Custom resource from the Prometheus Operator, used to tell Prometheus how to discover and scrape metrics from Python Application service.
    - `kubectl apply -f prometheus/svc-nodeport.yaml`                                         - Creates Service of type NodePort to expose Prometheus UI outside the cluster
    - `kubectl apply -f grafana/svc-nodeport.yaml`                                            - Creates Service of type NodePort to expose Grafana UI outside the cluster