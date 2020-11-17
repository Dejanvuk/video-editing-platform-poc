package com.dejanvuk.microservices.commentmicroservice;

import com.dejanvuk.microservices.api.comment.Comment;
import com.dejanvuk.microservices.commentmicroservice.payload.CommentPayload;
import com.dejanvuk.microservices.commentmicroservice.persistence.CommentEntity;
import com.dejanvuk.microservices.commentmicroservice.persistence.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import static org.springframework.http.HttpStatus.*;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles(profiles = "junit")
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment=RANDOM_PORT, properties = {"spring.data.mongodb.port=0"})
public class ServicesTests {
    @Autowired
    private WebTestClient client;

    @Autowired
    private CommentRepository repository;

    @BeforeEach
    public void setupDb() {
        repository.deleteAll().block();
    }

    @Test
    public void getOwnerCommentsTest(){
        insertRandomEntity("1", "2");
        insertRandomEntity("1", "3");

        getComments("/user", "/1", OK).hasSize(2);
    }

    @Test
    public void getVideoCommentsTest(){
        insertRandomEntity("1", "2");
        insertRandomEntity("1", "3");

        getComments("/video", "/2", OK).hasSize(1);
    }

    /*
    @Test
    public void putCommentTest() {
        CommentEntity commentEntity = insertRandomEntity("1", "2");
        String oldContent = commentEntity.getContent();

        CommentPayload updateCommentPayload = new CommentPayload();
        updateCommentPayload.setContent(oldContent + " edited!");

        client.put().uri("/comments/"+commentEntity.getId())
                .body(Mono.just(updateCommentPayload), CommentPayload.class)
                .exchange()
                .expectStatus().isEqualTo(OK);


        assertEquals(repository.findById(commentEntity.getId()).block().getContent(), oldContent + " edited!");
    }
    */

    /**
     *
     * @param path either /video or /user
     * @param id the id of the video or user
     * @param expectedStatus
     * @return
     */
    private WebTestClient.ListBodySpec<Comment> getComments(String path, String id, HttpStatus expectedStatus){
        return client.get()
                .uri("/comments" + path + id)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBodyList(Comment.class);
    }

    private CommentEntity insertRandomEntity(String ownerId, String videoId) {
        CommentEntity commentEntity = new CommentEntity();

        commentEntity.setOwnerId(ownerId);
        commentEntity.setContent("content");
        commentEntity.setVideoId(videoId);
        commentEntity.setLikes(0);
        commentEntity.setDislikes(0);
        commentEntity.setNrOfReports(0);

        return repository.save(commentEntity).block();
    }
}
