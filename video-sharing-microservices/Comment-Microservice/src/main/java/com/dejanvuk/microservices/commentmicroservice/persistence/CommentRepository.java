package com.dejanvuk.microservices.commentmicroservice.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CommentRepository extends ReactiveMongoRepository<CommentEntity, String> {
    Flux<CommentEntity> findAllByVideoId(String videoId);

    Flux<CommentEntity> findAllByOwnerId(String ownerId);

    Mono<Void> deleteById(String id);
}
