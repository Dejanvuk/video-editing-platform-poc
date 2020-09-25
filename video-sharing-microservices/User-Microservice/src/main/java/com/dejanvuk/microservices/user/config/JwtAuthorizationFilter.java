package com.dejanvuk.microservices.user.config;

import com.dejanvuk.microservices.user.utility.JwtTokenUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthorizationFilter implements WebFilter {

    @Value("${jwt.TOKEN_PREFIX}")
    private String TOKEN_PREFIX;

    @Autowired
    private JwtTokenUtility jwtTokenUtility;



    private static final Logger log = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        log.info("Request {} called", serverWebExchange.getRequest().getPath().value());
        String authorizationHeader = serverWebExchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if(StringUtils.isEmpty(authorizationHeader) || !(authorizationHeader.startsWith(TOKEN_PREFIX))) {
            return webFilterChain.filter(serverWebExchange);
        }

        return jwtTokenUtility.getUsernamePasswordAuthenticationTokenFromJwt(authorizationHeader).flatMap(auth -> {
            log.info("setting the reactive context");

            return webFilterChain.filter(serverWebExchange).subscriberContext(c -> ReactiveSecurityContextHolder.withAuthentication(auth)).onErrorResume(AuthenticationException.class, e -> {
                log.error("Authentication Exception", e);
                return webFilterChain.filter(serverWebExchange);
            });

        });
    }
}
