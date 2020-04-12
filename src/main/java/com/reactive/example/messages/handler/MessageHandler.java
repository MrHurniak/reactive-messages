package com.reactive.example.messages.handler;

import com.reactive.example.messages.dto.MessageCreateDto;
import com.reactive.example.messages.dto.MessageDto;
import com.reactive.example.messages.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Component
@RequiredArgsConstructor
public class MessageHandler {

    private final MessageService messageService;

    //TODO change for 'created'
    public Mono<ServerResponse> saveMessage(ServerRequest request) {
        UUID userId = UUID.fromString(request.pathVariable("user-id"));
        Mono<MessageDto> savedMessage = request.bodyToMono(MessageCreateDto.class)
                .flatMap(message -> messageService.saveMessage(userId, message));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(savedMessage, MessageDto.class);
    }

    public Mono<ServerResponse> getMessageById(ServerRequest request) {
        UUID userId = UUID.fromString(request.pathVariable("user-id"));
        UUID messageId = UUID.fromString(request.pathVariable("message-id"));
        Mono<MessageDto> message = messageService.getById(userId, messageId);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(message, MessageDto.class);
    }

    public Mono<ServerResponse> updateMessage(ServerRequest request) {
        UUID userId = UUID.fromString(request.pathVariable("user-id"));
        UUID messageId = UUID.fromString(request.pathVariable("message-id"));
        Mono<MessageDto> updatedMessage = request.bodyToMono(MessageCreateDto.class)
                .flatMap(message -> messageService.updateMessage(userId, messageId, message));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updatedMessage, MessageDto.class);
    }

    public Mono<ServerResponse> deleteMessage(ServerRequest request) {
        UUID userId = UUID.fromString(request.pathVariable("user-id"));
        UUID messageId = UUID.fromString(request.pathVariable("message-id"));
        return ServerResponse.noContent()
                .build(messageService.deleteMessage(userId, messageId));
    }


    @Bean
    public RouterFunction<ServerResponse> messageRoutes(MessageHandler handler) {
        return RouterFunctions.route()
                .path("/application/api/users/{user-id}/messages", builder -> builder
                        .POST("", accept(MediaType.APPLICATION_JSON), handler::saveMessage)
                        .GET("{message-id}", handler::getMessageById)
                        .PUT("{message-id}", accept(MediaType.APPLICATION_JSON), handler::updateMessage)
                        .DELETE("{message-id}", handler::deleteMessage)
                )
                .build();
    }
}
