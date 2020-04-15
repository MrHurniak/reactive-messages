package com.reactive.example.messages.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Document(collection = "messages")
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    @Id
    private MessageId id;

    private String messageText;

    private Instant updateDate;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MessageId implements Serializable {
        private String userId;
        private UUID messageId;
    }

}
