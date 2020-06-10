package com.reactive.example.messages.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reactive.example.messages.event.message.MessageEvent;
import com.reactive.example.messages.event.publisher.MessageEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Configuration
public class WebSocketConfig {

    @Bean
    public Executor executor() {
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    public HandlerMapping handlerMapping(WebSocketHandler webSocketHandler) {
        return new SimpleUrlHandlerMapping() {
            {
                setUrlMap(Map.of("/ws/messages", webSocketHandler));
                setOrder(10);
            }
        };
    }

    @Bean
    public WebSocketHandlerAdapter webSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    @Bean
    public WebSocketHandler webSocketHandler(ObjectMapper objectMapper, MessageEventPublisher messageEventPublisher) {
        Flux<MessageEvent> publish = Flux.create(messageEventPublisher)
                .share();

        return session -> {
            Flux<WebSocketMessage> messageFlux = publish.map(
                    messageEvent -> {
                        try {
                            return objectMapper.writeValueAsString(messageEvent);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .map(str -> {
                        log.info("sending " + str);
                        return session.textMessage(str);
                    });
            return session.send(messageFlux);
        };
    }
}
