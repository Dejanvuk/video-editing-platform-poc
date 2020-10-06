package com.dejanvuk.microservices.videomicroservice;

import com.dejanvuk.microservices.videomicroservice.persistence.VideoEntity;
import com.dejanvuk.microservices.videomicroservice.persistence.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@DataMongoTest(properties = {"spring.cloud.config.enabled=false"})
public class PersistenceTests {
    @Autowired
    VideoRepository repository;

    VideoEntity savedEntity;

    @BeforeEach
    public void setupDb() {
        StepVerifier.create(repository.deleteAll()).verifyComplete();

        VideoEntity videoEntity = getRandomEntity();

        StepVerifier.create(repository.save(videoEntity))
                .expectNextMatches(createdEntity -> {
                    savedEntity = createdEntity;
                    return videoEntity.equals(savedEntity);
                })
                .verifyComplete();
    }

    @Test
    public void create() {

        VideoEntity videoEntity = getRandomEntity();

        StepVerifier.create(repository.save(videoEntity))
                .expectNextMatches(createdEntity -> videoEntity.getDescription().equals(createdEntity.getDescription()))
                .verifyComplete();

        StepVerifier.create(repository.findById(videoEntity.getId()))
                .expectNextMatches(foundEntity -> foundEntity.equals(videoEntity))
                .verifyComplete();

        StepVerifier.create(repository.count()).expectNext(2l).verifyComplete();
    }

    @Test
    public void update() {
        savedEntity.setLikes(50);

        StepVerifier.create(repository.save(savedEntity))
                .expectNextMatches(updatedEntity -> updatedEntity.getLikes() == 50)
                .verifyComplete();

        StepVerifier.create(repository.findById(savedEntity.getId()))
                .expectNextMatches(foundEntity -> foundEntity.getVersion() == 1)
                .verifyComplete();
    }

    @Test
    public void delete() {
        StepVerifier.create(repository.delete(savedEntity)).verifyComplete();
        StepVerifier.create(repository.existsById(savedEntity.getId())).expectNext(false).verifyComplete();
    }

    @Test
    public void optimisticLockError() {
        // Store the saved entity in two separate entity objects
        VideoEntity entity1 = repository.findById(savedEntity.getId()).block();
        VideoEntity entity2 = repository.findById(savedEntity.getId()).block();

        // Update the entity using the first entity object
        entity1.setDislikes(5);
        repository.save(entity1).block();

        //  Update the entity using the second entity object.
        // This should fail since the second entity now holds an old version number, i.e. a Optimistic Lock Error
        StepVerifier.create(repository.save(entity2)).expectError(OptimisticLockingFailureException.class).verify();

        // Get the updated entity from the database and verify its new sate
        StepVerifier.create(repository.findById(savedEntity.getId()))
                .expectNextMatches(foundEntity ->
                        foundEntity.getVersion() == 1 &&
                                foundEntity.getDislikes() == 5)
                .verifyComplete();
    }

    private VideoEntity getRandomEntity() {
        VideoEntity videoEntity = new VideoEntity();

        videoEntity.setOwnerId("1");
        videoEntity.setLocationUrl("https://wwww.video-host.com/video-test.wav");
        videoEntity.setTitle("title");
        videoEntity.setDescription("description");
        videoEntity.setTags(null);
        videoEntity.setLikes(0);
        videoEntity.setDislikes(0);

        return videoEntity;
    }
}
