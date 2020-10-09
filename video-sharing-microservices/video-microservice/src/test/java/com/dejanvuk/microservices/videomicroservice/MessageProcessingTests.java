package com.dejanvuk.microservices.videomicroservice;

import com.dejanvuk.microservices.api.event.Event;
import com.dejanvuk.microservices.videomicroservice.payload.VideoPayload;
import com.dejanvuk.microservices.videomicroservice.persistence.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.messaging.support.GenericMessage;

import static com.dejanvuk.microservices.api.event.Event.Type.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.data.mongodb.port=0"})
public class MessageProcessingTests {
    @Autowired
    private Sink channels;

    @Autowired
    VideoRepository repository;

    private AbstractMessageChannel input = null;

    @BeforeEach
    public void setup() {
        input = (AbstractMessageChannel) channels.input();
        repository.deleteAll().block();
    }

    @Test
    public void createVideoEventTest() {
        String ownerId = "1";
        String description = "description";
        String title = "title";
        String locationUrl = "locationUrl";

        assertEquals(0, (long)repository.count().block());

        sendCreateVideoEvent(ownerId, description, title, locationUrl);

        assertEquals(1, (long)repository.count().block());
    }

    @Test
    public void deleteVideoEventTest() {
        String ownerId = "1";
        String description = "description";
        String title = "title";
        String locationUrl = "locationUrl";


        sendCreateVideoEvent(ownerId, description, title, locationUrl);
        assertEquals(1, (long)repository.count().block());

        String videoId = repository.findAllByOwnerId(ownerId).blockFirst().getId();

        sendDeleteVideoEvent(videoId);
        assertEquals(0, (long)repository.count().block());
    }

    @Test
    public void updateVideoEventTest() {
        String ownerId = "1";
        String description = "description";
        String title = "title";
        String locationUrl = "locationUrl";


        sendCreateVideoEvent(ownerId, description, title, locationUrl);
        assertEquals(1, (long)repository.count().block());

        String videoId = repository.findAllByOwnerId(ownerId).blockFirst().getId();

        VideoPayload updateVideoPayload = new VideoPayload();

        updateVideoPayload.setTitle("new-title");
        updateVideoPayload.setDescription("new-description");

        sendUpdateVideoEvent(videoId, updateVideoPayload);

        assertEquals(1, (long)repository.count().block());
        assertEquals("new-title", repository.findAllByOwnerId(ownerId).blockFirst().getTitle());
        assertEquals("new-description", repository.findAllByOwnerId(ownerId).blockFirst().getDescription());
    }

    private void sendCreateVideoEvent(String ownerId, String description, String title, String locationUrl) {
        VideoPayload videoPayload = new VideoPayload();

        videoPayload.setOwnerId(ownerId);
        videoPayload.setDescription(description);
        videoPayload.setLocationUrl(locationUrl);
        videoPayload.setTitle(title);
        videoPayload.setTags(null);

        Event<String, VideoPayload> event = new Event(CREATE, "", videoPayload);
        input.send(new GenericMessage<>(event));
    }

    private void sendDeleteVideoEvent(String videoId) {
        Event<String, VideoPayload> event = new Event(DELETE, videoId, "");
        input.send(new GenericMessage<>(event));
    }

    private void sendUpdateVideoEvent(String videoId, VideoPayload data) {
        Event<String, VideoPayload> event = new Event(UPDATE, videoId, data);
        input.send(new GenericMessage<>(event));
    }
}
