package com.dejanvuk.microservices.user;

import com.dejanvuk.microservices.user.persistence.Role;
import com.dejanvuk.microservices.user.persistence.UserEntity;
import com.dejanvuk.microservices.user.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Set;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ExtendWith(SpringExtension.class)
@AutoConfigureDataMongo
@SpringBootTest
public class PersistenceTests {

    @Autowired
    UserRepository repository;

    UserEntity savedEntity;

    @BeforeEach
    public void setup() {
        StepVerifier.create(repository.deleteAll()).verifyComplete();

        UserEntity userEntity = getRandomEntity("username", "email", "name", " password", "token", null);

        StepVerifier.create(repository.save(userEntity))
                .expectNextMatches(createdEntity -> {
                    savedEntity = createdEntity;
                    return userEntity.equals(savedEntity);
                })
                .verifyComplete();
    }

    @Test
    public void create() {
        UserEntity userEntity = getRandomEntity("username", "email", "name", " password", "token", null);

        StepVerifier.create(repository.save(userEntity))
                .expectNextMatches(createdEntity -> userEntity.getEmail().equals(createdEntity.getEmail()))
                .verifyComplete();

        StepVerifier.create(repository.findById(userEntity.getId()))
                .expectNextMatches(foundEntity -> foundEntity.getEmail().equals(userEntity.getEmail()))
                .verifyComplete();

        StepVerifier.create(repository.count()).expectNext(2l).verifyComplete();
    }

    @Test
    public void update() {
        savedEntity.setPassword("new-password");

        StepVerifier.create(repository.save(savedEntity))
                .expectNextMatches(updatedEntity -> updatedEntity.getPassword().equals("new-password"))
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

    private UserEntity getRandomEntity(String username, String email, String name, String password, String token, Set<Role> roles) {
        UserEntity userEntity = new UserEntity();

        userEntity.setRoles(roles);
        userEntity.setEmail(email);
        userEntity.setName(name);
        userEntity.setToken(token);
        userEntity.setUsername(username);
        userEntity.setPassword(password);

        return userEntity;
    }

}
