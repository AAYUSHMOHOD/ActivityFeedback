package com.example.ad_pbl_activityfeedback_q5.utils;

import android.text.TextUtils;
import android.widget.EditText;

import java.util.regex.Pattern;

public class ValidationUtil {

    // Validation patterns
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s]+$");
    private static final Pattern ROLL_NUMBER_PATTERN = Pattern.compile("^[0-9]+$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[A-Z])(?=.*[0-9]).{8,}$"
    );

    public static boolean validateName(EditText editText) {
        String name = editText.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            editText.setError("Name is required");
            return false;
        } else if (!NAME_PATTERN.matcher(name).matches()) {
            editText.setError("Name should contain only letters and spaces");
            return false;
        }
        return true;
    }

    public static boolean validateRollNumber(EditText editText, int minValue, int maxValue) {
        String roll = editText.getText().toString().trim();
        if (TextUtils.isEmpty(roll)) {
            editText.setError("Roll number is required");
            return false;
        } else if (!ROLL_NUMBER_PATTERN.matcher(roll).matches()) {
            editText.setError("Roll number should contain only digits");
            return false;
        } else {
            try {
                int rollNum = Integer.parseInt(roll);
                if (rollNum < minValue || rollNum > maxValue) {
                    editText.setError("Roll number must be between " + minValue + " and " + maxValue);
                    return false;
                }
            } catch (NumberFormatException e) {
                editText.setError("Invalid roll number format");
                return false;
            }
        }
        return true;
    }

    public static boolean validateEmail(EditText editText) {
        String email = editText.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            editText.setError("Email is required");
            return false;
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            editText.setError("Please enter a valid email address");
            return false;
        }
        return true;
    }

    public static boolean validatePassword(EditText editText) {
        String password = editText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            editText.setError("Password is required");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            editText.setError("Password must be at least 8 characters, contain at least 1 uppercase letter and 1 number");
            return false;
        }
        return true;
    }

    public static boolean validateProfessorId(EditText editText) {
        String profId = editText.getText().toString().trim();
        if (TextUtils.isEmpty(profId)) {
            editText.setError("Professor ID is required");
            return false;
        }
        return true;
    }
}