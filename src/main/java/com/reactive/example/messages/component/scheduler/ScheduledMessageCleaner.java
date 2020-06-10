package com.reactive.example.messages.component.scheduler;

import com.reactive.example.messages.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
public class ScheduledMessageCleaner {

    private final MessageService messageService;
    private final long messageLiveDurabilityMinutes;

    public ScheduledMessageCleaner(
            MessageService messageService,
            @Value("${app.messages.live.minutes:30}") long messageLiveDurabilityMinutes
    ) {
        this.messageService = messageService;
        this.messageLiveDurabilityMinutes = messageLiveDurabilityMinutes;
    }

    @Scheduled(fixedDelayString = "${app.messages.clear.task.period:PT30M}")
    void clearMessages() {
        Instant beforeTime = Instant.now().plus(messageLiveDurabilityMinutes, ChronoUnit.MINUTES);
        messageService.deleteAllMessagesOlderThan(beforeTime)
                .subscribe(count -> log.info("Deleted {} old messages before '{}'", count, beforeTime));
    }
}
