#!/bin/bash

set -e  # Exit immediately if a command exits with a non-zero status

# Colors for pretty output
GREEN="\033[0;32m"
BLUE="\033[1;34m"
RESET="\033[0m"

divider() {
  echo -e "${BLUE}--------------------------------------------------${RESET}"
}

section() {
  echo -e "\n${GREEN}➤ $1...${RESET}"
  divider
}

kubectl create ns devops-tools

section "Deploying Jenkins"

kubectl apply -f jenkins/pv.yaml
kubectl apply -f jenkins/sa-master.yaml
kubectl apply -f jenkins/sa-agent.yaml
kubectl apply -f jenkins/deploy.yaml
kubectl apply -f jenkins/svc-internal.yaml
kubectl apply -f jenkins/svc-nodeport.yaml

echo -e "${GREEN}✔ Jenkins deployed${RESET}"

section "Deploying Python app"

kubectl apply -f python-app/deploy.yaml
kubectl apply -f python-app/svc-nodeport.yaml
kubectl apply -f python-app/svc-internal.yaml

echo -e "${GREEN}✔ Python app deployed${RESET}"

section "Deploying Prometheus and Grafana"

helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update
helm install prometheus prometheus-community/prometheus -n devops-tools

kubectl apply -f prometheus/svc-nodeport.yaml
kubectl apply -f prometheus/svc-monitor.yaml
kubectl apply -f grafana/svc-nodeport.yaml

echo -e "\n${GREEN}🎉 All resources have been successfully applied!${RESET}"