package com.dejanvuk.microservices.user.config;

import com.dejanvuk.microservices.user.payload.LoginPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.AbstractJackson2Decoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
public class CustomServerAuthenticationConverter implements ServerAuthenticationConverter {

    @Autowired
    AbstractJackson2Decoder jacksonDecoder;

    @Override
    public Mono<Authentication> convert(ServerWebExchange serverWebExchange) {
        return getUsernameAndPassword(serverWebExchange)
                .switchIfEmpty(Mono.error(new RuntimeException("Failed to cast LoginPayload!"))).
                map(e -> new UsernamePasswordAuthenticationToken(e.getUsernameOrEmail(), e.getPassword()));
    }

    private Mono<LoginPayload> getUsernameAndPassword(ServerWebExchange exchange) {
        var dataBuffer = exchange.getRequest().getBody();
        return jacksonDecoder
                .decodeToMono(dataBuffer, ResolvableType.forClass(LoginPayload.class), MediaType.APPLICATION_JSON, Collections.<String, Object>emptyMap())
                .onErrorResume(throwable -> Mono.empty())
                .cast(LoginPayload.class);
    }
}
