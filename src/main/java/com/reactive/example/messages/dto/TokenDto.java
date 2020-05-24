package com.reactive.example.messages.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TokenDto {

    private String token;

}
