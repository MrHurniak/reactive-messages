package com.reactive.example.messages.service;

import com.reactive.example.messages.dto.MessageCreateDto;
import com.reactive.example.messages.dto.MessageDto;
import com.reactive.example.messages.mapper.MessageMapper;
import com.reactive.example.messages.model.Message;
import com.reactive.example.messages.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageMapper messageMapper;
    private final MessageRepository messageRepository;

    public Mono<MessageDto> getById(UUID userId, UUID messageId) {
        return messageRepository.findById(new Message.MessageId(userId, messageId))
                .map(messageMapper::from);
    }

    public Mono<MessageDto> saveMessage(UUID userId, MessageCreateDto messageToSave) {
        return Mono.just(
                messageMapper.to(messageToSave, new Message.MessageId(userId, UUID.randomUUID()))
                        .setUpdateDate(Instant.now()))
                .flatMap(messageRepository::save)
                .map(messageMapper::from);
    }

    public Mono<MessageDto> updateMessage(UUID userId, UUID messageId, MessageCreateDto messageToUpdate) {
        Message.MessageId id = new Message.MessageId(userId, messageId);
        return messageRepository.findById(id)
                .map(message -> message.setMessageText(messageToUpdate.getMessage())
                        .setUpdateDate(Instant.now())
                )
                .flatMap(messageRepository::save)
                .map(messageMapper::from);
    }

    public Mono<Void> deleteMessage(UUID userId, UUID messageId) {
        return messageRepository.deleteById(new Message.MessageId(userId, messageId));
    }
}
