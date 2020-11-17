package com.dejanvuk.microservices.user;

import com.dejanvuk.microservices.user.persistence.Role;
import com.dejanvuk.microservices.user.persistence.RoleRepository;
import com.dejanvuk.microservices.user.persistence.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import java.util.TimeZone;
import java.util.stream.Stream;


@SpringBootApplication
@EnableMongoAuditing
@ComponentScan("com.dejanvuk.microservices")
public class UserApplication implements CommandLineRunner {

    @Autowired
    RoleRepository roleRepository;

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
    
    @Override
    public void run(String... args) throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        Stream.of("ROLE_ADMIN", "ROLE_USER").forEach(roleName -> {
            Role role = new Role();
            role.setName(RoleType.valueOf(roleName));
            roleRepository.save(role).block();
        });
    }
}
