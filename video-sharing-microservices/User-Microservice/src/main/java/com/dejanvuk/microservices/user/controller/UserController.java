package com.dejanvuk.microservices.user.controller;

import com.dejanvuk.microservices.user.config.CustomUserDetails;
import com.dejanvuk.microservices.user.payload.SignUpPayload;
import com.dejanvuk.microservices.user.persistence.UserEntity;
import com.dejanvuk.microservices.user.response.UserResponse;
import com.dejanvuk.microservices.user.services.BindingResultValidationService;
import com.dejanvuk.microservices.user.services.UserService;
import com.dejanvuk.microservices.user.utility.JwtTokenUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    BindingResultValidationService bindingResultValidationService;

    @Autowired
    JwtTokenUtility jwtTokenUtility;


    @PostMapping("${app.AUTH_SIGN_UP_URL}")
    ResponseEntity<?> registerUser(@Valid @RequestBody SignUpPayload signUpPayload, BindingResult bindingResult, UriComponentsBuilder b) {

        ResponseEntity<Mono<Map<String, String>>> errors = bindingResultValidationService.validateResult(bindingResult);
        if(errors != null) return errors;
        ResponseEntity<?> validationErrors = userService.checkForDuplicates(signUpPayload);
        if(validationErrors != null) return validationErrors;

        userService.create(signUpPayload);

        // Send verification email
        //amazonSesService.sendVerificationEmail(user);

        UriComponents location = b.path("/").buildAndExpand();
        return ResponseEntity.created(location.toUri()).body("User created succesfully!");
    }

    /*
    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    ResponseEntity<Mono<UserResponse>> getCurrentLoggedInUser(@AuthenticationPrincipal Mono<CustomUserDetails> currentUser) {
        Mono<UserEntity> user = userService.findById(currentUser.getId()).map(e -> );
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    */

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
}
