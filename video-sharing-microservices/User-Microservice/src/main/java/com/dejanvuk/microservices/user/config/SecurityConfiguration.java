package com.dejanvuk.microservices.user.config;

import com.dejanvuk.microservices.user.services.CustomReactiveUserDetailsService;
import com.dejanvuk.microservices.user.utility.JwtTokenUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.codec.json.AbstractJackson2Decoder;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {

    @Value("${app.AUTH_SIGNIN_URL}")
    private String authSigninUrl;

    @Value("${jwt.TOKEN_HEADER}")
    private String RESPONSE_HEADER;

    @Value("${jwt.TOKEN_PREFIX}")
    private String TOKEN_PREFIX;

    @Autowired
    private JwtTokenUtility jwtTokenUtility;

    @Autowired
    private CustomReactiveUserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private CustomServerAuthenticationConverter customServerAuthenticationConverter;

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

        NoOpServerSecurityContextRepository sessionConfig = NoOpServerSecurityContextRepository.getInstance();

        //filter.setSecurityContextRepository(sessionConfig); // stateless sessions, clients must send Authorization header with every request
        filter.setServerAuthenticationConverter(customServerAuthenticationConverter);
        filter.setRequiresAuthenticationMatcher(
                ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, authSigninUrl)
        );

        // set success and failure handlers
        filter.setAuthenticationSuccessHandler((webFilterExchange, authentication) -> {
            //Set the value of the #AUTHORIZATION header to the given Bearer token.
            webFilterExchange.getExchange().getResponse().getHeaders().setBearerAuth(jwtTokenUtility.generateToken(authentication));
            return Mono.empty();
        });


        return filter;
    }


    /*
    @Autowired
    HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Autowired
    CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    CustomOauth2AuthenticationSuccessHandler customOauth2AuthenticationSuccessHandler;

    @Autowired
    CustomOauth2AuthenticationFailHandler customOauth2AuthenticationFailHandler;
    */

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http, ReactiveAuthenticationManager authenticationManager) {
        http.cors().and()
                .csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .logout().disable();

        http.authenticationManager(reactiveAuthenticationManager());

        http.authorizeExchange()
                .pathMatchers("/", "/favicon.ico", "/**/*.png", "/**/*.gif", "/**/*.svg", "/**/*.jpg", "/**/*.html", "/**/*.css", "/**/*.js").permitAll()
                .pathMatchers("/auth/**", "/oauth2/**").permitAll()
                .pathMatchers(HttpMethod.GET, "/api/user/verify-email").permitAll()
                .pathMatchers(HttpMethod.GET, "/email-verification/**").permitAll()
                .pathMatchers(HttpMethod.GET, "/api/user/reset-password/**").permitAll()
                .anyExchange().authenticated();

        /*
        http.oauth2Login().authorizationEndpoint().baseUri("/oauth2/authorize").authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository).and()
                .userInfoEndpoint().userService(customOAuth2UserService).and()
                .successHandler(customOauth2AuthenticationSuccessHandler)
                .failureHandler(customOauth2AuthenticationFailHandler);

         */

        http.addFilterAfter(authenticationWebFilter(),SecurityWebFiltersOrder.AUTHENTICATION);
        //http.addFilterAfter(authorizationWebFilter(), SecurityWebFiltersOrder.AUTHORIZATION);

        return http.build();
    }
}
