# video-editing-platform-poc

## About The Project

a simple video editor platform proof of concept
<ol>
<li><a href="https://github.com/Dejanvuk/video-editing-platform-poc/tree/master/video-sharing-microservices/README.md">Backend Microservices</a></li>
<li><a href="https://github.com/Dejanvuk/video-editing-platform-poc/tree/master/video-sharing-frontend/README.md">Frontend UI</a></li>
<li><a href="https://github.com/Dejanvuk/video-editing-platform-poc/tree/master/kube/README.md">K8 Setup</a></li>
</ol>

Work in progress

## Contributing
1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Reques

## TO DO
* Use Spring's local cache to save the top rated videos and comments of a video and back-up up with a remote cache provider Redis with replicas
* Sign out functionality: Use Redis to store the invalid jwt's 
* Clients should cache the top videos for an hour
* Add Dev and Test Overlays in Kustomization
* Upload and Backup the videos to AWS S3 / Azure Storage
* Add Lamba/Azure Functions to resize the uploaded videos and profile pictures
* Add PWA support to the Frontend App

##  Built With

* [ReactJs](https://reactjs.org/)
* [SpringBoot](https://spring.io/projects/spring-boot)
* [Kubernetes](https://kubernetes.io/)

No extra UI library on the frontend because of the CPU and RAM intensive nature of video editing
