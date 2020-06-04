package com.reactive.example.messages.handler.message;

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
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.UUID;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Component
@RequiredArgsConstructor
public class UserMessageManagementHandler {

    private static final String USER_MESSAGES_MANAGEMENT_PATH_PREFIX = "/application/api/user/message";

    private final MessageService messageService;
    private final UserService userService;
    private final SpringValidationHandler validationHandler;
    private final UuidProvider uuidProvider;

    public Mono<ServerResponse> saveMessage(ServerRequest request) {
        Mono<String> userId = request.principal().map(Principal::getName);
        Mono<MessageCreateDto> messageCreateDto = request.bodyToMono(MessageCreateDto.class);
        Mono<MessageDto> savedMessage = Mono.zip(userId, messageCreateDto)
                .doOnNext(validationHandler::handleRequest)
                .doOnNext(tuple2 -> userService.checkIfExistByLogin(tuple2.getT1()))
                .flatMap(tuple2 -> messageService.saveMessage(tuple2.getT1(), tuple2.getT2()));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(savedMessage, MessageDto.class);
    }

    public Mono<ServerResponse> updateMessage(ServerRequest request) {
        UUID messageId = uuidProvider.parse(request.pathVariable("message-id"));
        Mono<String> userId = request.principal().map(Principal::getName);
        Mono<MessageCreateDto> messageCreateDto = request.bodyToMono(MessageCreateDto.class);
        Mono<MessageDto> updatedMessage = Mono.zip(userId, messageCreateDto)
                .doOnNext(validationHandler::handleRequest)
                .doOnNext(tuple2 -> userService.checkIfExistByLogin(tuple2.getT1()))
                .flatMap(tuple2 -> messageService.updateMessage(tuple2.getT1(), messageId, tuple2.getT2()));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updatedMessage, MessageDto.class);
    }

    public Mono<ServerResponse> deleteMessage(ServerRequest request) {
        UUID messageId = uuidProvider.parse(request.pathVariable("message-id"));
        Mono<Void> deleteMessage = request.principal().map(Principal::getName)
                .doOnNext(userService::checkIfExistByLogin)
                .flatMap(login -> messageService.deleteMessage(login, messageId));
        return ServerResponse.noContent()
                .build(deleteMessage);
    }


    @Bean
    public RouterFunction<ServerResponse> messageManagementRoutes(UserMessageManagementHandler handler) {
        return RouterFunctions.route()
                .path(USER_MESSAGES_MANAGEMENT_PATH_PREFIX, builder -> builder
                        .POST("", accept(MediaType.APPLICATION_JSON), handler::saveMessage)
                        .PUT("{message-id}", accept(MediaType.APPLICATION_JSON), handler::updateMessage)
                        .DELETE("{message-id}", handler::deleteMessage)
                )
                .build();
    }
}
