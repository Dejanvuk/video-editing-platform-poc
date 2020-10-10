package com.dejanvuk.microservices.user.controller;

import com.dejanvuk.microservices.api.comment.Comment;
import com.dejanvuk.microservices.api.user.User;
import com.dejanvuk.microservices.api.video.Video;
import com.dejanvuk.microservices.user.config.CustomUserDetails;
import com.dejanvuk.microservices.user.mappers.UserEntityMapper;
import com.dejanvuk.microservices.user.payload.SignUpPayload;
import com.dejanvuk.microservices.user.response.UserCreatedResponse;
import com.dejanvuk.microservices.user.services.UserService;
import com.dejanvuk.microservices.user.utility.JwtTokenUtility;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@Tag(name = "REST API for users")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenUtility jwtTokenUtility;

    @Autowired
    UserEntityMapper userEntityMapper;

    @Operation(description = "Sign up with the given credentials")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Successfully signed up", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400", description = "Missing or invalid request body"),
            @ApiResponse(responseCode = "500", description = "Internal error")})
    @PostMapping(path = "${app.AUTH_SIGNUP_URL}", consumes = "application/json", produces = "application/json")
    Mono<?> registerUser(@RequestBody SignUpPayload signUpPayload, ServerHttpResponse response) {
        Mono<ResponseEntity<Map<String, String>>> errors = validateSignUpResult(signUpPayload);
        if (errors != null) return errors;

        return userService.checkForDuplicates(signUpPayload).flatMap(exists -> {
            if (exists) return Mono.just(new ResponseEntity<>("User already exists!", HttpStatus.UNPROCESSABLE_ENTITY));
            else return userService.create(signUpPayload).map(user -> {
                // Send verification email
                //amazonSesService.sendVerificationEmail(user);
                response.setStatusCode( HttpStatus.CREATED);
                return new UserCreatedResponse("User created successfully!");
            });
        });
    }


    @Operation(description = "Get private details about the user", security = @SecurityRequirement(name = "BearerScheme", scopes = "USER"))
    @ApiResponses({@ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid request body"),
            @ApiResponse(responseCode = "401", description = "Access token is missing or invalid")})
    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    Mono<User> getCurrentLoggedInUser(@AuthenticationPrincipal Mono<CustomUserDetails> currentUserMono) {
        return currentUserMono.flatMap(currentUser -> userService.findById(currentUser.getId()));
    }


    public String generateRandomString(int n) {

        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxyz";

        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            int index = (int) (AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }

        return sb.toString();
    }

    private Mono<ResponseEntity<Map<String, String>>> validateSignUpResult(SignUpPayload signUpPayload) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<SignUpPayload>> violations = validator.validate(signUpPayload);

        if (violations.size() == 0) return null;

        Map<String, String> errorMap = new HashMap<>();

        for (ConstraintViolation<SignUpPayload> violation : violations) {
            errorMap.put(violation.getInvalidValue().toString(), violation.getMessage());
        }

        return Mono.just(new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST));
    }

    @GetMapping(value = "/users/{userId}", produces = "application/json")
    Mono<User> getUser(@PathVariable String userId) {
        return userService.findById(userId);
    }

    @GetMapping(value = "/users/{userId}/videos", produces = "application/json")
    Flux<Video> getUsersVideos(@PathVariable String userId) {
        return userService.getUsersVideos(userId).onErrorReturn(CallNotPermittedException.class, getFallbackVideos());
    }

    Video getFallbackVideos() {
        return new Video(); // empty video
    }

    @GetMapping(value = "/users/{userId}/comments", produces = "application/json")
    Flux<Comment> getUsersComments(@PathVariable String userId) {
        return userService.getUsersComments(userId).onErrorReturn(CallNotPermittedException.class, getFallbackComments());
    }

    Comment getFallbackComments() {
        return new Comment(); // empty comment
    }
}
