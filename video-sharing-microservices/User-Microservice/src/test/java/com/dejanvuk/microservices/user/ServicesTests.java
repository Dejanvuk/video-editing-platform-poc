package com.dejanvuk.microservices.user;


import com.dejanvuk.microservices.api.comment.Comment;
import com.dejanvuk.microservices.api.video.Video;
import com.dejanvuk.microservices.user.controller.UserController;
import com.dejanvuk.microservices.user.mappers.UserEntityMapper;
import com.dejanvuk.microservices.user.payload.LoginPayload;
import com.dejanvuk.microservices.user.payload.SignUpPayload;
import com.dejanvuk.microservices.user.persistence.UserRepository;
import com.dejanvuk.microservices.user.services.UserService;
import com.dejanvuk.microservices.user.utility.JwtTokenUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Base64Utils;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.data.mongodb.port=0"})
@ExtendWith(MockitoExtension.class)
public class ServicesTests {
    @Autowired
    WebTestClient client;

    @Autowired
    UserRepository repository;

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenUtility jwtTokenUtility;

    @Autowired
    UserEntityMapper userEntityMapper;

    @Value("${app.AUTH_SIGNUP_URL}")
    private String signUpUrl;

    @Value("${app.AUTH_SIGNIN_URL}")
    private String signInUrl;

    @BeforeEach
    public void setupDb() {

        repository.deleteAll().block();
    }

    ExchangeFilterFunction printlnFilter = (request, next) -> {
        System.out.println("\n\n" + request.method().toString().toUpperCase() + ":\n\nURL:"
                + request.url().toString() + ":\n\nHeaders:" + request.headers().toString() + "\n\nAttributes:"
                + request.attributes() + "\n\n");

        return next.exchange(request);
    };

    public void signUpTest() {
        SignUpPayload payload = new SignUpPayload();
        payload.setUsername("username");
        payload.setEmail("email@email.com");
        payload.setName("name name");
        payload.setPassword("password");

        client.post()
                .uri(signUpUrl)
                .body(Mono.just(payload), SignUpPayload.class)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(CREATED)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody().jsonPath("$.message").isEqualTo("User created successfully!");

        StepVerifier.create(repository.count()).expectNext(1l).verifyComplete();
    }

    public void signInAndGetUserDetailsTest() {
        LoginPayload payload = new LoginPayload();
        payload.setUsernameOrEmail("username");
        payload.setPassword("password");

        String jwtToken = "";

        client.post()
                .uri(signInUrl)
                .body(Mono.just(payload), LoginPayload.class)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(OK)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectHeader().value(HttpHeaders.AUTHORIZATION, s -> {
                    System.out.println(s);
            client.get()
                    .uri("/users/me")
                    .header(HttpHeaders.AUTHORIZATION, s)
                    .accept(APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isEqualTo(OK)
                    .expectHeader().contentType(APPLICATION_JSON)
                    .expectBody()
                    .jsonPath("$.name").isEqualTo("name name")
                    .jsonPath("$.email").isEqualTo("email@email.com");
        });
    }

    @Test
    public void SignUpSignInTest() {
        signUpTest();
        signInAndGetUserDetailsTest();
    }

    @Test
    public void getUserDetailsUnauthorizedTest(){
        client.get()
                .uri("/users/me")
                .headers(httpHeaders -> httpHeaders.setBearerAuth("faketoken"))
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void getUsersVideosTest() {
        UserService service = Mockito.mock(UserService.class);


    }

    /*
    @Test
    public void getUsersCommentsTest() {
        UserService service = Mockito.mock(UserService.class);

        String userId = "1";
        Comment comment1 = new Comment();
        Comment comment2 = new Comment();
        Comment comment3 = new Comment();
        Flux<Comment> commentFlux = Flux.just(comment1, comment2, comment3);
        Mockito.when(service.getUsersComments(userId)).thenReturn(commentFlux);

        UserController userController = new UserController();

        userController.setUserService(service);

        WebTestClient mockedClient = WebTestClient.bindToController(userController).build();

        mockedClient.get()
                .uri("/users/" + userId + "/comments")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(OK)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBodyList(Video.class).hasSize(3);
    }
    */


}
