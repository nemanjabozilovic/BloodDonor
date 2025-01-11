package com.example.blooddonor.data.models;

public class User {
    private int id;
    private String fullName;
    private String email;
    private String password;
    private String bloodType;
    private String profilePicture;
    private int roleId;

    public User(int id, String fullName, String email, String password, String bloodType, String profilePicture, int roleId) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.bloodType = bloodType;
        this.profilePicture = profilePicture;
        this.roleId = roleId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }
}