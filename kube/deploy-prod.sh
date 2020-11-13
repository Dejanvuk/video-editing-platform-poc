#!/usr/bin/env bash

# A helper script to create the Kubernetes resources

# ACR_LOGIN_SERVER=$ACR_LOGIN_SERVER
: ${ACR_LOGIN_SERVER=acrekstest.azurecr.io}

# Print all commands and stop executing if one fails

set -ex

# Create and setup the namespace for all the cluster's resources

kubectl create namespace namespace-myakscluster-prod

kubectl config set-context --current --namespace=namespace-myakscluster-prod

# Store application configurations in configmaps for each service

kubectl create configmap configmap-comment           --from-file=resources-config/application.yml --from-file=resources-config/comment.yml --save-config
kubectl create configmap configmap-user    --from-file=resources-config/application.yml --from-file=resources-config/user.yml --save-config
kubectl create configmap configmap-video           --from-file=resources-config/application.yml --from-file=resources-config/video.yml --save-config

# The username and passwords to access various remote services are stored in secrets

kubectl create secret generic rabbitmq-credentials \
    --from-literal=SPRING_RABBITMQ_USERNAME=rabbit-user \
    --from-literal=SPRING_RABBITMQ_PASSWORD=rabbit-password \
    --save-config

kubectl create secret generic mongodb-credentials \
    --from-literal=SPRING_DATA_MONGODB_AUTHENTICATION_DATABASE=admin \
    --from-literal=SPRING_DATA_MONGODB_USERNAME=mongodb-user \
    --from-literal=SPRING_DATA_MONGODB_PASSWORD=mongodb-password \
    --save-config

# Deploy a NGINX ingress controller in the AKS cluster (requires Helm 3+) if not present already

helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx

helm install nginx-ingress ingress-nginx/ingress-nginx \
    --namespace namespace-myakscluster-prod \
    --set controller.replicaCount=2 \
    --set controller.nodeSelector."beta\.kubernetes\.io/os"=linux \
    --set defaultBackend.nodeSelector."beta\.kubernetes\.io/os"=linux \
    --set controller.admissionWebhooks.patch.nodeSelector."beta\.kubernetes\.io/os"=linux

# Deploy all the resources

kubectl apply -f overlays/prod

kubectl wait --timeout=500s --for=condition=ready pod --all

set +ex

