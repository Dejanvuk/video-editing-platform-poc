package com.dejanvuk.microservices.videomicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.web.reactive.function.client.WebClient;

@EnableReactiveMongoRepositories
@SpringBootApplication
@ComponentScan("com.dejanvuk.microservices")
public class VideoMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VideoMicroserviceApplication.class, args);
    }

}
