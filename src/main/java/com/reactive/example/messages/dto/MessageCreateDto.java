package com.reactive.example.messages.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class MessageCreateDto {

    @NotBlank
    private String message;

}
