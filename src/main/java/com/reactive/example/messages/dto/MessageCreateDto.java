package com.reactive.example.messages.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class MessageCreateDto {

    @NotBlank
    private String message;

    @NotNull
    private UUID authorId;
}
