package com.example.blooddonor.domain.models;

import android.os.Parcel;
import android.os.Parcelable;

public class LocationTypeDTO implements Parcelable {
    public static final Creator<LocationTypeDTO> CREATOR = new Creator<LocationTypeDTO>() {
        @Override
        public LocationTypeDTO createFromParcel(Parcel in) {
            return new LocationTypeDTO(in);
        }

        @Override
        public LocationTypeDTO[] newArray(int size) {
            return new LocationTypeDTO[size];
        }
    };
    private int id;
    private String name;

    public LocationTypeDTO() {
    }

    public LocationTypeDTO(int id, String name) {
        this.id = id;
        this.name = name;
    }

    protected LocationTypeDTO(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}