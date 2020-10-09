package com.dejanvuk.microservices.user.services;

import com.dejanvuk.microservices.api.comment.Comment;
import com.dejanvuk.microservices.api.user.User;
import com.dejanvuk.microservices.api.video.Video;
import com.dejanvuk.microservices.user.payload.SignUpPayload;
import com.dejanvuk.microservices.user.persistence.UserEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IUserService {
    Mono<UserEntity> create(SignUpPayload signUpPayload);

    Mono<User> findById(String id);

    Flux<User> findByName(String name);

    Mono<User> findByUsernameOrEmail(String name);

    Flux<User> findAll();

    //Mono<User> update(UserPayload e);

    Mono<Void> delete(Long id);

    Mono<Boolean> checkForDuplicates(SignUpPayload signUpPayload);

    Flux<Video> getUsersVideos(String userId);

    Flux<Comment>getUsersComments(String userId);

}
