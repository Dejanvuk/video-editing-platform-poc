package com.dejanvuk.microservices.user.services;

import com.dejanvuk.microservices.user.config.CustomUserDetails;
import com.dejanvuk.microservices.user.persistence.UserEntity;
import com.dejanvuk.microservices.user.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

import static reactor.core.publisher.Mono.error;

@Service
public class CustomReactiveUserDetailsService implements ReactiveUserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    public Mono<UserDetails> findByUsername(String s) {
        Mono<CustomUserDetails> customUser = userRepository.findByUsernameOrEmail(s, s).switchIfEmpty(error(() -> new UsernameNotFoundException("Username or email dont exist! " + s))).log().map(
                user -> {
                    Set<GrantedAuthority> authorities = user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toSet());
                    return new CustomUserDetails(user.getId(),user.getName(), user.getUsername(), user.getEmail(), user.getPassword(),user.getVerified(), authorities, null);
                }
        );
        // needs testing
        return customUser.cast(UserDetails.class);
    }
}
