package com.reactive.example.messages.config;

import com.reactive.example.messages.component.security.auth.BearerTokenReactiveAuthenticationManager;
import com.reactive.example.messages.component.security.auth.ServerHttpBearerAuthenticationConverter;
import com.reactive.example.messages.component.security.token.JwtSecurityComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@EnableWebFluxSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity httpSecurity,
            JwtSecurityComponent jwtSecurityComponent
    ) {
        httpSecurity
                .httpBasic().disable()
                .formLogin().disable()
                .csrf().disable()
                .logout().disable()
                .cors().disable();

        httpSecurity
                .authorizeExchange()
                .pathMatchers(
                        "/application/api/users/login",
                        "/application/api/users/register",
                        //TODO remove
                        "/ws/messages"
                ).permitAll()
                .anyExchange().authenticated()
                .and()
                .addFilterAt(bearerAuthenticationFilter(jwtSecurityComponent), SecurityWebFiltersOrder.AUTHENTICATION);

        return httpSecurity.build();
    }

    //TODO what is going here?
    private AuthenticationWebFilter bearerAuthenticationFilter(JwtSecurityComponent jwtSecurityComponent) {
        var authManager = new BearerTokenReactiveAuthenticationManager();
        var bearerAuthFilter = new AuthenticationWebFilter(authManager);
        var authConverter = new ServerHttpBearerAuthenticationConverter(jwtSecurityComponent);

        bearerAuthFilter.setServerAuthenticationConverter(authConverter);
        bearerAuthFilter.setRequiresAuthenticationMatcher(
                ServerWebExchangeMatchers.pathMatchers("/**")
        );

        return bearerAuthFilter;
    }
}
