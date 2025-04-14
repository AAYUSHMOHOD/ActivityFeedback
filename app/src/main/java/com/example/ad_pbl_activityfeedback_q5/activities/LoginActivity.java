package com.example.ad_pbl_activityfeedback_q5.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ad_pbl_activityfeedback_q5.R;
import com.example.ad_pbl_activityfeedback_q5.models.User;
import com.example.ad_pbl_activityfeedback_q5.utils.FirebaseHelper;
import com.example.ad_pbl_activityfeedback_q5.utils.ValidationUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView registerTextView, forgotPasswordTextView;
    private ProgressBar progressBar;

    private TextView currentTimeTextView;
    private TextView currentUserTextView;
    private Handler timeUpdateHandler;
    private Runnable timeUpdateRunnable;

    private void updateCurrentTime() {
        // Format current time in a cleaner format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        String currentTime = sdf.format(new Date());
        currentTimeTextView.setText("Current Date and Time: " + currentTime);
    }

    private void displayCurrentUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // When already logged in, display the email
            currentUserTextView.setText("Current User's Login: " + currentUser.getEmail());
        } else {
            // When not logged in
            currentUserTextView.setText("Current User's Login: Not signed in");
        }
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.login_button);
        registerTextView = findViewById(R.id.register_text_view);
        forgotPasswordTextView = findViewById(R.id.forgot_password_text_view);
        progressBar = findViewById(R.id.progress_bar);
        currentTimeTextView = findViewById(R.id.current_time_text_view);
        currentUserTextView = findViewById(R.id.current_user_text_view);

        // Setup login button
        loginButton.setOnClickListener(v -> attemptLogin());

        // Setup register text view
        registerTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        timeUpdateHandler = new Handler(Looper.getMainLooper());
        timeUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                updateCurrentTime();
                timeUpdateHandler.postDelayed(this, 1000); // Update every second
            }
        };

        // Setup forgot password text view
        forgotPasswordTextView.setOnClickListener(v -> showForgotPasswordDialog());

        // In the onCreate method, change this:
        currentUserTextView = findViewById(R.id.current_user_text_view);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // If a user is already logged in, show their email
            currentUserTextView.setText("Current User: " + currentUser.getEmail());
        } else {
            // If no user is logged in
            currentUserTextView.setText("No user currently logged in");
        }

        displayCurrentUser();
    }

    private void attemptLogin() {
        // Validate inputs
        boolean isValid = true;

        // Email validation
        if (!ValidationUtil.validateEmail(emailEditText)) {
            isValid = false;
        }

        // We don't do strict password validation on login - just check if it's empty
        String password = passwordEditText.getText().toString();
        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        String email = emailEditText.getText().toString().trim();

        // Show progress
        progressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);

        // Attempt login
        FirebaseHelper.loginUser(email, password, task -> {
            progressBar.setVisibility(View.GONE);
            loginButton.setEnabled(true);

            if (task.isSuccessful()) {
                // Get user details and navigate
                FirebaseHelper.getCurrentUser(user -> {
                    if (user != null) {
                        navigateBasedOnRole(user);
                    } else {
                        Toast.makeText(LoginActivity.this,
                                "Error retrieving user data", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(LoginActivity.this,
                        "Login failed: " + task.getException().getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showForgotPasswordDialog() {
        // Create a custom dialog for forgot password
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Password");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot_password, null);
        builder.setView(dialogView);

        EditText resetEmailEditText = dialogView.findViewById(R.id.reset_email_edit_text);
        EditText rollNumberEditText = dialogView.findViewById(R.id.roll_number_edit_text);
        Spinner professorSpinner = dialogView.findViewById(R.id.professor_spinner);
        TextView passwordInfoTextView = dialogView.findViewById(R.id.password_info_text_view);
        ProgressBar dialogProgressBar = dialogView.findViewById(R.id.progress_bar);
        TextView statusTextView = dialogView.findViewById(R.id.status_text_view);

        // Set the password info text
        String rollNumberExample = "42441";
        passwordInfoTextView.setText("When request approved, your password will be set to Pict_RollNo format.\nE.g., Pict_" + rollNumberExample);

        // Clear status initially
        statusTextView.setVisibility(View.GONE);

        // Initialize with empty spinner
        List<User> professorsList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new ArrayList<>());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        professorSpinner.setAdapter(adapter);

        // Load professors list
        dialogProgressBar.setVisibility(View.VISIBLE);

        FirebaseHelper.getAllProfessors(professors -> {
            dialogProgressBar.setVisibility(View.GONE);
            professorsList.clear();
            professorsList.addAll(professors);

            List<String> professorNames = new ArrayList<>();
            for (User prof : professorsList) {
                professorNames.add(prof.getName() + " (" + prof.getProfessorId() + ")");
            }

            adapter.clear();
            adapter.addAll(professorNames);
            adapter.notifyDataSetChanged();
        });

        builder.setPositiveButton("Send Reset Request", null); // We'll override this below
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        // Override the positive button to prevent automatic dismissal
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String email = resetEmailEditText.getText().toString().trim();
            String rollNumber = rollNumberEditText.getText().toString().trim();

            if (!ValidationUtil.validateEmail(resetEmailEditText)) {
                return;
            }

            if (rollNumber.isEmpty()) {
                rollNumberEditText.setError("Roll number is required");
                return;
            }

            if (professorSpinner.getSelectedItem() == null) {
                Toast.makeText(LoginActivity.this, "Please select a professor", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get selected professor
            int selectedPosition = professorSpinner.getSelectedItemPosition();
            User selectedProfessor = professorsList.get(selectedPosition);

            // Show progress
            dialogProgressBar.setVisibility(View.VISIBLE);
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

            // First check if there's already a request
            FirebaseHelper.checkExistingPasswordResetRequest(email, exists -> {
                if (exists) {
                    dialogProgressBar.setVisibility(View.GONE);
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);

                    // Show error message in the dialog
                    statusTextView.setVisibility(View.VISIBLE);
                    statusTextView.setText("You already have a pending password reset request");
                    statusTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                } else {
                    // Send password reset request to selected professor
                    FirebaseHelper.sendPasswordResetRequest(email, rollNumber, selectedProfessor.getUserId(), task -> {
                        dialogProgressBar.setVisibility(View.GONE);
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);

                        if (task.isSuccessful()) {
                            statusTextView.setVisibility(View.VISIBLE);
                            statusTextView.setText("Password reset request sent successfully");
                            statusTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));

                            // Disable the button to prevent sending again
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                            // Auto dismiss after 2 seconds
                            new Handler().postDelayed(dialog::dismiss, 2000);
                        } else {
                            statusTextView.setVisibility(View.VISIBLE);
                            statusTextView.setText("Failed to send request: " +
                                    (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                            statusTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                        }
                    });
                }
            });
        });
    }

    private void navigateBasedOnRole(User user) {
        Intent intent;
        if (user.isProfessor()) {
            intent = new Intent(LoginActivity.this, ProfessorDashboardActivity.class);
        } else {
            intent = new Intent(LoginActivity.this, StudentDashboardActivity.class);
        }
        intent.putExtra("userId", user.getUserId());
        intent.putExtra("userName", user.getName());
        startActivity(intent);
        finish();
    }
}