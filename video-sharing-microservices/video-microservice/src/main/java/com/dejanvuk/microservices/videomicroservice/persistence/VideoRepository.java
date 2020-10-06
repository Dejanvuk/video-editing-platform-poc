package com.dejanvuk.microservices.videomicroservice.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface VideoRepository extends ReactiveMongoRepository<VideoEntity, String> {
    Flux<VideoEntity> findAllByOwnerId(String ownerId);

    Mono<Void> deleteById(String videoId);

}
