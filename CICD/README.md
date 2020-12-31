
# Copy .kube/config to Jenkins pod first

```
kubectl cp ~/.kube/config jenkins-0:/var/lib/jenkins/config 
```

# Install Docker
```
apt-get update
```

```
apt-get install build-essential
```
```
apt-get install software-properties-common
```
```
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add -
```
```
add-apt-repository \
   "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
   $(lsb_release -cs) \
   stable"
```
```
apt-get install docker-ce -y
```

# Install Kubectl
```
curl -LO "https://storage.googleapis.com/kubernetes-release/release/$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/linux/amd64/kubectl"
```
```
chmod +x ./kubectl
```
```
mv ./kubectl /usr/local/bin/kubectl
```

# 
# 
# Build new images and push to ACR.
# 
#

```
TAG=$(echo ${GIT_COMMIT} | head -c 6)
```
```
USER_IMAGE_NAME="${ACR_LOGINSERVER}/azure-user-microservice:$TAG"
VIDEO_IMAGE_NAME="${ACR_LOGINSERVER}/azure-video-microservice:$TAG"
COMMENT_IMAGE_NAME="${ACR_LOGINSERVER}/azure-comment-microservice:$TAG"
```
```
docker build -t $USER_IMAGE_NAME ./video-sharing-microservices/User-Microservice
docker build -t $VIDEO_IMAGE_NAME ./video-sharing-microservices/video-microservice
docker build -t $COMMENT_IMAGE_NAME ./video-sharing-microservices/Comment-Microservice
```

```
docker login ${ACR_LOGINSERVER} -u ${ACR_ID} -p ${ACR_PASSWORD}
```

```
docker push $USER_IMAGE_NAME
docker push $VIDEO_IMAGE_NAME
docker push $COMMENT_IMAGE_NAME
```

# Deploy to AKS

```
USER_IMAGE_NAME="${ACR_LOGINSERVER}/azure-user-microservice:$TAG"
VIDEO_IMAGE_NAME="${ACR_LOGINSERVER}/azure-video-microservice:$TAG"
COMMENT_IMAGE_NAME="${ACR_LOGINSERVER}/azure-comment-microservice:$TAG"
```

```
kubectl patch deployment user --type='json' -p='[{"op": "replace", "path": "/spec/template/spec/containers/0/image", "value":$USER_IMAGE_NAME}]' --kubeconfig /var/lib/jenkins/config
```

```
kubectl patch deployment video --type='json' -p='[{"op": "replace", "path": "/spec/template/spec/containers/0/image", "value":$VIDEO_IMAGE_NAME}]' --kubeconfig /var/lib/jenkins/config
```

```
kubectl patch deployment comment --type='json' -p='[{"op": "replace", "path": "/spec/template/spec/containers/0/image", "value":$COMMENT_IMAGE_NAME}]' --kubeconfig /var/lib/jenkins/config
```






