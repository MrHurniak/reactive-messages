package com.reactive.example.messages.component.security.encryption;

public interface EncryptionComponent {

    String encrypt(String plainText);

    String decrypt(String encryptedText);
}
