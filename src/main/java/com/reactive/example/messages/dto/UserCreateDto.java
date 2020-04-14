package com.reactive.example.messages.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserCreateDto {

    private String login;
    private String password;
}
