FROM openjdk:12.0.2

EXPOSE 80

ADD ./target/user-microservice-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
