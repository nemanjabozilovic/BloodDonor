package com.example.blooddonor.domain.models;

import android.os.Parcel;
import android.os.Parcelable;

public class UserDTO implements Parcelable {
    public static final Creator<UserDTO> CREATOR = new Creator<UserDTO>() {
        @Override
        public UserDTO createFromParcel(Parcel in) {
            return new UserDTO(in);
        }

        @Override
        public UserDTO[] newArray(int size) {
            return new UserDTO[size];
        }
    };
    private int id;
    private String fullName;
    private String email;
    private String dateOfBirth;
    private String password;
    private String salt;
    private String bloodType;
    private String profilePicture;
    private int roleId;
    private String roleName;
    private int verificationCode;

    public UserDTO() {
    }

    public UserDTO(int id, String fullName, String email, String dateOfBirth, String password, String salt, String bloodType, String profilePicture, int roleId, int verificationCode) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.password = password;
        this.salt = salt;
        this.bloodType = bloodType;
        this.profilePicture = profilePicture;
        this.roleId = roleId;
        this.verificationCode = verificationCode;
    }

    protected UserDTO(Parcel in) {
        id = in.readInt();
        fullName = in.readString();
        email = in.readString();
        dateOfBirth = in.readString();
        password = in.readString();
        salt = in.readString();
        bloodType = in.readString();
        profilePicture = in.readString();
        roleId = in.readInt();
        roleName = in.readString();
        verificationCode = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(fullName);
        dest.writeString(email);
        dest.writeString(dateOfBirth);
        dest.writeString(password);
        dest.writeString(salt);
        dest.writeString(bloodType);
        dest.writeString(profilePicture);
        dest.writeInt(roleId);
        dest.writeString(roleName);
        dest.writeInt(verificationCode);
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

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
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

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public int getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(int verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}