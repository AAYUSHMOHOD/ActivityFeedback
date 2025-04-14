package com.example.ad_pbl_activityfeedback_q5.models;

public class User {
    private String userId;
    private String email;
    private String name;
    private String role;
    private String rollNumber; // For students
    private String professorId; // For professors

    public User() {
        // Required for Firebase
    }

    public User(String userId, String email, String name, String role) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.role = role;
    }

    // Constructor with roll number for students
    public User(String userId, String email, String name, String role, String rollNumber) {
        this(userId, email, name, role);
        this.rollNumber = rollNumber;
    }

    // Constructor with professorId for professors
    public User(String userId, String email, String name, String role, String professorId, boolean isProfessor) {
        this(userId, email, name, role);
        this.professorId = professorId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isProfessor() {
        return "professor".equals(role);
    }

    public boolean isStudent() {
        return "student".equals(role);
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getProfessorId() {
        return professorId;
    }

    public void setProfessorId(String professorId) {
        this.professorId = professorId;
    }
}