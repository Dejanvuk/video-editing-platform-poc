package com.dejanvuk.microservices.user.config.oauth2;

import com.dejanvuk.microservices.user.utility.CookieUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.oauth2.client.web.server.ServerAuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.SerializationUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Base64;

@Service
public class CustomServerAuthorizationRequestRepository implements ServerAuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    @Value("${app.OAUTH2_AUTHORIZATION_COOKIE}")
    private String OAUTH2_AUTHORIZATION_COOKIE;

    @Value("${app.REDIRECT_COOKIE}")
    private String REDIRECT_COOKIE;

    public static final int COOKIE_AGE = 120;

    @Autowired
    private CookieUtility cookieUtility;

    @Override
    public Mono<OAuth2AuthorizationRequest> loadAuthorizationRequest(ServerWebExchange serverWebExchange) {
        MultiValueMap<String, HttpCookie> cookieMap = serverWebExchange.getRequest().getCookies();

        if (cookieMap != null) {
            String oauth2CookieValue = cookieUtility.getCookieValue(cookieMap, OAUTH2_AUTHORIZATION_COOKIE);

            return Mono.just(OAuth2AuthorizationRequest.class
                    .cast(SerializationUtils.deserialize(Base64.getUrlDecoder().decode(oauth2CookieValue))));
        } else {
            return Mono.empty();
        }
    }

    @Override
    public Mono<Void> saveAuthorizationRequest(OAuth2AuthorizationRequest oAuth2AuthorizationRequest, ServerWebExchange serverWebExchange) {
        if (oAuth2AuthorizationRequest == null) {
            MultiValueMap<String, HttpCookie> cookieMap = serverWebExchange.getRequest().getCookies();

            if (cookieMap != null) {
                ServerHttpResponse response = serverWebExchange.getResponse();
                cookieUtility.deleteCookie(response, cookieMap, OAUTH2_AUTHORIZATION_COOKIE);
                cookieUtility.deleteCookie(response, cookieMap, REDIRECT_COOKIE);
            }
            return Mono.empty();
        } else {
            ServerHttpResponse response = serverWebExchange.getResponse();
            cookieUtility.addCookie(response, OAUTH2_AUTHORIZATION_COOKIE, Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(oAuth2AuthorizationRequest)), "", COOKIE_AGE);
            cookieUtility.addCookie(response, REDIRECT_COOKIE, serverWebExchange.getRequest().getQueryParams().getFirst(REDIRECT_COOKIE), "", COOKIE_AGE);

            return Mono.empty();
        }
    }

    @Override
    public Mono<OAuth2AuthorizationRequest> removeAuthorizationRequest(ServerWebExchange serverWebExchange) {
        return this.loadAuthorizationRequest(serverWebExchange);
    }
}
