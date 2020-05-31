package com.reactive.example.messages.component.security.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.security.Key;
import java.security.Principal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class JwtSecurityComponent {

    private static final String ROLES_KEY = "ROLES";
    private static final String USERNAME_KEY = "username";
    //TODO move to KeyStore or something move secure ¯\_(ツ)_/¯ (JKS)
    private static final String TEMP_SECRET_KEY = "2Mmhq2Dtwrhl3pDnOQyO1qIKvF94h2mxGQfDZvBqoPHi6yX3Khx03JUkVfoAcUhuC1tVpLyrr84MfYsdOzDikcAWr6UrNOxJbbw3kdlx9XfDoCtIgjuoKXFMIjI4tf8iS9NjRLoV6CvfeJi1NgyvSOWRczn1WB5PEzvjSG6lbSRm9IZXvEwPzqpdxIqtJYnwIv2T7huobPx4l0g8bT9JxvDMXkI2zBjHF698HzEkvVmD6cCj2jfkonjoqu1hNkcq";
    private final Key secretKey = Keys.hmacShaKeyFor(TEMP_SECRET_KEY.getBytes());

    public String generateToken(@NotNull UserDetails user) {
        log.info("Generate token for User '{}'", user.getUsername());
        HashMap<String, Object> claims = new HashMap<>();
        claims.put(USERNAME_KEY, user.getUsername());
        claims.put(ROLES_KEY, user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList())
        );
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)))
                .signWith(secretKey)
                .compact();
    }

    public boolean isTokenValid(@NotNull String token) {
        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
        Date expiration = claimsJws.getBody().getExpiration();
        return expiration != null && expiration.after(Date.from(Instant.now()));
    }

    public Mono<Authentication> getAuthentication(@NotNull String token) {
        Claims body = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Mono.just(new Authentication() {

            @Override
            public String getName() {
                return body.get(USERNAME_KEY, String.class);
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return Stream.of(body.get(ROLES_KEY, String[].class))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
            }

            @Override
            public Principal getPrincipal() {
                return this;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) {
                throw new UnsupportedOperationException();
            }
        });
    }
}
