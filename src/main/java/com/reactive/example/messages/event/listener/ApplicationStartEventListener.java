package com.reactive.example.messages.event.listener;

import com.reactive.example.messages.model.Message;
import com.reactive.example.messages.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@Profile("test_start")
@RequiredArgsConstructor
public class ApplicationStartEventListener implements ApplicationListener<ApplicationReadyEvent> {


    private final MessageRepository repository;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        repository.save(new Message().setId(UUID.randomUUID())
                .setMessageText("Test message")
                .setAuthorId(UUID.randomUUID()))
                .subscribe(message -> log.info("Saved {}", message));
    }
}
