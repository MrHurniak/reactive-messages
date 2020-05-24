package com.reactive.example.messages.dto;

import com.reactive.example.messages.component.validator.custom.annotation.Email;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Accessors(chain = true)
public class UserCreateDto {

    @NotBlank
    @Size(min = 5, max = 25)
    private String login;

    @NotBlank
    @Size(min = 8, max = 50)
    private String password;

    @Email(isNullable = true)
    private String email;
}
