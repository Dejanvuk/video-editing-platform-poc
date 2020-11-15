package com.dejanvuk.microservices.videomicroservice.config;

import com.mongodb.reactivestreams.client.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.server.reactive.ContextPathCompositeHandler;
import org.springframework.http.server.reactive.HttpHandler;
import reactor.netty.http.server.HttpServer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ReactiveMongoConfig {
    @Autowired
    MongoClient mongoClient;

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate() {
        return new ReactiveMongoTemplate(mongoClient, "comment-db");
    }

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
