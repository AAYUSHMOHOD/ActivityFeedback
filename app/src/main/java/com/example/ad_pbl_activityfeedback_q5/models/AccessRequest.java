package com.example.ad_pbl_activityfeedback_q5.models;

import android.os.Build;
import androidx.annotation.RequiresApi;

import com.example.ad_pbl_activityfeedback_q5.utils.DateTimeConverter;

import java.time.LocalDateTime;

public class AccessRequest {
    private String requestId;
    private String formId;
    private String studentId;
    private String studentRollNumber;
    private String studentName;
    private String requestType; // "ACCESS", "RESUBMIT", "ACTIVATE"
    private String requestStatus; // "PENDING", "APPROVED", "REJECTED"
    private LocalDateTime requestedAt;
    private LocalDateTime respondedAt;
    private String message; // Optional message from student

    public AccessRequest() {
        // Required for Firebase
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public AccessRequest(String requestId, String formId, String studentId,
                         String studentRollNumber, String studentName, String requestType) {
        this.requestId = requestId;
        this.formId = formId;
        this.studentId = studentId;
        this.studentRollNumber = studentRollNumber;
        this.studentName = studentName;
        this.requestType = requestType;
        this.requestStatus = "PENDING";
        this.requestedAt = DateTimeConverter.now(); // Use IST
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentRollNumber() {
        return studentRollNumber;
    }

    public void setStudentRollNumber(String studentRollNumber) {
        this.studentRollNumber = studentRollNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(LocalDateTime respondedAt) {
        this.respondedAt = respondedAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}