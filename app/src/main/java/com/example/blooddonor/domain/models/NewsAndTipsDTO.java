package com.example.blooddonor.domain.models;

import android.os.Parcel;
import android.os.Parcelable;

public class NewsAndTipsDTO implements Parcelable {
    private int id;
    private String title;
    private String text;
    private String createdDate;

    public NewsAndTipsDTO() {}

    public NewsAndTipsDTO(int id, String title, String text, String createdDate) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.createdDate = createdDate;
    }

    protected NewsAndTipsDTO(Parcel in) {
        id = in.readInt();
        title = in.readString();
        text = in.readString();
        createdDate = in.readString();
    }

    public static final Creator<NewsAndTipsDTO> CREATOR = new Creator<NewsAndTipsDTO>() {
        @Override
        public NewsAndTipsDTO createFromParcel(Parcel in) {
            return new NewsAndTipsDTO(in);
        }

        @Override
        public NewsAndTipsDTO[] newArray(int size) {
            return new NewsAndTipsDTO[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(text);
        dest.writeString(createdDate);
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}