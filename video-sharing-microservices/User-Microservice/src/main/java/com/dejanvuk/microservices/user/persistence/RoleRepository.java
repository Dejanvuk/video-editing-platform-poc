package com.dejanvuk.microservices.user.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface RoleRepository extends ReactiveMongoRepository<Role, Long> {
    Mono<Role> findByName(RoleType roleName);
}
