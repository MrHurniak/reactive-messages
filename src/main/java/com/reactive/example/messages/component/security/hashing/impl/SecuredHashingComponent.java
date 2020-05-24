package com.reactive.example.messages.component.security.hashing.impl;

import com.reactive.example.messages.component.security.hashing.HashingComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class SecuredHashingComponent implements HashingComponent {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String hash(String plainText) {
        return passwordEncoder.encode(plainText);
    }

    @Override
    public boolean isEquals(String plainText, String hash) {
        return passwordEncoder.matches(plainText, hash);
    }
}
