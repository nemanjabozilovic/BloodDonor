package com.example.blooddonor.domain.models;

import android.os.Parcel;
import android.os.Parcelable;

public class FaqDTO implements Parcelable {
    private int id;
    private String question;
    private String answer;

    public FaqDTO() {}

    public FaqDTO(int id, String question, String answer) {
        this.id = id;
        this.question = question;
        this.answer = answer;
    }

    protected FaqDTO(Parcel in) {
        id = in.readInt();
        question = in.readString();
        answer = in.readString();
    }

    public static final Creator<FaqDTO> CREATOR = new Creator<FaqDTO>() {
        @Override
        public FaqDTO createFromParcel(Parcel in) {
            return new FaqDTO(in);
        }

        @Override
        public FaqDTO[] newArray(int size) {
            return new FaqDTO[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(question);
        dest.writeString(answer);
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

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}