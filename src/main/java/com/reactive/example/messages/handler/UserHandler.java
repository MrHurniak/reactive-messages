package com.reactive.example.messages.handler;

import com.reactive.example.messages.component.validator.impl.SpringValidationHandler;
import com.reactive.example.messages.dto.UserCreateDto;
import com.reactive.example.messages.dto.UserDto;
import com.reactive.example.messages.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Component
@RequiredArgsConstructor
public class UserHandler {

    private static final String USERS_PATH_PREFIX = "/application/api/users";

    private final SpringValidationHandler validationHandler;
    private final UserService userService;

    public Mono<ServerResponse> createUser(ServerRequest request) {
        Mono<UserDto> createdUser = request.bodyToMono(UserCreateDto.class)
                .doOnNext(validationHandler::handleRequest)
                .flatMap(userService::createUser);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(createdUser, UserDto.class);
    }

    public Mono<ServerResponse> getUser(ServerRequest request) {
        String userLogin = request.pathVariable("login");
        Mono<UserDto> user = userService.getUserByLogin(userLogin);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(user, UserDto.class);
    }

    @Bean
    public RouterFunction<ServerResponse> userRoutes(UserHandler handler) {
        return RouterFunctions.route()
                .path(USERS_PATH_PREFIX, builder -> builder
                        .POST("register", accept(MediaType.APPLICATION_JSON), handler::createUser)
                        //TODO create login endpoint
                        .POST("login", accept(MediaType.APPLICATION_JSON), request -> ServerResponse.ok().build())
                        .GET("{login}", handler::getUser)
                )
                .build();
    }

}
