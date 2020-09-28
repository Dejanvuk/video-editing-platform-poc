package com.dejanvuk.microservices.user.config.oauth2;

import com.dejanvuk.microservices.user.config.JwtAuthorizationFilter;
import com.dejanvuk.microservices.user.persistence.RoleRepository;
import com.dejanvuk.microservices.user.persistence.UserRepository;
import net.minidev.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

/*
@Service
public class CustomReactiveOAuth2UserService implements ReactiveOAuth2UserService<OidcUserRequest, OidcUser> {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    private static final Logger log = LoggerFactory.getLogger(CustomReactiveOAuth2UserService .class);

    private final OidcReactiveOAuth2UserService service = new OidcReactiveOAuth2UserService();

    @Override
    public Mono<OidcUser> loadUser(OidcUserRequest oidcUserRequest) throws OAuth2AuthenticationException {
        log.info("saving oidc user to database");

        Mono<OidcUser> mOidcUser = service.loadUser(oidcUserRequest);

        return mOidcUser
                .log()
                .cast(DefaultOidcUser.class)
                .map(DefaultOidcUser::getClaims)
                .flatMapIterable(Map::entrySet)
                .flatMapIterable(roleEntry -> (JSONArray) roleEntry.getValue())
                .map(roleString -> {
                    log.debug("roleString={}", roleString);
                    return new OidcUserAuthority((String) roleString, oidcUserRequest.getIdToken(), null);
                })
                .collect(Collectors.toSet())
                .map(authorities -> {
                    log.debug("authorities={}", authorities);
                    return new DefaultOidcUser(authorities, oidcUserRequest.getIdToken());
                });
    }
}

*/