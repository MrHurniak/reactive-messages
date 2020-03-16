package com.reactive.example.messages.repository;

import com.reactive.example.messages.model.Message;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface MessageRepository extends ReactiveMongoRepository<Message, UUID> {

    Flux<Message> findAllByAuthorId(UUID author);
}
