package com.reactive.example.messages.service;

import com.mongodb.client.result.DeleteResult;
import com.reactive.example.messages.dto.MessageCreateDto;
import com.reactive.example.messages.dto.MessageDto;
import com.reactive.example.messages.event.message.MessageEvent;
import com.reactive.example.messages.exception.NotFoundException;
import com.reactive.example.messages.mapper.MessageMapper;
import com.reactive.example.messages.model.Message;
import com.reactive.example.messages.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

import static com.reactive.example.messages.event.EventType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageMapper messageMapper;
    private final MessageRepository messageRepository;
    private final ReactiveMongoTemplate mongoTemplate;
    private final ApplicationEventPublisher eventPublisher;

    public Mono<MessageDto> getById(String userId, UUID messageId) {
        return messageRepository.findById(new Message.MessageId(userId, messageId))
                .switchIfEmpty(Mono.error(new NotFoundException("MESSAGE_NOT_FOUND")))
                .map(messageMapper::from);
    }

    public Flux<MessageDto> getAllUserMessages(String userLogin) {
        return mongoTemplate.find(Query.query(Criteria.where("_id.userId").is(userLogin)), Message.class)
                .switchIfEmpty(Mono.error(new NotFoundException("MESSAGE_NOT_FOUND")))
                .map(messageMapper::from);
    }

    public Flux<MessageDto> getAllMessagesPageable(int page, int count) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updateDate");
        return messageRepository.findAll(PageRequest.of(page, count, sort))
                .map(messageMapper::from);
    }

    public Mono<MessageDto> saveMessage(String userId, MessageCreateDto messageToSave) {
        return Mono.just(
                messageMapper.to(messageToSave, new Message.MessageId(userId, UUID.randomUUID()))
                        .setUpdateDate(Instant.now()))
                .flatMap(messageRepository::save)
                .map(messageMapper::from)
                .doOnNext(message -> eventPublisher.publishEvent(new MessageEvent(ADD, message)));
    }

    public Mono<MessageDto> updateMessage(String userId, UUID messageId, MessageCreateDto messageToUpdate) {
        Message.MessageId id = new Message.MessageId(userId, messageId);
        return messageRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("MESSAGE_NOT_FOUND")))
                .map(message -> message.setMessageText(messageToUpdate.getMessage())
                        .setUpdateDate(Instant.now())
                )
                .flatMap(messageRepository::save)
                .map(messageMapper::from)
                .doOnNext(message -> eventPublisher.publishEvent(new MessageEvent(UPDATE, message)));
    }

    public Mono<Void> deleteMessage(String userId, UUID messageId) {
        return messageRepository.deleteById(new Message.MessageId(userId, messageId))
                .doOnNext(v -> eventPublisher.publishEvent(
                        new MessageEvent(DELETE, new MessageDto().setId(messageId)))
                );
    }

    public Mono<Long> deleteAllMessagesOlderThan(Instant beforeTime) {
        return mongoTemplate.remove(Query.query(Criteria.where("updateDate").lt(beforeTime)), Message.class)
                .map(DeleteResult::getDeletedCount);
    }
}
