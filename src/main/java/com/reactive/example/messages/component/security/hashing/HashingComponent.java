package com.reactive.example.messages.component.security.hashing;

public interface HashingComponent {

    String hash(String plainText);

    boolean isEquals(String plainText, String hash);
}
