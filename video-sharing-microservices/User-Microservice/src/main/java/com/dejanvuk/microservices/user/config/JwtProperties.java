package com.dejanvuk.microservices.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

@Primary
@Configuration
@PropertySource("classpath:jwtprops.yml")
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {
    private String JWT_SECRET;
    private String TOKEN_HEADER;
    private String TOKEN_PREFIX;
    private String TOKEN_TYPE;
    private Long EXPIRATION_TIME;
    private String TOKEN_ISSUER;
    private String TOKEN_AUDIENCE;
}
