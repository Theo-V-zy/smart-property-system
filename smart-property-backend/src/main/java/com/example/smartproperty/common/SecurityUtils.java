package com.example.smartproperty.common;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static String md5(String raw) {
        return DigestUtils.md5DigestAsHex(raw.getBytes(StandardCharsets.UTF_8));
    }
}
