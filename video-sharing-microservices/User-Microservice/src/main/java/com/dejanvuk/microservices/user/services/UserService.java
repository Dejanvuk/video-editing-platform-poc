package com.dejanvuk.microservices.user.services;

import com.dejanvuk.microservices.user.mappers.UserEntityMapper;
import com.dejanvuk.microservices.user.payload.SignUpPayload;
import com.dejanvuk.microservices.user.persistence.*;
import com.dejanvuk.microservices.user.response.UserResponse;
import com.dejanvuk.microservices.user.utility.JwtTokenUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static reactor.core.publisher.Mono.error;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class UserService implements IUserService{
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
    public void create(SignUpPayload signUpPayload) {
        UserEntity user = new UserEntity();
        user.setUsername(signUpPayload.getUsername());
        user.setEmail(signUpPayload.getEmail());
        user.setName(signUpPayload.getName());
        user.setPassword(passwordEncoder.encode(signUpPayload.getPassword()));

        user.setVerified(false);
        user.setToken(jwtTokenUtility.generateTokenByEmail(signUpPayload.getEmail()));

        Role role = roleRepository.findByName(RoleType.ROLE_USER).block();
        user.setRoles(Collections.singleton(role));

        System.out.println(user);

        userRepository.save(user);
    }

    @Override
    public Mono<UserResponse> findById(String id) {
        return userRepository.findById(id).map(e -> userEntityMapper.userEntityToUserResponse(e));
    }

    @Override
    public Flux<UserResponse> findByName(String name) {
        return null;
    }

    @Override
    public Flux<UserResponse> findAll() {
        return null;
    }

    @Override
    public Mono<Void> delete(Long id) {
        return null;
    }

    @Override
    public ResponseEntity<Mono<List<String>>> checkForDuplicates(SignUpPayload signUpPayload) {
        List<String> duplicatesList= new ArrayList<String>();
        if(userRepository.existsByUsername(signUpPayload.getUsername()).block()) {
            duplicatesList.add("Username is already taken!");
        }
        if(userRepository.existsByEmail(signUpPayload.getEmail()).block()) {
            duplicatesList.add("Email is already taken!");
        }
        return (!duplicatesList.isEmpty()) ? new ResponseEntity<>(Mono.just(duplicatesList), HttpStatus.UNPROCESSABLE_ENTITY) : null;
    }


}
