package com.example.blooddonor.data.models;

public class BloodRequest {
    private int id;
    private int submittedBy;
    private String patientName;
    private int locationId;
    private String bloodType;
    private String possibleDonors;
    private String createdAt;
    private String deadline;

    public BloodRequest(int id, int submittedBy, String patientName, int locationId, String bloodType, String possibleDonors, String createdAt, String deadline) {
        this.id = id;
        this.submittedBy = submittedBy;
        this.patientName = patientName;
        this.locationId = locationId;
        this.bloodType = bloodType;
        this.possibleDonors = possibleDonors;
        this.createdAt = createdAt;
        this.deadline = deadline;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(int submittedBy) {
        this.submittedBy = submittedBy;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getPossibleDonors() {
        return possibleDonors;
    }

    public void setPossibleDonors(String possibleDonors) {
        this.possibleDonors = possibleDonors;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
}