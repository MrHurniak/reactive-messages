package com.reactive.example.messages.component.security.encryption.impl;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.subtle.Hex;
import com.reactive.example.messages.component.security.encryption.EncryptionComponent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class AeadEncryptionComponent implements EncryptionComponent {

    private final Aead aead;

    @Override
    @SneakyThrows
    public String encrypt(String plainText) {
        byte[] encryptedText = aead.encrypt(StringUtils.getBytes(plainText, StandardCharsets.UTF_8), null);
        return Hex.encode(encryptedText);
    }

    @Override
    @SneakyThrows
    public String decrypt(String cipherText) {
        byte[] decryptedText = aead.decrypt(Hex.decode(cipherText), null);
        return new String(decryptedText, StandardCharsets.UTF_8);
    }
}
