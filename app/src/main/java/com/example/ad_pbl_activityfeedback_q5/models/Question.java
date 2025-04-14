package com.example.ad_pbl_activityfeedback_q5.models;

import java.util.ArrayList;
import java.util.List;

public class Question {
    private String questionId;
    private String type;
    private String text;
    private List<String> options;
    private int min;
    private int max;

    public Question() {
        // Required for Firebase
        options = new ArrayList<>();
        min = 1;
        max = 5;
    }

    public Question(String questionId, String type, String text) {
        this.questionId = questionId;
        this.type = type;
        this.text = text;
        this.options = new ArrayList<>();
        this.min = 1;
        this.max = 5;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public void addOption(String option) {
        if (options == null) {
            options = new ArrayList<>();
        }
        options.add(option);
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}