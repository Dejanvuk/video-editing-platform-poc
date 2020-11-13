#!/usr/bin/env bash

# Before executing this script use "az login" to login in Azure else it will fail

# Print all commands and stop executing if one fails

set -ex

: ${RG_NAME=myResourceGroup}
: ${ACR_NAME=acrEksTest}
: ${ACR_LOGIN_SERVER=acrekstest.azurecr.io}

az group create --name $RG_NAME --location eastus

az acr create --resource-group myResourceGroup --name $ACR_NAME --sku Basic

# Create the Kubernetes cluster

az aks create \
    --resource-group $RG_NAME \
    --name ACR_NAME \
    --node-count 3 \
    --generate-ssh-keys \
    --attach-acr $ACR_NAME

set +ex
