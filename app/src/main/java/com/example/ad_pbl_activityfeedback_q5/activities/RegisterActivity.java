package com.example.ad_pbl_activityfeedback_q5.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ad_pbl_activityfeedback_q5.R;
import com.example.ad_pbl_activityfeedback_q5.utils.FirebaseHelper;
import com.example.ad_pbl_activityfeedback_q5.utils.ValidationUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText, idEditText;
    private RadioGroup roleRadioGroup;
    private Button registerButton;
    private TextView loginTextView, idLabel;
    private ProgressBar progressBar;
    private LinearLayout idLayout;
    private TextView currentTimeTextView;
    private TextView currentUserTextView;
    private Handler timeUpdateHandler;
    private Runnable timeUpdateRunnable;
    private static final int MIN_ROLL = 42101;
    private static final int MAX_ROLL = 42485;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        nameEditText = findViewById(R.id.name_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        idEditText = findViewById(R.id.id_edit_text);
        roleRadioGroup = findViewById(R.id.role_radio_group);
        registerButton = findViewById(R.id.register_button);
        loginTextView = findViewById(R.id.login_text_view);
        progressBar = findViewById(R.id.progress_bar);
        idLayout = findViewById(R.id.id_layout);
        idLabel = findViewById(R.id.id_label);
        currentTimeTextView = findViewById(R.id.current_time_text_view);
        currentUserTextView = findViewById(R.id.current_user_text_view);
        currentUserTextView.setText("Creating new user account");

        // Initialize time update handler
        timeUpdateHandler = new Handler(Looper.getMainLooper());
        timeUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                updateCurrentTime();
                timeUpdateHandler.postDelayed(this, 1000); // Update every second
            }
        };

        // Setup role change listener
        roleRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRole = findViewById(checkedId);
            if (selectedRole != null) {
                String role = selectedRole.getText().toString().toLowerCase();
                if ("student".equals(role)) {
                    idLabel.setText("Roll Number (" + MIN_ROLL + "-" + MAX_ROLL + "):");
                    idEditText.setHint("Enter your roll number");
                    idEditText.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
                } else {
                    idLabel.setText("Professor ID:");
                    idEditText.setHint("Enter your professor ID");
                    idEditText.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
                }
                idLayout.setVisibility(View.VISIBLE);
            }
        });

        // Setup register button
        registerButton.setOnClickListener(v -> attemptRegistration());

        // Setup login text view
        loginTextView.setOnClickListener(v -> {
            finish(); // Go back to login
        });
    }

    private void updateCurrentTime() {
        // Format current time in YYYY-MM-DD HH:MM:SS format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        String currentTime = sdf.format(new Date());
        currentTimeTextView.setText("Current Date and Time: " + currentTime);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Start time updates
        updateCurrentTime();
        timeUpdateHandler.postDelayed(timeUpdateRunnable, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop time updates
        timeUpdateHandler.removeCallbacks(timeUpdateRunnable);
    }

    private void attemptRegistration() {
        // Check selected role
        int selectedRoleId = roleRadioGroup.getCheckedRadioButtonId();
        if (selectedRoleId == -1) {
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton selectedRole = findViewById(selectedRoleId);
        String role = selectedRole.getText().toString().toLowerCase();

        // Validate inputs
        boolean isValid = true;

        // Name validation
        if (!ValidationUtil.validateName(nameEditText)) {
            isValid = false;
        }

        // Email validation
        if (!ValidationUtil.validateEmail(emailEditText)) {
            isValid = false;
        }

        // Password validation
        if (!ValidationUtil.validatePassword(passwordEditText)) {
            isValid = false;
        }

        // Roll number or Professor ID validation
        if ("student".equals(role)) {
            if (!ValidationUtil.validateRollNumber(idEditText, MIN_ROLL, MAX_ROLL)) {
                isValid = false;
            }
        } else {
            if (!ValidationUtil.validateProfessorId(idEditText)) {
                isValid = false;
            }
        }

        if (!isValid) {
            return; // Don't proceed if validation failed
        }

        // Get values
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        String idValue = idEditText.getText().toString().trim();

        // Show progress
        progressBar.setVisibility(View.VISIBLE);
        registerButton.setEnabled(false);

        // Attempt registration
        FirebaseHelper.registerUser(email, password, name, role, idValue, task -> {
            progressBar.setVisibility(View.GONE);
            registerButton.setEnabled(true);

            if (task.isSuccessful()) {
                Toast.makeText(RegisterActivity.this,
                        "Registration successful", Toast.LENGTH_SHORT).show();

                // Navigate to appropriate dashboard
                Intent intent;
                if ("professor".equals(role)) {
                    intent = new Intent(RegisterActivity.this, ProfessorDashboardActivity.class);
                } else {
                    intent = new Intent(RegisterActivity.this, StudentDashboardActivity.class);
                }
                String userId = FirebaseHelper.getCurrentUserId();
                intent.putExtra("userId", userId);
                intent.putExtra("userName", name);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(RegisterActivity.this,
                        "Registration failed: " + task.getException().getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}