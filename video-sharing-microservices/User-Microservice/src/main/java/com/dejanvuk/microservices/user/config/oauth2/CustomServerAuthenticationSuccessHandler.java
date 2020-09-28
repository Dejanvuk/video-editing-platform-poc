package com.dejanvuk.microservices.user.config.oauth2;


import com.dejanvuk.microservices.user.config.CustomUserDetails;
import com.dejanvuk.microservices.user.payload.SignUpPayload;
import com.dejanvuk.microservices.user.persistence.RoleRepository;
import com.dejanvuk.microservices.user.persistence.RoleType;
import com.dejanvuk.microservices.user.persistence.UserEntity;
import com.dejanvuk.microservices.user.persistence.UserRepository;
import com.dejanvuk.microservices.user.services.UserService;
import com.dejanvuk.microservices.user.utility.CookieUtility;
import com.dejanvuk.microservices.user.utility.JwtTokenUtility;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.SerializationUtils;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

/*

@Component
public class CustomServerAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomServerAuthenticationSuccessHandler.class);

    @Autowired
    CookieUtility cookieUtility;

    public static final String REDIRECT_COOKIE = "redirect_uri";
    public static final String OAUTH2_AUTHORIZATION_COOKIE = "oauth2_authorization_cookie";

    @Value("${jwt.TOKEN_PREFIX}")
    private String TOKEN_PREFIX;

    @Autowired
    private JwtTokenUtility jwtTokenUtility;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        log.info("inside onAuthenticationSuccess {}", authentication.getClass());

        if (authentication instanceof OAuth2AuthenticationToken) {
            ServerHttpRequest request = webFilterExchange.getExchange().getRequest();
            MultiValueMap<String, HttpCookie> cookieMap = request.getCookies();

            log.info("OAuth2AuthenticationToken {}", authentication);

            String redirectUri = cookieUtility.getCookieValue(cookieMap, REDIRECT_COOKIE);

            log.info("authentication.getPrincipal():=== {}", authentication.getPrincipal());
            log.info("authentication.getPrincipal class {}", authentication.getPrincipal().getClass());

            ServerHttpResponse response = webFilterExchange.getExchange().getResponse();

            if (response.isCommitted()) {
                log.debug("Response has already been committed. Unable to redirect to " + redirectUri);
                return Mono.empty();
            }

            // save the oauth2 user in database
            DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

            Map<String, Object> userAttributes = defaultOAuth2User.getAttributes();

            userRepository.existsByEmail((String)userAttributes.get("email")).flatMap(exists -> {
                OAuth2UserAttributes oAuth2UserAttributes = OAuth2ProviderFactory.getOAuth2UserAttributesByProvider((OAuth2AuthenticationToken) authentication, userAttributes);

                String jwt = TOKEN_PREFIX + " " + jwtTokenUtility.generateToken(oAuth2UserAttributes.getEmail());

                String location = UriComponentsBuilder.fromUriString(redirectUri).queryParam("jwt", jwt).build().toUriString();

                // clear authentication attributes and cookies are no longer needed
                cookieUtility.deleteCookie(response, cookieMap, REDIRECT_COOKIE);
                cookieUtility.deleteCookie(response, cookieMap, OAUTH2_AUTHORIZATION_COOKIE);

                response.setStatusCode(HttpStatus.PERMANENT_REDIRECT);
                response.getHeaders().setLocation(URI.create(location));



                if(!exists) {
                    // Register the new user
                    UserEntity userEntity = new UserEntity();
                    userEntity.setName(oAuth2UserAttributes.getName());
                    // use email but make the user input the username
                    userEntity.setUsername(oAuth2UserAttributes.getEmail());
                    userEntity.setEmail(oAuth2UserAttributes.getEmail());
                    userEntity.setImageUrl(oAuth2UserAttributes.getPictureUrl());
                    userEntity.setProviderType(((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId());

                    userEntity.setToken(null);
                    userEntity.setVerified(true);

                    roleRepository.findByName(RoleType.ROLE_USER).flatMap(role -> {
                        userEntity.setRoles(Collections.singleton(role));
                        System.out.println(userEntity);
                        return userRepository.save(userEntity);
                    });

                    return Mono.empty();
                }
                else { // User already exists
                    return Mono.empty();
                }
            });

        }

        return Mono.empty();
    }
}

*/
