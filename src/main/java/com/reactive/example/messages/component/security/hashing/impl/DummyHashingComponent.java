package com.reactive.example.messages.component.security.hashing.impl;

import com.reactive.example.messages.component.security.hashing.HashingComponent;

import java.util.Objects;

public class DummyHashingComponent implements HashingComponent {

    @Override
    public String hash(String plainText) {
        return plainText;
    }

    @Override
    public boolean isEquals(String plainText, String hash) {
        return Objects.equals(plainText, hash);
    }
}
