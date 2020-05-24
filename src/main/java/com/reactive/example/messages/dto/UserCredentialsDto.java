package com.reactive.example.messages.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class UserCredentialsDto {

    @NotBlank
    private String login;

    @NotBlank
    private String password;
}
