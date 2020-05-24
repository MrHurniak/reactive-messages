package com.reactive.example.messages.config;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.JsonKeysetReader;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadConfig;
import com.reactive.example.messages.component.security.encryption.EncryptionComponent;
import com.reactive.example.messages.component.security.encryption.impl.AeadEncryptionComponent;
import com.reactive.example.messages.component.security.hashing.HashingComponent;
import com.reactive.example.messages.component.security.hashing.impl.SecuredHashingComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class PrivateDataSecurityConfig {

    private static final String AEAD_KEYSET_FILENAME = "aead_keyset.json";

    @Bean
    public PasswordEncoder passwordEncoder() {
        String idForEncode = "bcrypt";

        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(idForEncode, new BCryptPasswordEncoder());
        encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());
        encoders.put("scrypt", new SCryptPasswordEncoder());
        encoders.put("sha256", new StandardPasswordEncoder());

        return new DelegatingPasswordEncoder(idForEncode, encoders);
    }

    @Bean
    public HashingComponent SecuredHashingComponent(PasswordEncoder passwordEncoder) {
        return new SecuredHashingComponent(passwordEncoder);
    }

    /**
     * Keyset file must be generated and places in root folder. To generate keyset use next code snippet
     * {@code
     * KeysetHandle keysetHandle = KeysetHandle.generateNew(AeadKeyTemplates.AES128_EAX)
     * CleartextKeysetHandle.write(keysetHandle, JsonKeysetWriter.withFile(new File(AEAD_KEYSET_FILENAME)));
     * }
     */
    @Bean
    public EncryptionComponent aeadEncryptionComponent() throws Exception {
        AeadConfig.register();
        KeysetHandle read = CleartextKeysetHandle.read(JsonKeysetReader.withFile(new File(AEAD_KEYSET_FILENAME)));
        return new AeadEncryptionComponent(read.getPrimitive(Aead.class));
    }
}
