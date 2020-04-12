package com.reactive.example.messages.repository;

import com.reactive.example.messages.model.Message;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface MessageRepository extends ReactiveMongoRepository<Message, Message.MessageId> {

}
