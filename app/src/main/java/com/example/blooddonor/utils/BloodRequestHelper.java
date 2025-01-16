package com.example.blooddonor.utils;

import java.util.Collections;
import java.util.List;

public class BloodRequestHelper {

    public static List<String> getCompatibleDonors(String patientBloodType) {
        if (patientBloodType == null || patientBloodType.isEmpty()) {
            return Collections.emptyList();
        }

        switch (patientBloodType.toUpperCase()) {
            case "A+":
                return List.of("A+", "A-", "O+", "O-");
            case "A-":
                return List.of("A-", "O-");
            case "B+":
                return List.of("B+", "B-", "O+", "O-");
            case "B-":
                return List.of("B-", "O-");
            case "AB+":
                return List.of("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");
            case "AB-":
                return List.of("A-", "B-", "AB-", "O-");
            case "O+":
                return List.of("O+", "O-");
            case "O-":
                return List.of("O-");
            default:
                return Collections.emptyList();
        }
    }
}
