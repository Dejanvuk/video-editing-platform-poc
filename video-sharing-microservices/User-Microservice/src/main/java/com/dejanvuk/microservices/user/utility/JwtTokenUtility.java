package com.dejanvuk.microservices.user.utility;

import com.dejanvuk.microservices.user.config.CustomUserDetails;
import com.dejanvuk.microservices.user.config.JwtProperties;
import com.dejanvuk.microservices.user.persistence.UserEntity;
import com.dejanvuk.microservices.user.persistence.UserRepository;
import com.dejanvuk.microservices.user.response.Status;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtility {
    @Autowired
    JwtProperties jwtProps;
    private static final Logger log = LoggerFactory.getLogger(JwtTokenUtility.class);

    @Autowired
    UserRepository userRepository;

    public String generateToken(Authentication authentication) {
        Date expireDate = new Date(new Date().getTime() + jwtProps.getEXPIRATION_TIME());
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        String token = Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .setAudience(jwtProps.getTOKEN_AUDIENCE())
                .setIssuer(jwtProps.getTOKEN_ISSUER())
                .signWith(SignatureAlgorithm.HS512, jwtProps.getJWT_SECRET())
                .compact();
        return token;
    }

    public String generateTokenByEmail(String email) {
        Date expireDate = new Date(new Date().getTime() + jwtProps.getEXPIRATION_TIME());
        String token = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .setAudience(jwtProps.getTOKEN_AUDIENCE())
                .setIssuer(jwtProps.getTOKEN_ISSUER())
                .signWith(SignatureAlgorithm.HS512, jwtProps.getJWT_SECRET())
                .compact();
        return token;
    }

    /*
    public String validateToken(String token) {
        String status = "";

        Optional<UserEntity> optionalUser = userRepository.findUserByToken(token);

        if(optionalUser.isPresent()) {
            if(!(isTokenExpired(token))) {
                UserEntity user = optionalUser.get();
                user.setToken(null);
                user.setVerified(true);
                userRepository.save(user);
                status = Status.IS_VALID.name();
            }
            else status = Status.TOKEN_EXPIRED.name();
        }
        else status = Status.ERROR.name();

        return status;
    }
    */


    public Boolean isTokenExpired(String token) {
        var jwtBody = Jwts.parser().setSigningKey(jwtProps.getJWT_SECRET()).parseClaimsJws(token).getBody();
        Date expirationTime = jwtBody.getExpiration();
        Date todayTime = new Date();
        return expirationTime.before(todayTime);
    }
}
