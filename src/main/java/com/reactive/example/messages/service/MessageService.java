package com.reactive.example.messages.service;

import com.reactive.example.messages.dto.MessageCreateDto;
import com.reactive.example.messages.dto.MessageDto;
import com.reactive.example.messages.dto.MessageUpdateDto;
import com.reactive.example.messages.mapper.MessageMapper;
import com.reactive.example.messages.model.Message;
import com.reactive.example.messages.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageMapper messageMapper;
    private final MessageRepository messageRepository;

    public Mono<MessageDto> getById(UUID messageId) {
        return messageRepository.findById(messageId)
                .map(messageMapper::from);
    }

    public Flux<MessageDto> getAllByAuthorId(UUID authorId) {
        return messageRepository.findAllByAuthorId(authorId)
                .map(messageMapper::from);
    }

    public Mono<MessageDto> saveMessage(MessageCreateDto messageCreateDto) {
        Message message = messageMapper.to(messageCreateDto)
                .setId(UUID.randomUUID())
                .setUpdateDate(Instant.now());
        return messageRepository.save(message)
                .map(messageMapper::from);
    }

    //TODO add check on Mono#empty
    public Mono<MessageDto> updateMessage(UUID messageId, MessageUpdateDto messageUpdateDto) {
        return messageRepository.findById(messageId)
                .doOnSuccess(m -> {
                    Message message = new Message()
                            .setId(messageId)
                            .setAuthorId(m.getAuthorId())
                            .setMessageText(messageUpdateDto.getMessage())
                            .setUpdateDate(Instant.now());
                    messageRepository.save(message);
                })
                .map(messageMapper::from);
    }

    public Mono<Void> deleteMessage(UUID messageId) {
        return messageRepository.deleteById(messageId);
    }
}
