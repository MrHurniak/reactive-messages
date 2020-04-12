package com.reactive.example.messages.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MessageCreateDto {

    @NotBlank
    private String message;

}
