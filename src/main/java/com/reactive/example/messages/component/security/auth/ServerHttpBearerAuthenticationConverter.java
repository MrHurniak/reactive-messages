package com.reactive.example.messages.component.security.auth;

import com.reactive.example.messages.component.security.token.JwtSecurityComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class ServerHttpBearerAuthenticationConverter implements ServerAuthenticationConverter {

    public static final String BEARER_PREFIX = "Bearer ";
    private final JwtSecurityComponent jwtSecurityComponent;

    @Override
    //TODO too much complicated construction. Simplify!
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        List<String> authHeaders = headers.get(HttpHeaders.AUTHORIZATION);
        if (CollectionUtils.isNotEmpty(authHeaders)) {
            Optional<String> token = authHeaders.stream()
                    .filter(header -> header.startsWith(BEARER_PREFIX))
                    .map(header -> header.replaceAll(BEARER_PREFIX, ""))
                    .findFirst();
            if (token.isEmpty()) {
                return Mono.empty();
            }
            try {
                boolean tokenValid = jwtSecurityComponent.isTokenValid(token.get());
                if (tokenValid) {
                    Mono<Authentication> authentication = jwtSecurityComponent.getAuthentication(token.get());
                    exchange.mutate().principal(authentication.map(authentication1 -> (Principal) authentication1.getPrincipal()));
                    return authentication;
                }
            } catch (Exception ex) {
                log.info("Invalid token '{}'", token.get(), ex);
                return Mono.empty();
            }
        }
        return Mono.empty();
    }
}
