#!/usr/bin/env bash

# A helper script to create the Kubernetes resources

# ACR_LOGIN_SERVER=$ACR_LOGIN_SERVER
: ${ACR_LOGIN_SERVER=acrekstest.azurecr.io}

: ${AKS_NAMESPACE=myakscluster-prod}

# Print all commands and stop executing if one fails

set -ex

# Create and setup the namespace for all the cluster's resources

kubectl create namespace $AKS_NAMESPACE

kubectl config set-context --current --namespace=$AKS_NAMESPACE

# Store application configurations in configmaps for each service

kubectl create configmap configmap-user    --from-file=resources-config/application.yml --from-file=resources-config/user.yml --save-config

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

kubectl create secret generic mongodb-server-credentials \
    --from-literal=MONGO_INITDB_ROOT_USERNAME=mongodb-user \
    --from-literal=MONGO_INITDB_ROOT_PASSWORD=mongodb-password \
    --save-config

# Deploy all the resources

kubectl apply -f deploy-db-stateful.yml
sleep 30s
kubectl apply -f user.yml

kubectl wait --timeout=500s --for=condition=ready pod --all

set +ex

