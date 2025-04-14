package com.example.ad_pbl_activityfeedback_q5.models;

public class PasswordResetRequest {
    private String requestId;
    private String studentEmail;
    private String studentRollNumber;
    private long requestedAt;
    private String status; // "pending", "approved", "rejected"

    public PasswordResetRequest() {
        // Required for Firebase
    }

    public PasswordResetRequest(String requestId, String studentEmail, String studentRollNumber, long requestedAt, String status) {
        this.requestId = requestId;
        this.studentEmail = studentEmail;
        this.studentRollNumber = studentRollNumber;
        this.requestedAt = requestedAt;
        this.status = status;
    }

    // Old constructor for backward compatibility
    public PasswordResetRequest(String requestId, String studentEmail, long requestedAt, String status) {
        this.requestId = requestId;
        this.studentEmail = studentEmail;
        this.studentRollNumber = null;
        this.requestedAt = requestedAt;
        this.status = status;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getStudentRollNumber() {
        return studentRollNumber;
    }

    public void setStudentRollNumber(String studentRollNumber) {
        this.studentRollNumber = studentRollNumber;
    }

    public long getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(long requestedAt) {
        this.requestedAt = requestedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}