package com.dejanvuk.microservices.user.services;

import com.dejanvuk.microservices.api.comment.Comment;
import com.dejanvuk.microservices.api.user.User;
import com.dejanvuk.microservices.api.video.Video;
import com.dejanvuk.microservices.user.mappers.UserEntityMapper;
import com.dejanvuk.microservices.user.payload.SignUpPayload;
import com.dejanvuk.microservices.user.persistence.*;
import com.dejanvuk.microservices.user.utility.JwtTokenUtility;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static reactor.core.publisher.Mono.error;

import java.time.Duration;
import java.util.Collections;

@EnableBinding(UserService.MessageSources.class)
@Service
public class UserService implements IUserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    UserRepository userRepository;

    PasswordEncoder passwordEncoder;

    RoleRepository roleRepository;

    JwtTokenUtility jwtTokenUtility;

    UserEntityMapper userEntityMapper;

    int COMMENT_SERVICE_MAX_TIMEOUT;

    int VIDEO_SERVICE_MAX_TIMEOUT;

    private String VIDEO_SERVICE_URL;

    private String COMMENT_SERVICE_URL;

    private final MessageSources messageSources;

    public interface MessageSources {

        String OUTPUT_VIDEOS = "output-videos";
        String OUTPUT_COMMENTS = "output-comments";

        @Output(OUTPUT_VIDEOS)
        MessageChannel outputVideos();

        @Output(OUTPUT_COMMENTS)
        MessageChannel outputComments();
    }

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository,
                       JwtTokenUtility jwtTokenUtility, UserEntityMapper userEntityMapper,
                       @Value("${app.COMMENT_SERVICE_MAX_TIMEOUT}") int COMMENT_SERVICE_MAX_TIMEOUT,
                       @Value("${app.VIDEO_SERVICE_MAX_TIMEOUT}") int VIDEO_SERVICE_MAX_TIMEOUT,
                       @Value("${app.VIDEO_SERVICE_URL}") String VIDEO_SERVICE_URL,
                       @Value("${app.COMMENT_SERVICE_URL}") String COMMENT_SERVICE_URL,
                       MessageSources messageSources) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.jwtTokenUtility = jwtTokenUtility;
        this.userEntityMapper = userEntityMapper;
        this.COMMENT_SERVICE_MAX_TIMEOUT = COMMENT_SERVICE_MAX_TIMEOUT;
        this.VIDEO_SERVICE_MAX_TIMEOUT = VIDEO_SERVICE_MAX_TIMEOUT;
        this.VIDEO_SERVICE_URL = VIDEO_SERVICE_URL;
        this.COMMENT_SERVICE_URL = COMMENT_SERVICE_URL;
        this.messageSources = messageSources;
    }

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

    @Retry(name = "user")
    @CircuitBreaker(name = "user")
    @Override
    public Flux<Video> getUsersVideos(String userId) {
        WebClient webClient = WebClient.create(VIDEO_SERVICE_URL);
        return webClient.get().uri("/videos/user/" + userId).retrieve()
                .bodyToFlux(Video.class).log().timeout(Duration.ofSeconds(COMMENT_SERVICE_MAX_TIMEOUT));
    }

    @Retry(name = "user")
    @CircuitBreaker(name = "user")
    @Override
    public Flux<Comment> getUsersComments(String userId) {
        WebClient webClient = WebClient.create(COMMENT_SERVICE_URL);
        return webClient.get().uri("/comments/user/" + userId).retrieve()
                .bodyToFlux(Comment.class).log().timeout(Duration.ofSeconds(VIDEO_SERVICE_MAX_TIMEOUT));
    }


}
