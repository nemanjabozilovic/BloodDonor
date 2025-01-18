package com.example.blooddonor.utils;

import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtils {
    private static final int SALT_LENGTH = 16; // Bytes
    private static final int HASH_ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256; // Bits

    public static String generateSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPassword(String password, String salt) {
        try {
            byte[] saltBytes = Base64.getDecoder().decode(salt);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, HASH_ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error while hashing password", e);
        }
    }

    public static boolean verifyPassword(String password, String salt, String hashedPassword) {
        String calculatedHash = hashPassword(password, salt);
        return calculatedHash.equals(hashedPassword);
    }
}