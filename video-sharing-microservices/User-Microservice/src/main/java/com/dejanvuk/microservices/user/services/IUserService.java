package com.dejanvuk.microservices.user.services;

import com.dejanvuk.microservices.user.payload.SignUpPayload;
import com.dejanvuk.microservices.user.persistence.UserEntity;
import com.dejanvuk.microservices.user.response.UserResponse;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IUserService {
    Mono<UserEntity> create(SignUpPayload signUpPayload);

    Mono<UserResponse> findById(String id);

    Flux<UserResponse> findByName(String name);

    Flux<UserResponse> findAll();

    //Mono<UserResponse> update(UserPayload e);

    Mono<Void> delete(Long id);

    Mono<Boolean> checkForDuplicates(SignUpPayload signUpPayload);
}
