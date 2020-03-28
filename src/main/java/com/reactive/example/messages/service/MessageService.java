package com.reactive.example.messages.service;

import com.reactive.example.messages.dto.MessageCreateDto;
import com.reactive.example.messages.dto.MessageDto;
import com.reactive.example.messages.dto.MessageUpdateDto;
import com.reactive.example.messages.exception.NotFound;
import com.reactive.example.messages.mapper.MessageMapper;
import com.reactive.example.messages.model.Message;
import com.reactive.example.messages.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageMapper messageMapper;
    private final MessageRepository messageRepository;

    public Mono<MessageDto> getById(UUID messageId) {
        return messageRepository.findById(messageId)
                .log()
                .map(messageMapper::from)
                .switchIfEmpty(Mono.error(new NotFound()));
    }

    public Flux<MessageDto> getAllByAuthorId(UUID authorId) {
        return messageRepository.findAllByAuthorId(authorId)
                .switchIfEmpty(Mono.error(new NotFound()))
                .log()
                .map(messageMapper::from);
    }

    public Mono<MessageDto> saveMessage(MessageCreateDto messageCreateDto) {
        return Mono.just(
                messageMapper.to(messageCreateDto)
                        .setId(UUID.randomUUID())
                        .setUpdateDate(Instant.now()))
                .flatMap(messageRepository::save)
                .log()
                .map(messageMapper::from)
                .switchIfEmpty(Mono.error(new RuntimeException("Could not save message")));
    }

    public Mono<MessageDto> updateMessage(UUID messageId, MessageUpdateDto messageUpdateDto) {
        return messageRepository.findById(messageId)
                .map(message -> new Message()
                        .setId(messageId)
                        .setAuthorId(message.getAuthorId())
                        .setMessageText(messageUpdateDto.getMessage())
                        .setUpdateDate(Instant.now()))
                .flatMap(messageRepository::save)
                .log()
                .switchIfEmpty(Mono.error(new RuntimeException("Message do not exist!")))
                .map(messageMapper::from);
    }

    public Mono<Void> deleteMessage(UUID messageId) {
        return messageRepository.deleteById(messageId);
    }
}
