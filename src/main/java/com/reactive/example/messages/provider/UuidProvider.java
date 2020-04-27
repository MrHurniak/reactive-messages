package com.reactive.example.messages.provider;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.regex.Pattern;

@Component
public class UuidProvider {

    private static final Pattern uuidPattern = Pattern.compile("([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})");

    public UUID parse(String value) {
        if (StringUtils.isNotBlank(value) && uuidPattern.matcher(value).matches()) {
            return UUID.fromString(value);
        }
        throw new IllegalArgumentException(String.format("Cannot parse '%s' to UUID", value));
    }
}
