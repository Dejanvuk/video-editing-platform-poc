package com.dejanvuk.microservices.user.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveMongoRepository<UserEntity, String> {
    /*
    Mono<UserEntity> findByUsernameOrEmail(String username, String email);
    Mono<UserEntity> findByUsername(String username);
    Mono<UserEntity> findByEmail(String email);
    Mono<Boolean> existsByUsername(String username);
    Mono<Boolean> existsByEmail(String email);
    Mono<UserEntity> findUserByToken(String token);
    */

    Mono<UserEntity> findByUsernameOrEmail(String username, String email);
    Mono<Boolean> existsByUsername(String username);
    Mono<Boolean> existsByEmail(String email);
}
