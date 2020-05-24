package com.reactive.example.messages.component.security.auth;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

public class ServerHttpBearerAuthenticationConverter implements ServerAuthenticationConverter {

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        List<String> authHeaders = headers.get(HttpHeaders.AUTHORIZATION);
        if (CollectionUtils.isNotEmpty(authHeaders) && CollectionUtils.containsAny(authHeaders, "Bearer security_token")) {
            return getAuth(exchange);
        }
        return Mono.empty();
    }

    // This is temporary mock.
    private Mono<Authentication> getAuth(ServerWebExchange exchange) {

        return Mono.just(new Authentication() {

            @Override
            public String getName() {
                return "Test_user";
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of(() -> "TEST_AUTHORITY");
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
            public Object getPrincipal() {
                return null;
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }
        });
    }
}
