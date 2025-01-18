package com.example.blooddonor.domain.models;

import android.os.Parcel;
import android.os.Parcelable;

public class RoleDTO implements Parcelable {
    public static final Creator<RoleDTO> CREATOR = new Creator<RoleDTO>() {
        @Override
        public RoleDTO createFromParcel(Parcel in) {
            return new RoleDTO(in);
        }

        @Override
        public RoleDTO[] newArray(int size) {
            return new RoleDTO[size];
        }
    };
    private int id;
    private String roleName;

    public RoleDTO() {
    }

    public RoleDTO(int id, String roleName) {
        this.id = id;
        this.roleName = roleName;
    }

    protected RoleDTO(Parcel in) {
        id = in.readInt();
        roleName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(roleName);
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

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}