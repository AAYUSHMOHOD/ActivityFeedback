package com.example.ad_pbl_activityfeedback_q5.models;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.ad_pbl_activityfeedback_q5.utils.DateTimeConverter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class Form {
    private String formId;
    private String title;
    private String description;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
    private boolean isActive;
    private Map<String, Question> questions;
    private List<String> allowedRollNumbers; // To store the roll numbers allowed to access this form
    private int minRollNumber; // Minimum roll number in range
    private int maxRollNumber; // Maximum roll number in range

    public Form() {
        // Required for Firebase
        questions = new HashMap<>();
        allowedRollNumbers = new ArrayList<>();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Form(String formId, String title, String description, String createdBy) {
        this.formId = formId;
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
        this.createdAt = DateTimeConverter.now(); // Use IST
        this.lastModified = this.createdAt;
        this.isActive = true;
        this.questions = new HashMap<>();
        this.allowedRollNumbers = new ArrayList<>();
        this.minRollNumber = 0;
        this.maxRollNumber = 0;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Map<String, Question> getQuestions() {
        return questions;
    }

    public void setQuestions(Map<String, Question> questions) {
        this.questions = questions;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addQuestion(String id, Question question) {
        questions.put(id, question);
        this.lastModified = LocalDateTime.now();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void removeQuestion(String id) {
        questions.remove(id);
        this.lastModified = LocalDateTime.now();
    }

    public List<String> getAllowedRollNumbers() {
        return allowedRollNumbers;
    }

    public void setAllowedRollNumbers(List<String> allowedRollNumbers) {
        this.allowedRollNumbers = allowedRollNumbers;
    }

    public void addAllowedRollNumber(String rollNumber) {
        if (allowedRollNumbers == null) {
            allowedRollNumbers = new ArrayList<>();
        }
        allowedRollNumbers.add(rollNumber);
    }

    public int getMinRollNumber() {
        return minRollNumber;
    }

    public void setMinRollNumber(int minRollNumber) {
        this.minRollNumber = minRollNumber;
    }

    public int getMaxRollNumber() {
        return maxRollNumber;
    }

    public void setMaxRollNumber(int maxRollNumber) {
        this.maxRollNumber = maxRollNumber;
    }

    public boolean isRollNumberAllowed(String rollNumber) {
        // Check if specific roll number is in the allowed list
        if (allowedRollNumbers != null && !allowedRollNumbers.isEmpty()) {
            return allowedRollNumbers.contains(rollNumber);
        }

        // Check if roll number is in the allowed range
        if (minRollNumber > 0 && maxRollNumber > 0) {
            try {
                int rollNum = Integer.parseInt(rollNumber);
                return rollNum >= minRollNumber && rollNum <= maxRollNumber;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        // If no restrictions are set, allow access
        return allowedRollNumbers == null || allowedRollNumbers.isEmpty();
    }
}