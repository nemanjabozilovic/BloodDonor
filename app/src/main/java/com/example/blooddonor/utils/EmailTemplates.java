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

    public static String getNewUserWelcomeTemplate(String username) {
        return "Dear " + username + ",\n\n" +
                "Welcome to the Blood Donor platform! We are thrilled to have you on board.\n\n" +
                "If you have any questions or need assistance, please reach out to our support team at support@blooddonor.com.\n\n" +
                "Thank you for joining us in this life-saving mission!\n\n" +
                "Best regards,\n" +
                "Blood Donor Team";
    }

    public static String getVerificationSubject() {
        return "Password Reset Verification Code";
    }

    public static String getWelcomeSubject() {
        return "Welcome to Blood Donor Platform!";
    }
}