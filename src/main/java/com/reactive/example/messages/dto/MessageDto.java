package com.reactive.example.messages.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class MessageDto {

    private UUID id;

    private String message;

    private UUID userId;

    private Instant updateDate;

}
