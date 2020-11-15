package com.dejanvuk.microservices.user.config;

import com.dejanvuk.microservices.user.config.oauth2.CustomServerAuthorizationRequestRepository;
import com.dejanvuk.microservices.user.config.oauth2.OAuth2ProviderFactory;
import com.dejanvuk.microservices.user.config.oauth2.OAuth2UserAttributes;
import com.dejanvuk.microservices.user.persistence.RoleRepository;
import com.dejanvuk.microservices.user.persistence.RoleType;
import com.dejanvuk.microservices.user.persistence.UserEntity;
import com.dejanvuk.microservices.user.persistence.UserRepository;
import com.dejanvuk.microservices.user.services.CustomReactiveUserDetailsService;
import com.dejanvuk.microservices.user.utility.CookieUtility;
import com.dejanvuk.microservices.user.utility.JwtTokenUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.*;
import org.springframework.http.codec.json.AbstractJackson2Decoder;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.server.reactive.ContextPathCompositeHandler;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authorization.AuthorizationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.WebFilter;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfiguration.class);

    @Value("${app.AUTH_SIGNIN_URL}")
    private String authSigninUrl;

    @Value("${jwt.TOKEN_HEADER}")
    private String RESPONSE_HEADER;

    @Value("${jwt.TOKEN_PREFIX}")
    private String TOKEN_PREFIX;

    @Value("${app.OAUTH2_AUTHORIZATION_COOKIE}")
    private String OAUTH2_AUTHORIZATION_COOKIE;

    @Value("${app.REDIRECT_COOKIE}")
    private String REDIRECT_COOKIE;

    @Autowired
    private JwtTokenUtility jwtTokenUtility;

    @Autowired
    private CustomReactiveUserDetailsService userDetailsService;

    @Autowired
    private CustomServerAuthenticationConverter customServerAuthenticationConverter;

    @Autowired
    JwtAuthorizationFilter jwtAuthorizationFilter;

    // OAUTH2 OPENID
    @Autowired
    CustomServerAuthorizationRequestRepository customServerAuthorizationRequestRepository;

    @Bean
    AbstractJackson2Decoder jacksonDecoder() {
        return new Jackson2JsonDecoder();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager() {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);

        authenticationManager.setPasswordEncoder(passwordEncoder());

        return authenticationManager;
    }

    @Bean
    public AuthenticationWebFilter authenticationWebFilter() {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(reactiveAuthenticationManager());

        filter.setServerAuthenticationConverter(customServerAuthenticationConverter);
        filter.setRequiresAuthenticationMatcher(
                ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, authSigninUrl)
        );

        NoOpServerSecurityContextRepository sessionConfig = NoOpServerSecurityContextRepository.getInstance();

        filter.setSecurityContextRepository(sessionConfig);

        filter.setAuthenticationSuccessHandler((webFilterExchange, authentication) -> {
            //Set the value of the #AUTHORIZATION header to the given Bearer token.
            CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

            ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
            response.getHeaders().setBearerAuth(jwtTokenUtility.generateToken(user.getUsername()));
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return Mono.empty();
        });

        filter.setAuthenticationFailureHandler(customServerAuthenticationFailureHandler());

        return filter;
    }

    @Bean
    ServerAuthenticationFailureHandler customServerAuthenticationFailureHandler() {
        ServerAuthenticationFailureHandler serverAuthenticationFailureHandler = (webFilterExchange, ex) -> {
            final Logger log = LoggerFactory.getLogger(ServerAuthenticationFailureHandler.class);

            ServerHttpResponse response = webFilterExchange.getExchange().getResponse();

            response.setStatusCode(HttpStatus.OK);

            JSONObject obj = new JSONObject();
            obj.put("status", HttpStatus.OK.toString());
            obj.put("message", ex.getMessage());
            obj.put("authenticated", "false");
            //obj.put("exists: " , {check the instance of the exception});

            log.info("AUTHENTICATION FAILED!! {}", obj);

            DataBuffer bodyDataBuffer = response.bufferFactory().wrap(JSONObject.toJSONString(obj).getBytes(StandardCharsets.UTF_8));

            return response.writeWith(Mono.just(bodyDataBuffer));
        };

        return serverAuthenticationFailureHandler;
    }

    @Bean
    ServerAuthenticationFailureHandler customOauth2ServerAuthenticationFailureHandler() {
        ServerAuthenticationFailureHandler serverAuthenticationFailureHandler = new ServerAuthenticationFailureHandler() {
            @Autowired
            CookieUtility cookieUtility;

            @Override
            public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException ex) {
                final Logger log = LoggerFactory.getLogger(ServerAuthenticationFailureHandler.class);

                ServerHttpResponse response = webFilterExchange.getExchange().getResponse();

                response.setStatusCode(HttpStatus.OK);

                // redirect the user without a jwt
                ServerHttpRequest request = webFilterExchange.getExchange().getRequest();
                MultiValueMap<String, HttpCookie> cookieMap = request.getCookies();


                String redirectUri = cookieUtility.getCookieValue(cookieMap, REDIRECT_COOKIE);

                response.getHeaders().setLocation(URI.create(redirectUri));

                JSONObject obj = new JSONObject();
                obj.put("status", HttpStatus.OK.toString());
                obj.put("message", ex.getMessage());
                obj.put("authenticated", "false");
                //obj.put("exists: " , {check the instance of the exception});

                log.info("AUTHENTICATION FAILED!! {}", obj);

                DataBuffer bodyDataBuffer = response.bufferFactory().wrap(JSONObject.toJSONString(obj).getBytes(StandardCharsets.UTF_8));

                return response.writeWith(Mono.just(bodyDataBuffer));
            }
        };

        return serverAuthenticationFailureHandler;
    }


    @Bean
    RedirectServerAuthenticationSuccessHandler customRedirectServerAuthenticationSuccessHandler() {
        RedirectServerAuthenticationSuccessHandler redirectServerAuthenticationSuccessHandler = new RedirectServerAuthenticationSuccessHandler() {
            @Autowired
            CookieUtility cookieUtility;

            @Value("${jwt.TOKEN_PREFIX}")
            private String TOKEN_PREFIX;

            @Autowired
            private JwtTokenUtility jwtTokenUtility;

            @Autowired
            UserRepository userRepository;

            @Autowired
            RoleRepository roleRepository;


            public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
                if (authentication instanceof OAuth2AuthenticationToken) {
                    ServerHttpRequest request = webFilterExchange.getExchange().getRequest();
                    MultiValueMap<String, HttpCookie> cookieMap = request.getCookies();


                    String redirectUri = cookieUtility.getCookieValue(cookieMap, REDIRECT_COOKIE);

                    log.info("redirectURi {}", redirectUri);


                    ServerHttpResponse response = webFilterExchange.getExchange().getResponse();

                    if (response.isCommitted()) {
                        return Mono.empty();
                    }

                    // save the oauth2 user in database
                    DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

                    Map<String, Object> userAttributes = defaultOAuth2User.getAttributes();

                    return userRepository.existsByEmail((String) userAttributes.get("email")).flatMap(exists -> {
                        OAuth2UserAttributes oAuth2UserAttributes = OAuth2ProviderFactory.getOAuth2UserAttributesByProvider((OAuth2AuthenticationToken) authentication, userAttributes);

                        String jwt = TOKEN_PREFIX + " " + jwtTokenUtility.generateToken(oAuth2UserAttributes.getEmail());

                        String location = null;
                        try {
                            location = UriComponentsBuilder.fromUriString(redirectUri).queryParam("jwt", URLEncoder.encode(jwt, "UTF-8")).build().toUriString();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        // clear authentication attributes and cookies are no longer needed
                        cookieUtility.deleteCookie(response, cookieMap, REDIRECT_COOKIE);
                        cookieUtility.deleteCookie(response, cookieMap, OAUTH2_AUTHORIZATION_COOKIE);

                        response.setStatusCode(HttpStatus.PERMANENT_REDIRECT);
                        response.getHeaders().setLocation(URI.create(location));

                        log.info("location {}", location);

                        if (!exists) {
                            log.info("user doesnt exist, creating him!");
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

                            return roleRepository.findByName(RoleType.ROLE_USER).flatMap(role -> {
                                userEntity.setRoles(Collections.singleton(role));
                                System.out.println(userEntity);
                                return userRepository.save(userEntity).flatMap(user -> Mono.empty());
                            });
                        } else { // User already exists
                            log.info("user exists!");
                            return Mono.empty();
                        }
                    });

                } else return Mono.empty();
            }

        };

        return redirectServerAuthenticationSuccessHandler;
    }


    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http, ReactiveAuthenticationManager authenticationManager) {
        http.cors().and()
                .csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .logout().disable();

        http.authenticationManager(reactiveAuthenticationManager());

        http.securityContextRepository(NoOpServerSecurityContextRepository.getInstance()); // stateless sessions, clients must send Authorization header with every request

        http.authorizeExchange()
                .pathMatchers("/", "/favicon.ico", "/**/*.png", "/**/*.gif", "/**/*.svg", "/**/*.jpg", "/**/*.html", "/**/*.css", "/**/*.js").permitAll()
                .pathMatchers("/auth/**", "/oauth2/**").permitAll()
                .pathMatchers("/users/**").permitAll()
                .pathMatchers(HttpMethod.GET, "/api/user/verify-email").permitAll()
                .pathMatchers(HttpMethod.GET, "/email-verification/**").permitAll()
                .pathMatchers(HttpMethod.GET, "/api/user/reset-password/**").permitAll()
                .pathMatchers(HttpMethod.GET, "/actuator/**").permitAll()
                .pathMatchers("/v3/api-docs/**", "/v2/api-docs/**",
                        "/configuration/ui",
                        "/swagger-resources/**",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/webjars/**").permitAll()
                .anyExchange().authenticated();

        http.oauth2Login()
                .authorizationRequestRepository(customServerAuthorizationRequestRepository)
                .authenticationSuccessHandler(customRedirectServerAuthenticationSuccessHandler())
                .authenticationFailureHandler(customOauth2ServerAuthenticationFailureHandler());

        http.addFilterAfter(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION);
        http.addFilterBefore(jwtAuthorizationFilter, SecurityWebFiltersOrder.HTTP_BASIC);

        return http.build();
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
