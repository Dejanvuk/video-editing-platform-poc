package com.dejanvuk.microservices.user.utility;

import com.dejanvuk.microservices.user.config.CustomUserDetails;
import com.dejanvuk.microservices.user.persistence.UserRepository;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtility {
    @Value("${jwt.EXPIRATION_TIME}")
    private Long EXPIRATION_TIME;

    @Value("${jwt.TOKEN_AUDIENCE}")
    private String TOKEN_AUDIENCE;

    @Value("${jwt.TOKEN_ISSUER}")
    private String TOKEN_ISSUER;

    @Value("${jwt.JWT_SECRET}")
    private String JWT_SECRET;

    private static final Logger log = LoggerFactory.getLogger(JwtTokenUtility.class);

    @Autowired
    UserRepository userRepository;

    public String generateToken(String usernameOrEmail) {
        Date expireDate = new Date(new Date().getTime() + EXPIRATION_TIME);

        String token = Jwts.builder()
                .setSubject(usernameOrEmail)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .setAudience(TOKEN_AUDIENCE)
                .setIssuer(TOKEN_ISSUER)
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
        return token;
    }

    public String generateTokenByEmail(String email) {
        Date expireDate = new Date(new Date().getTime() + EXPIRATION_TIME);

        String token = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .setAudience(TOKEN_AUDIENCE)
                .setIssuer(TOKEN_ISSUER)
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
        return token;
    }

    public Mono<UsernamePasswordAuthenticationToken> getUsernamePasswordAuthenticationTokenFromJwt(String jwt) {
        try {
            var jwtBody = Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(jwt.replace("Bearer ", "")).getBody();
            String username = jwtBody.getSubject();
            return userRepository.findByUsernameOrEmail(username, username).switchIfEmpty(Mono.error(new UsernameNotFoundException("Username or email dont exist! " + username)))
                    .flatMap(user -> {
                        Set<GrantedAuthority> roles = user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toSet());
                        CustomUserDetails currentUser = new CustomUserDetails(user.getId(), user.getName(), user.getUsername(), user.getEmail(), user.getPassword(), user.getVerified(), roles, null);
                        return Mono.just(new UsernamePasswordAuthenticationToken(currentUser, null, roles));
                    });
        } catch (ExpiredJwtException exception) {
            log.warn("Request to parse expired JWT : {} failed : {}", jwt, exception.getMessage());
        } catch (UnsupportedJwtException exception) {
            log.warn("Request to parse unsupported JWT : {} failed : {}", jwt, exception.getMessage());
        } catch (MalformedJwtException exception) {
            log.warn("Request to parse invalid JWT : {} failed : {}", jwt, exception.getMessage());
        } catch (SignatureException exception) {
            log.warn("Request to parse JWT with invalid signature : {} failed : {}", jwt, exception.getMessage());
        } catch (IllegalArgumentException exception) {
            log.warn("Request to parse empty or null JWT : {} failed : {}", jwt, exception.getMessage());
        }
        return null;
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
        var jwtBody = Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token).getBody();
        Date expirationTime = jwtBody.getExpiration();
        Date todayTime = new Date();
        return expirationTime.before(todayTime);
    }

    /*
    Date getDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        return cal.getTime();
    }
    */
}
