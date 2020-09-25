package com.dejanvuk.microservices.user.controller;

import com.dejanvuk.microservices.user.config.CustomUserDetails;
import com.dejanvuk.microservices.user.mappers.UserEntityMapper;
import com.dejanvuk.microservices.user.payload.SignUpPayload;
import com.dejanvuk.microservices.user.persistence.UserEntity;
import com.dejanvuk.microservices.user.response.UserResponse;
import com.dejanvuk.microservices.user.services.UserService;
import com.dejanvuk.microservices.user.utility.JwtTokenUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import javax.validation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenUtility jwtTokenUtility;

    @Autowired
    UserEntityMapper userEntityMapper;


    @PostMapping(path = "${app.AUTH_SIGNUP_URL}", consumes = "application/json", produces = "application/json")
    Mono<?> registerUser(@RequestBody SignUpPayload signUpPayload, UriComponentsBuilder b) {

        Mono<ResponseEntity<Map<String, String>>> errors = validateSignUpResult(signUpPayload);
        if(errors != null) return errors;
        return userService.checkForDuplicates(signUpPayload).flatMap(exists -> {
            if(exists) return Mono.just(new ResponseEntity<>("User already exists!", HttpStatus.UNPROCESSABLE_ENTITY));
            else {
                return userService.create(signUpPayload).flatMap(user -> {
                    // Send verification email
                    //amazonSesService.sendVerificationEmail(user);
                    return Mono.just(new ResponseEntity<>("User created succesfully!", HttpStatus.CREATED));
                });
            }
        });
    }


    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    Mono<ResponseEntity<UserResponse>> getCurrentLoggedInUser(@AuthenticationPrincipal Mono<CustomUserDetails> currentUserMono) {
        return currentUserMono.flatMap(currentUser -> userService.findById(currentUser.getId()).map(userResponse -> new ResponseEntity<>(userResponse, HttpStatus.OK)));
    }


    public String generateRandomString(int n)
    {

        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxyz";

        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            int index = (int)(AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString .charAt(index));
        }

        return sb.toString();
    }

    private Mono<ResponseEntity<Map<String, String>>> validateSignUpResult(SignUpPayload signUpPayload) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<SignUpPayload>> violations = validator.validate(signUpPayload);

        if(violations.size() == 0) return null;

        Map<String,String> errorMap = new HashMap<>();

        for (ConstraintViolation<SignUpPayload > violation : violations) {
            errorMap.put(violation.getInvalidValue().toString(), violation.getMessage());
        }

        return Mono.just(new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST));
    }
}
