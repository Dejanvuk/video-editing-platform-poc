#!/usr/bin/env bash

# Before executing this script use "az login" to login in Azure and then "az acr login --name <acrName>" to setup your docker config.json to acr, else it will fail

az group create --name <rgName> --location eastus

az acr create --resource-group myResourceGroup --name <acrName> --sku Basic

docker tag video-sharing-platform_user acrekstest.azurecr.io/azure-user-microservice:v1

docker tag video-sharing-platform_comment acrekstest.azurecr.io/azure-comment-microservice:v1

docker tag video-sharing-platform_video acrekstest.azurecr.io/azure-video-microservice:v1

docker push acrekstest.azurecr.io/azure-user-microservice:v1

docker push acrekstest.azurecr.io/azure-comment-microservice:v1

docker push acrekstest.azurecr.io/azure-video-microservice:v1

az aks create \
    --resource-group <rgName> \
    --name <aksName> \
    --node-count 3 \
    --generate-ssh-keys \
    --attach-acr <acrName>
