package com.reactive.example.messages.repository;

import com.reactive.example.messages.model.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface MessageRepository extends ReactiveMongoRepository<Message, Message.MessageId> {

    default Flux<Message> findAll(Pageable pageable) {
        return findAll(pageable.getSort())
                .skip(pageable.getOffset())
                .take(pageable.getPageSize());
    }
}
