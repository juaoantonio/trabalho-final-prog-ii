package br.com.joaobarbosa.shared.utils;

import java.util.UUID;

public class CodeGenerator {
    public static String generateCode(int length) {
        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        return uuid.substring(0, Math.min(length, uuid.length()));
    }
}
