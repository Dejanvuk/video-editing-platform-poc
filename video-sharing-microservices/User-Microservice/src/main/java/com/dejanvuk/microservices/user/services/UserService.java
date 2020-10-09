package com.dejanvuk.microservices.user.services;

import com.dejanvuk.microservices.api.comment.Comment;
import com.dejanvuk.microservices.api.user.User;
import com.dejanvuk.microservices.api.video.Video;
import com.dejanvuk.microservices.user.mappers.UserEntityMapper;
import com.dejanvuk.microservices.user.payload.SignUpPayload;
import com.dejanvuk.microservices.user.persistence.*;
import com.dejanvuk.microservices.user.utility.JwtTokenUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static reactor.core.publisher.Mono.error;

import java.util.Collections;

@Service
public class UserService implements IUserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    JwtTokenUtility jwtTokenUtility;

    @Autowired
    UserEntityMapper userEntityMapper;

    private final String VIDEO_SERVICE_URL = "http://videos-service";

    private final String COMMENT_SERVICE_URL = "http://comments-service";

    @Override
    public Mono<UserEntity> create(SignUpPayload signUpPayload) {

        log.debug("Creating user {} ", signUpPayload);

        UserEntity user = new UserEntity();
        user.setUsername(signUpPayload.getUsername());
        user.setEmail(signUpPayload.getEmail());
        user.setName(signUpPayload.getName());
        user.setPassword(passwordEncoder.encode(signUpPayload.getPassword()));

        user.setVerified(false);
        user.setToken(jwtTokenUtility.generateTokenByEmail(signUpPayload.getEmail()));

        return roleRepository.findByName(RoleType.ROLE_USER).flatMap(role -> {
            user.setRoles(Collections.singleton(role));
            System.out.println(user);
            return userRepository.save(user);
        });
    }

    @Override
    public Mono<User> findById(String id) {
        return userRepository.findById(id).map(e -> userEntityMapper.userEntityToUserResponse(e));
    }

    @Override
    public Flux<User> findByName(String name) {
        return null;
    }

    @Override
    public Mono<User> findByUsernameOrEmail(String name) {
        return null;
    }

    @Override
    public Flux<User> findAll() {
        return null;
    }

    @Override
    public Mono<Void> delete(Long id) {
        return null;
    }

    @Override
    public Mono<Boolean> checkForDuplicates(SignUpPayload signUpPayload) {
        log.debug("Checking user sign up payload for duplicates");

        return userRepository.existsByUsernameOrEmail(signUpPayload.getUsername(), signUpPayload.getEmail());
    }

    @Override
    public Flux<Video> getUsersVideos(String userId) {
        WebClient webClient = WebClient.create(VIDEO_SERVICE_URL);
        return webClient.get().uri("/videos/user/" + userId).retrieve().bodyToFlux(Video.class).log().onErrorResume(error -> Flux.empty());
    }

    @Override
    public Flux<Comment> getUsersComments(String userId) {
        WebClient webClient = WebClient.create(COMMENT_SERVICE_URL);
        return webClient.get().uri("/comments/user/" + userId).retrieve().bodyToFlux(Comment.class).log().onErrorResume(error -> Flux.empty());
    }


}
