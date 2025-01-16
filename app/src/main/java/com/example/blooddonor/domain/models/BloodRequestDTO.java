package com.example.blooddonor.domain.models;

import android.os.Parcel;
import android.os.Parcelable;

public class BloodRequestDTO implements Parcelable {
    private int id;
    private int submittedBy;
    private String patientName;
    private int locationId;
    private String bloodType;
    private String possibleDonors;
    private String createdAt;
    private String deadline;
    private String locationName;

    public BloodRequestDTO() {}

    public BloodRequestDTO(int id, int submittedBy, String patientName, int locationId, String bloodType, String possibleDonors, String createdAt, String deadline) {
        this.id = id;
        this.submittedBy = submittedBy;
        this.patientName = patientName;
        this.locationId = locationId;
        this.bloodType = bloodType;
        this.possibleDonors = possibleDonors;
        this.createdAt = createdAt;
        this.deadline = deadline;
    }

    protected BloodRequestDTO(Parcel in) {
        id = in.readInt();
        submittedBy = in.readInt();
        patientName = in.readString();
        locationId = in.readInt();
        bloodType = in.readString();
        possibleDonors = in.readString();
        createdAt = in.readString();
        deadline = in.readString();
        locationName = in.readString();
    }

    public static final Creator<BloodRequestDTO> CREATOR = new Creator<BloodRequestDTO>() {
        @Override
        public BloodRequestDTO createFromParcel(Parcel in) {
            return new BloodRequestDTO(in);
        }

        @Override
        public BloodRequestDTO[] newArray(int size) {
            return new BloodRequestDTO[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(submittedBy);
        dest.writeString(patientName);
        dest.writeInt(locationId);
        dest.writeString(bloodType);
        dest.writeString(possibleDonors);
        dest.writeString(createdAt);
        dest.writeString(deadline);
        dest.writeString(locationName);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
}