package com.dejanvuk.microservices.user.persistence;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository {
    Optional<Role> findByName(RoleType roleName);
}
