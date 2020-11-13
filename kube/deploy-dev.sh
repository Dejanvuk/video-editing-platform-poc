#!/usr/bin/env bash

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

set +ex

