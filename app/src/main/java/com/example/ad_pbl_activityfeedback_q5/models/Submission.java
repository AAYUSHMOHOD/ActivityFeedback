package com.example.ad_pbl_activityfeedback_q5.models;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.ad_pbl_activityfeedback_q5.utils.DateTimeConverter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Submission {
    private String submissionId;
    private String formId;
    private String studentId;
    private LocalDateTime submittedAt;
    private String studentRollNumber;
    private Map<String, Object> answers;

    public Submission() {
        // Required for Firebase
        answers = new HashMap<>();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Submission(String submissionId, String formId, String studentId, String studentRollNumber) {
        this.submissionId = submissionId;
        this.formId = formId;
        this.studentId = studentId;
        this.studentRollNumber = studentRollNumber;
        this.submittedAt = DateTimeConverter.now(); // Use IST
        this.answers = new HashMap<>();
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(String submissionId) {
        this.submissionId = submissionId;
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

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getStudentRollNumber() {
        return studentRollNumber;
    }

    public void setStudentRollNumber(String studentRollNumber) {
        this.studentRollNumber = studentRollNumber;
    }

    public Map<String, Object> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<String, Object> answers) {
        this.answers = answers;
    }

    public void addAnswer(String questionId, Object answer) {
        answers.put(questionId, answer);
    }
}