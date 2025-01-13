package com.example.blooddonor.utils;

public class EmailTemplates {
    public static String getVerificationEmailTemplate(String code, String username) {
        return "Dear " + username + ",\n\n" +
                "We have received a request to reset your password. Use the following verification code to proceed:\n\n" +
                "Verification Code: " + code + "\n\n" +
                "If you did not make this request, please ignore this email.\n\n" +
                "Thank you,\n" +
                "Blood Donor Team";
    }

    public static String getSubject() {
        return "Password Reset Verification Code";
    }
}