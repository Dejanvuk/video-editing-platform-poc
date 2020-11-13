#!/usr/bin/env bash

# Tag and push the images to the acr repository
# Before running the script use "az acr login --name <acrName>" to setup your docker config.json to azure's acr, else it will fail

# ACR_LOGIN_SERVER=$ACR_LOGIN_SERVER
: ${ACR_LOGIN_SERVER=acrekstest.azurecr.io}

# The tag for production will be the unique and the first letters of the git commit hash

TAG=$(git log -1 --pretty=%h)

docker tag video-sharing-platform_user $ACR_LOGIN_SERVER/azure-user-microservice:$TAG

docker tag video-sharing-platform_comment $ACR_LOGIN_SERVER/azure-comment-microservice:$TAG

docker tag video-sharing-platform_video $ACR_LOGIN_SERVER/azure-video-microservice:$TAG

docker push $ACR_LOGIN_SERVER/azure-user-microservice:$TAG

docker push $ACR_LOGIN_SERVER/azure-comment-microservice:$TAG

docker push $ACR_LOGIN_SERVER/azure-video-microservice:$TAG
