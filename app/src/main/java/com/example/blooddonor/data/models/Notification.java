package com.example.blooddonor.data.models;

public class Notification {
    private int id;
    private String title;
    private String text;
    private String createdDate;

    public Notification(int id, String title, String text, String createdDate) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.createdDate = createdDate;
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