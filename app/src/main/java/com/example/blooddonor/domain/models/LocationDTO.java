package com.example.blooddonor.domain.models;

import android.os.Parcel;
import android.os.Parcelable;

public class LocationDTO implements Parcelable {
    private int id;
    private String name;
    private String phoneNumbers;
    private String location;
    private int locationTypeId;
    private double longitude;
    private double latitude;

    public LocationDTO() {}

    public LocationDTO(int id, String name, String phoneNumbers, String location, int locationTypeId, double longitude, double latitude) {
        this.id = id;
        this.name = name;
        this.phoneNumbers = phoneNumbers;
        this.location = location;
        this.locationTypeId = locationTypeId;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    protected LocationDTO(Parcel in) {
        id = in.readInt();
        name = in.readString();
        phoneNumbers = in.readString();
        location = in.readString();
        locationTypeId = in.readInt();
        longitude = in.readDouble();
        latitude = in.readDouble();
    }

    public static final Creator<LocationDTO> CREATOR = new Creator<LocationDTO>() {
        @Override
        public LocationDTO createFromParcel(Parcel in) {
            return new LocationDTO(in);
        }

        @Override
        public LocationDTO[] newArray(int size) {
            return new LocationDTO[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(phoneNumbers);
        dest.writeString(location);
        dest.writeInt(locationTypeId);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
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