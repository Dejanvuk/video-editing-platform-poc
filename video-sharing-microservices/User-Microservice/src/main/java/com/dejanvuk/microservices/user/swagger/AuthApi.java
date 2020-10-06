package com.dejanvuk.microservices.user.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Authentication API specification for Swagger documentation and Code Generation.
 * Implemented by Spring Security.
 */
@Tag(name = "Authentication")
@RequestMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
public interface AuthApi {
    /**
     * Implemented by Spring Security
     */
    @Operation(description = "Login with the given credentials.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "login api"),
            @ApiResponse(responseCode = "404", description = "Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Missing or invalid request body"),
            @ApiResponse(responseCode = "500", description = "Internal error")})
    @RequestMapping(value = "${app.AUTH_SIGNIN_URL}", method = RequestMethod.POST)
    default void login(
            @RequestParam("usernameOrEmail") String username,
            @RequestParam("password") String password
    ) {
        throw new IllegalStateException("Add Spring Security to handle authentication");
    }
}

