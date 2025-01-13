package com.example.blooddonor.utils;

import java.util.Random;

public class VerificationCodeGenerator {
    private static final int CODE_LENGTH = 6;

    public static String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }

        return code.toString();
    }
}