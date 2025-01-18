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

    public static String getBloodRequestDeadlineTemplate(
            String userName,
            String patientName,
            String patientBloodType,
            String locationName,
            String phoneNumbers,
            String deadline
    ) {
        return "Dear " + userName + ",\n\n" +
                "⏳ Blood Donation Reminder!\n\n" +
                "The deadline for the following blood donation request is approaching in less than 24 hours:\n\n" +
                "🔹 Patient Name: " + patientName + "\n" +
                "🩸 Required Blood Type: " + patientBloodType + "\n\n" +
                "🏥 Location: " + locationName + "\n" +
                "📞 Contact: " + phoneNumbers + "\n" +
                "⏳ Deadline: " + (deadline != null && !deadline.isEmpty() ? deadline : "ASAP") + "\n\n" +
                "If you are eligible and willing to donate, please consider helping. Your contribution could save a life!\n\n" +
                "Thank you for your generosity and support.\n\n" +
                "Best regards,\n" +
                "Blood Donor Team 🩸";
    }

    public static String getBirthdayTemplate(String userName) {
        return "Dear " + userName + ",\n\n" +
                "🎂 Happy Birthday! 🎉\n\n" +
                "On this special day, we want to take a moment to celebrate you and wish you a day filled with joy, happiness, and love.\n\n" +
                "Thank you for being a valuable part of the Blood Donor community. Your generosity and willingness to help others truly make a difference in the world.\n\n" +
                "We hope you have a fantastic birthday! 🥳🎁\n\n" +
                "Best wishes,\n" +
                "Blood Donor Team 🩸";
    }

    public static String getVerificationSubject() {
        return "Password Reset Verification Code";
    }

    public static String getWelcomeSubject() {
        return "Welcome to Blood Donor Platform!";
    }

    public static String getBloodRequestSubject() {
        return "📢 New Blood Request Created";
    }

    public static String getBloodRequestDeadlineSubject() {
        return "⏳ Urgent: Blood Donation Deadline Approaching";
    }

    public static String getBirthdaySubject() {
        return "🎉 Happy Birthday from Blood Donor Team!";
    }
}