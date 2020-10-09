package com.dejanvuk.microservices.commentmicroservice;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@EnableReactiveMongoRepositories
@SpringBootApplication
@ComponentScan("com.dejanvuk.microservices")
public class CommentApplication {
	public static void main(String[] args) {
		SpringApplication.run(CommentApplication.class, args);
	}

}
