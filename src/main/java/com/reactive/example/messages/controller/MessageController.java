package com.reactive.example.messages.controller;

import com.reactive.example.messages.dto.MessageCreateDto;
import com.reactive.example.messages.dto.MessageDto;
import com.reactive.example.messages.dto.MessageUpdateDto;
import com.reactive.example.messages.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/application/api/messages", produces = MediaType.APPLICATION_JSON_VALUE)
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/{message-id}")
    public Publisher<MessageDto> getAllMessages(@PathVariable("message-id") UUID messageId) {
        return messageService.getById(messageId);
    }

    @GetMapping("/author/{author-id}")
    public Publisher<MessageDto> getAllAuthorMessages(@PathVariable("author-id") UUID authorId) {
        return messageService.getAllByAuthorId(authorId);
    }

    @PostMapping
    public Publisher<MessageDto> saveMessage(@Valid @RequestBody MessageCreateDto message) {
        return messageService.saveMessage(message);
    }

    @PatchMapping("/{message-id}")
    public Publisher<MessageDto> updateMessage(
            @PathVariable("message-id") UUID messageId,
            @Valid @RequestBody MessageUpdateDto messageDto
    ) {
        return messageService.updateMessage(messageId, messageDto);
    }

    @DeleteMapping("/{message-id}")
    public Publisher<Void> deleteMessage(@PathVariable("message-id") UUID messageId) {
        return messageService.deleteMessage(messageId);
    }
}
