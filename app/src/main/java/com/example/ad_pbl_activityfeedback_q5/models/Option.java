package com.example.ad_pbl_activityfeedback_q5.models;

public class Option {
    private String id;
    private String text;

    public Option() {
        // Required for Firebase
    }

    public Option(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}