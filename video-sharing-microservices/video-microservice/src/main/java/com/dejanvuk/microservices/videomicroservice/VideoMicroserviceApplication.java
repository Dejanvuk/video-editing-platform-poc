package com.dejanvuk.microservices.videomicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.http.server.reactive.ContextPathCompositeHandler;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.server.HttpServer;

import java.util.HashMap;
import java.util.Map;

@EnableReactiveMongoRepositories
@SpringBootApplication
@ComponentScan("com.dejanvuk.microservices")
public class VideoMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VideoMicroserviceApplication.class, args);
    }

    @Profile("!junit")
    @Bean
    public NettyReactiveWebServerFactory nettyReactiveWebServerFactory() {
        NettyReactiveWebServerFactory webServerFactory = new NettyReactiveWebServerFactory() {
            @Override
            public WebServer getWebServer(HttpHandler httpHandler) {
                Map<String, HttpHandler> handlerMap = new HashMap<>();
                handlerMap.put("/api/v1", httpHandler);
                return super.getWebServer(new ContextPathCompositeHandler(handlerMap));
            }
        };
        webServerFactory.addServerCustomizers(portCustomizer());
        return webServerFactory;
    }

    public NettyServerCustomizer portCustomizer() {
        return new NettyServerCustomizer() {
            @Override
            public HttpServer apply(HttpServer httpServer) {
                return httpServer.port(80);
            }
        };
    }

}
