package com.example.blooddonor.utils;

public class EmailTemplates {

    public static String getVerificationEmailTemplate(String code, String userName) {
        return "Dear " + userName + ",\n\n" +
                "We have received a request to reset your password. Use the following verification code to proceed:\n\n" +
                "Verification Code: " + code + "\n\n" +
                "If you did not make this request, please ignore this email.\n\n" +
                "Thank you,\n" +
                "Blood Donor Team 🩸";
    }

    public static String getNewUserWelcomeTemplate(String userName) {
        return "Dear " + userName + ",\n\n" +
                "Welcome to the 🩸 Blood Donor 🩸 platform! We are thrilled to have you on board.\n\n" +
                "If you have any questions or need assistance, please reach out to our support team at support@blooddonor.com.\n\n" +
                "Thank you for joining us in this life-saving mission!\n\n" +
                "Best regards,\n" +
                "Blood Donor Team 🩸";
    }

    public static String getBloodRequestCreatedTemplate(
            String userName,
            String locationName,
            String phoneNumbers,
            String location,
            String patientName,
            String patientBloodType,
            String possibleDonors,
            String deadline
    ) {
        return "Dear " + userName + ",\n\n" +
                "A new blood donation request has been created. Here are the details:\n\n" +
                "🔹 Patient Name: " + patientName + "\n" +
                "🩸 Blood Type Needed: " + patientBloodType + "\n\n" +
                "🏥 Location Name: " + locationName + "\n" +
                "📞 Contact Number(s): " + phoneNumbers + "\n" +
                "📌 Address: " + location + "\n\n" +
                "🩸 Possible Donors: " + possibleDonors + "\n" +
                "⏳ Deadline for Donation: " + (deadline != null && !deadline.isEmpty() ? deadline : "ASAP") + "\n\n" +
                "If you are available and meet the criteria, please consider donating blood to help save a life.\n\n" +
                "Thank you for your life-saving contribution!\n\n" +
                "Best regards,\n" +
                "Blood Donor Team 🩸";
    }

    public static String getVerificationSubject() {
        return "Password Reset Verification Code";
    }

    public static String getWelcomeSubject() {
        return "Welcome to Blood Donor Platform!";
    }

    public static String getBloodRequestSubject() {
        return "📢: New Blood Request Created";
    }
}