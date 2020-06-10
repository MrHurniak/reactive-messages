package com.reactive.example.messages.event.message;

import com.reactive.example.messages.dto.MessageDto;
import com.reactive.example.messages.event.EventType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
@EqualsAndHashCode(callSuper = false)
public class MessageEvent extends ApplicationEvent {

    private final EventType eventType;

    public MessageEvent(EventType eventType, MessageDto message) {
        super(message);
        this.eventType = eventType;
    }
}
