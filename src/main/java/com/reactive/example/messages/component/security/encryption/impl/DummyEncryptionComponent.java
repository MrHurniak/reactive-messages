package com.reactive.example.messages.component.security.encryption.impl;

import com.reactive.example.messages.component.security.encryption.EncryptionComponent;

public class DummyEncryptionComponent implements EncryptionComponent {

    @Override
    public String encrypt(String plainText) {
        return plainText;
    }

    @Override
    public String decrypt(String cipherText) {
        return cipherText;
    }
}
