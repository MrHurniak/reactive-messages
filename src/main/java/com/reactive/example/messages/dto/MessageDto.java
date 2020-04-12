package com.reactive.example.messages.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class MessageDto {

    private UUID id;

    private String message;

    private UUID userId;

    private Instant updateDate;

}
