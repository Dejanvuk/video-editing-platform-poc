package com.dejanvuk.microservices.commentmicroservice;

import com.dejanvuk.microservices.api.event.Event;
import com.dejanvuk.microservices.commentmicroservice.payload.CommentPayload;
import com.dejanvuk.microservices.commentmicroservice.persistence.CommentEntity;
import com.dejanvuk.microservices.commentmicroservice.persistence.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.TestPropertySource;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import static com.dejanvuk.microservices.api.event.Event.Type.CREATE;
import static com.dejanvuk.microservices.api.event.Event.Type.DELETE;
import static com.dejanvuk.microservices.api.event.Event.Type.UPDATE;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.data.mongodb.port=0"})
public class MessageProcessingTests {
    @Autowired
    private Sink channels;

    @Autowired
    CommentRepository repository;

    private AbstractMessageChannel input = null;

    @BeforeEach
    public void setup() {
        input = (AbstractMessageChannel) channels.input();
        repository.deleteAll().block();
    }

    @Test
    public void createCommentEventTest() {

        String ownerId = "2";
        String videoId = "3";

        assertEquals(0, (long)repository.count().block());

        sendCreateCommentEvent(ownerId, videoId);

        assertEquals(1, (long)repository.count().block());

    }

    @Test
    public void deleteCommentEventTest() {

        String ownerId = "2";
        String videoId = "3";

        sendCreateCommentEvent(ownerId, videoId);
        assertEquals(1, (long)repository.count().block());

        String commentId = repository.findAllByOwnerId(ownerId).blockFirst().getId();

        sendDeleteCommentEvent(commentId);
        assertEquals(0, (long)repository.count().block());

    }

    @Test
    public void updateCommentEventTest() {
        String ownerId = "2";
        String videoId = "3";

        sendCreateCommentEvent(ownerId, videoId);
        assertEquals(1, (long)repository.count().block());

        String commentId = repository.findAllByOwnerId(ownerId).blockFirst().getId();

        CommentPayload updatePayload = new CommentPayload();

        updatePayload.setContent("new-content");

        sendUpdateCommentEvent(commentId, updatePayload);

        assertEquals(1, (long)repository.count().block());
        assertEquals("new-content", repository.findAllByOwnerId(ownerId).blockFirst().getContent());
    }


    private void sendCreateCommentEvent(String ownerId, String videoId) {
        CommentPayload commentPayload = new CommentPayload();

        commentPayload.setOwnerId(ownerId);
        commentPayload.setVideoId(videoId);
        commentPayload.setContent("content");

        Event<String, CommentPayload> event = new Event(CREATE, "", commentPayload);
        input.send(new GenericMessage<>(event));
    }

    private void sendDeleteCommentEvent(String commentId) {
        Event<String, CommentPayload> event = new Event(DELETE, commentId, "");
        input.send(new GenericMessage<>(event));
    }

    private void sendUpdateCommentEvent(String commentId, CommentPayload data) {
        Event<String, CommentPayload> event = new Event(UPDATE, commentId, data);
        input.send(new GenericMessage<>(event));
    }
}


