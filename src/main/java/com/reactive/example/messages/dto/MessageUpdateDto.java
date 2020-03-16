package com.reactive.example.messages.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MessageUpdateDto {

    @NotBlank
    private String message;

}
