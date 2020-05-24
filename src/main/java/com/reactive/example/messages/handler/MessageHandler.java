package com.reactive.example.messages.handler;

import com.reactive.example.messages.component.provider.UuidProvider;
import com.reactive.example.messages.component.validator.impl.SpringValidationHandler;
import com.reactive.example.messages.dto.MessageCreateDto;
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

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Component
@RequiredArgsConstructor
public class MessageHandler {

    private static final String MESSAGES_PATH_PREFIX = "/application/api/users/{user-login}/messages";

    private final MessageService messageService;
    private final UserService userService;
    private final SpringValidationHandler validationHandler;
    private final UuidProvider uuidProvider;

    public Mono<ServerResponse> saveMessage(ServerRequest request) {
        String userLogin = request.pathVariable("user-login");
        Mono<MessageDto> savedMessage = request.bodyToMono(MessageCreateDto.class)
                .doOnNext(validationHandler::handleRequest)
                .flatMap(message -> userService.checkIfExistByLogin(userLogin)
                        .then(messageService.saveMessage(userLogin, message)));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(savedMessage, MessageDto.class);
    }

    public Mono<ServerResponse> getMessageById(ServerRequest request) {
        String userLogin = request.pathVariable("user-login");
        UUID messageId = uuidProvider.parse(request.pathVariable("message-id"));
        Mono<MessageDto> message = userService.checkIfExistByLogin(userLogin)
                .then(messageService.getById(userLogin, messageId));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(message, MessageDto.class);
    }

    public Mono<ServerResponse> getAllUserMessages(ServerRequest request) {
        String userLogin = request.pathVariable("user-login");
        Flux<MessageDto> messages = userService.checkIfExistByLogin(userLogin)
                .thenMany(messageService.getAllUserMessages(userLogin));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(messages, MessageDto.class);
    }

    public Mono<ServerResponse> updateMessage(ServerRequest request) {
        String userLogin = request.pathVariable("user-login");
        UUID messageId = uuidProvider.parse(request.pathVariable("message-id"));
        Mono<MessageDto> updatedMessage = request.bodyToMono(MessageCreateDto.class)
                .doOnNext(validationHandler::handleRequest)
                .flatMap(message -> userService.checkIfExistByLogin(userLogin)
                        .then(messageService.updateMessage(userLogin, messageId, message)));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updatedMessage, MessageDto.class);
    }

    public Mono<ServerResponse> deleteMessage(ServerRequest request) {
        String userLogin = request.pathVariable("user-login");
        UUID messageId = uuidProvider.parse(request.pathVariable("message-id"));
        Mono<Void> deleteMessage = userService.checkIfExistByLogin(userLogin)
                .then(messageService.deleteMessage(userLogin, messageId));
        return ServerResponse.noContent()
                .build(deleteMessage);
    }


    @Bean
    public RouterFunction<ServerResponse> messageRoutes(MessageHandler handler) {
        return RouterFunctions.route()
                .path(MESSAGES_PATH_PREFIX, builder -> builder
                        .POST("", accept(MediaType.APPLICATION_JSON), handler::saveMessage)
                        .GET("", handler::getAllUserMessages)
                        .GET("{message-id}", handler::getMessageById)
                        .PUT("{message-id}", accept(MediaType.APPLICATION_JSON), handler::updateMessage)
                        .DELETE("{message-id}", handler::deleteMessage)
                )
                .build();
    }
}
