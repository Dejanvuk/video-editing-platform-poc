package com.dejanvuk.microservices.user.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;

import static java.util.Collections.emptyList;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;
import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SwaggerConfig {
    @Value("${api.common.title}")
    private String apiTitle;

    @Value("${api.common.description}")
    private String apiDescription;

    @Value("${api.common.version}")
    private String apiVersion;

    @Value("${api.common.termsOfServiceUrl}")
    private String apiTermsOfServiceUrl;

    @Value("${api.common.contact.name}")
    private String apiContactName;

    @Value("${api.common.contact.url")
    private String apiContactUrl;

    @Value("${api.common.contact.email")
    private String apiContactEmail;

    @Value("${api.common.license}")
    private String apiLicense;

    @Value("${api.common.licenseUrl}")
    private String apiLicenseUrl;

    @Value("${app.AUTH_SIGNIN_URL}")
    private String authSigninUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("BearerScheme", new SecurityScheme()
                                .type((SecurityScheme.Type.HTTP))
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization"))
                        .addSecuritySchemes("Oauth2Scheme", new SecurityScheme()
                                .description("Oauth2 flow")
                                .type((SecurityScheme.Type.OAUTH2))
                                .scheme("OAuth")
                                .flows(new OAuthFlows().authorizationCode(new OAuthFlow()
                                        .authorizationUrl("http://localhost:8080/oauth2/authorization/facebook")
                                        .scopes(new Scopes()
                                                .addString("email", "access to get email")
                                                .addString("public_profile", "access to public details of the account"))))))
                .info(new Info()
                        .title(apiTitle)
                        .version(apiVersion)
                        .description(apiDescription)
                        .termsOfService(apiTermsOfServiceUrl)
                        .contact(new Contact().name(apiContactName).email(apiContactEmail).url(apiContactUrl))
                        .license(new License().name(apiLicense).url(apiLicenseUrl)));
    }
}
