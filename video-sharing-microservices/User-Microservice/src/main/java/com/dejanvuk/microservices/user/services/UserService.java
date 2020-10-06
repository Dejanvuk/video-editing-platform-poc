package com.dejanvuk.microservices.user.services;

import com.dejanvuk.microservices.api.user.User;
import com.dejanvuk.microservices.user.mappers.UserEntityMapper;
import com.dejanvuk.microservices.user.payload.SignUpPayload;
import com.dejanvuk.microservices.user.persistence.*;
import com.dejanvuk.microservices.user.utility.JwtTokenUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static reactor.core.publisher.Mono.error;

import java.util.Collections;

@Service
public class UserService implements IUserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    JwtTokenUtility jwtTokenUtility;

    @Autowired
    UserEntityMapper userEntityMapper;

    @Override
    public Mono<UserEntity> create(SignUpPayload signUpPayload) {

        UserEntity user = new UserEntity();
        user.setUsername(signUpPayload.getUsername());
        user.setEmail(signUpPayload.getEmail());
        user.setName(signUpPayload.getName());
        user.setPassword(passwordEncoder.encode(signUpPayload.getPassword()));

        user.setVerified(false);
        user.setToken(jwtTokenUtility.generateTokenByEmail(signUpPayload.getEmail()));

        return roleRepository.findByName(RoleType.ROLE_USER).flatMap(role -> {
            user.setRoles(Collections.singleton(role));
            System.out.println(user);
            return userRepository.save(user);
        });
    }

    @Override
    public Mono<User> findById(String id) {
        return userRepository.findById(id).map(e -> userEntityMapper.userEntityToUserResponse(e));
    }

    @Override
    public Flux<User> findByName(String name) {
        return null;
    }

    @Override
    public Mono<User> findByUsernameOrEmail(String name) {
        return null;
    }

    @Override
    public Flux<User> findAll() {
        return null;
    }

    @Override
    public Mono<Void> delete(Long id) {
        return null;
    }

    @Override
    public Mono<Boolean> checkForDuplicates(SignUpPayload signUpPayload) {
        return userRepository.existsByUsernameOrEmail(signUpPayload.getUsername(), signUpPayload.getEmail());
    }


}
