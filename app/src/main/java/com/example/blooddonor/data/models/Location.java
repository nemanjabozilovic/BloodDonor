package com.example.blooddonor.data.models;

public class Location {
    private int id;
    private String name;
    private String phoneNumbers;
    private String location;
    private int locationTypeId;
    private double longitude;
    private double latitude;

    public Location(int id, String name, String phoneNumbers, String location, int locationTypeId, double longitude, double latitude) {
        this.id = id;
        this.name = name;
        this.phoneNumbers = phoneNumbers;
        this.location = location;
        this.locationTypeId = locationTypeId;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(String phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getLocationTypeId() {
        return locationTypeId;
    }

    public void setLocationTypeId(int locationTypeId) {
        this.locationTypeId = locationTypeId;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}