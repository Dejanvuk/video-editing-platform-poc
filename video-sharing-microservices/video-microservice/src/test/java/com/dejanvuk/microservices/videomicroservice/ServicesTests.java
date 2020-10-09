package com.dejanvuk.microservices.videomicroservice;

import com.dejanvuk.microservices.api.comment.Comment;
import com.dejanvuk.microservices.api.video.Video;
import com.dejanvuk.microservices.videomicroservice.controller.VideoController;
import com.dejanvuk.microservices.videomicroservice.persistence.VideoEntity;
import com.dejanvuk.microservices.videomicroservice.persistence.VideoRepository;
import com.dejanvuk.microservices.videomicroservice.services.IVideoService;
import com.dejanvuk.microservices.videomicroservice.services.VideoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment=RANDOM_PORT, properties = {"spring.data.mongodb.port=0"})
@ExtendWith(MockitoExtension.class)
public class ServicesTests {
    @Autowired
    WebTestClient client;

    @Autowired
    private VideoRepository repository;

    @BeforeEach
    public void setupDb() {

        repository.deleteAll().block();
    }

    @Test
    public void getVideosTest() {
        String ownerId = "1";
        insertRandomEntity(ownerId, "description1", "title1", "location1");
        insertRandomEntity(ownerId, "description2", "title2", "location2");

        client.get()
                .uri("/videos")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(OK)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBodyList(Video.class).hasSize(2);
    }


    @Test
    public void getOwnerVideosTest(){
        String ownerId = "1";
        insertRandomEntity(ownerId, "description1", "title1", "location1");
        insertRandomEntity(ownerId, "description2", "title2", "location2");

        client.get()
                .uri("/videos/" + ownerId + "/users")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(OK)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBodyList(Video.class).hasSize(2);
    }

    @Test
    public void getVideoCommentsTest(){

        VideoService videoService = Mockito.mock(VideoService.class);

        String videoId = "1";
        Comment comment1 = new Comment();
        Comment comment2 = new Comment();
        Comment comment3 = new Comment();
        Flux<Comment> commentFlux = Flux.just(comment1, comment2, comment3);
        Mockito.when(videoService.getVideoComments(videoId)).thenReturn(commentFlux);


        WebTestClient mockedClient = WebTestClient.bindToController(new VideoController(videoService)).build();

        mockedClient.get()
                .uri("/videos/" + videoId + "/comments")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(OK)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBodyList(Video.class).hasSize(3);

        Mockito.verify(videoService).getVideoComments(videoId);
    }

    private VideoEntity insertRandomEntity(String ownerId, String description, String title, String locationUrl) {
        VideoEntity videoEntity = new VideoEntity();
        videoEntity.setOwnerId(ownerId);
        videoEntity.setLocationUrl(locationUrl);
        videoEntity.setTitle(title);
        videoEntity.setDescription(description);
        videoEntity.setTags(null);
        videoEntity.setLikes(0);
        videoEntity.setDislikes(0);

        return repository.save(videoEntity).block();
    }

}
