package com.dejanvuk.microservices.commentmicroservice;

import com.dejanvuk.microservices.commentmicroservice.persistence.CommentEntity;
import com.dejanvuk.microservices.commentmicroservice.persistence.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@DataMongoTest(properties = {"spring.cloud.config.enabled=false"})
public class PersistenceTest {
    @Autowired
    CommentRepository repository;

    CommentEntity savedEntity;

    @BeforeEach
    public void setupDb() {
        StepVerifier.create(repository.deleteAll()).verifyComplete();

        CommentEntity commentEntity = getRandomEntity();

        StepVerifier.create(repository.save(commentEntity))
                .expectNextMatches(createdEntity -> {
                    savedEntity = createdEntity;
                    return commentEntity.equals(savedEntity);
                })
                .verifyComplete();
    }

    @Test
    public void create() {

        CommentEntity commentEntity = getRandomEntity();

        StepVerifier.create(repository.save(commentEntity))
                .expectNextMatches(createdEntity -> commentEntity.getContent().equals(createdEntity.getContent()))
                .verifyComplete();

        StepVerifier.create(repository.findById(commentEntity.getId()))
                .expectNextMatches(foundEntity -> foundEntity.equals(commentEntity))
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
        CommentEntity entity1 = repository.findById(savedEntity.getId()).block();
        CommentEntity entity2 = repository.findById(savedEntity.getId()).block();

        // Update the entity using the first entity object
        entity1.setNrOfReports(5);
        repository.save(entity1).block();

        //  Update the entity using the second entity object.
        // This should fail since the second entity now holds an old version number, i.e. a Optimistic Lock Error
        StepVerifier.create(repository.save(entity2)).expectError(OptimisticLockingFailureException.class).verify();

        // Get the updated entity from the database and verify its new sate
        StepVerifier.create(repository.findById(savedEntity.getId()))
                .expectNextMatches(foundEntity ->
                        foundEntity.getVersion() == 1 &&
                                foundEntity.getNrOfReports() == 5)
                .verifyComplete();
    }

    private CommentEntity getRandomEntity() {
        CommentEntity commentEntity = new CommentEntity();

        commentEntity.setOwnerId("1");
        commentEntity.setContent("content");
        commentEntity.setVideoId("2");
        commentEntity.setLikes(0);
        commentEntity.setDislikes(0);
        commentEntity.setNrOfReports(0);

        return commentEntity;
    }
}
