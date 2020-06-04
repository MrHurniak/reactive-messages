package com.reactive.example.messages.handler.message;

import com.reactive.example.messages.component.provider.UuidProvider;
import com.reactive.example.messages.dto.MessageDto;
import com.reactive.example.messages.service.MessageService;
import com.reactive.example.messages.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GeneralMessageHandler {

    private static final String MESSAGES_PATH_PREFIX = "/application/api/messages";

    private final MessageService messageService;
    private final UserService userService;
    private final UuidProvider uuidProvider;

    public Mono<ServerResponse> getMessageById(ServerRequest request) {
        String userId = request.pathVariable("user-id");
        UUID messageId = uuidProvider.parse(request.pathVariable("message-id"));
        Mono<MessageDto> message = userService.checkIfExistByLogin(userId)
                .then(messageService.getById(userId, messageId));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(message, MessageDto.class);
    }

    public Mono<ServerResponse> getAllUserMessages(ServerRequest request) {
        String userId = request.pathVariable("user-id");
        Flux<MessageDto> messages = userService.checkIfExistByLogin(userId)
                .thenMany(messageService.getAllUserMessages(userId));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(messages, MessageDto.class);
    }

    public Mono<ServerResponse> getAllMessagesPageable(ServerRequest request) {
        int page = request.queryParam("page")
                .map(Integer::parseInt)
                .filter(p -> p >= 0)
                .orElse(0);
        int count = request.queryParam("count")
                .map(Integer::parseInt)
                .filter(c -> c > 0)
                .orElse(10);
        Flux<MessageDto> messages = messageService.getAllMessagesPageable(page, count);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(messages, MessageDto.class);
    }

    @Bean
    public RouterFunction<ServerResponse> messageRoutes(GeneralMessageHandler handler) {
        return RouterFunctions.route()
                .path(MESSAGES_PATH_PREFIX, builder -> builder
                        .GET("/{message-id}/user/{user-id}", handler::getMessageById)
                        .GET("/user/{user-id}", handler::getAllUserMessages)
                        .GET("", handler::getAllMessagesPageable)
                )
                .build();
    }
}
