package com.example.ad_pbl_activityfeedback_q5.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ad_pbl_activityfeedback_q5.R;
import com.example.ad_pbl_activityfeedback_q5.adapters.StudentFormAdapter;
import com.example.ad_pbl_activityfeedback_q5.models.AccessRequest;
import com.example.ad_pbl_activityfeedback_q5.models.Form;
import com.example.ad_pbl_activityfeedback_q5.models.User;
import com.example.ad_pbl_activityfeedback_q5.utils.FirebaseHelper;
import com.example.ad_pbl_activityfeedback_q5.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

public class StudentDashboardActivity extends AppCompatActivity {

    private String userId;
    private String userName;
    private String rollNumber;
    private RecyclerView formsRecyclerView;
    private StudentFormAdapter formAdapter;
    private List<Form> formList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyTextView;
    private TextView userNameTextView;
    private TextView currentTimeTextView;
    private Handler timeUpdateHandler;
    private Runnable timeUpdateRunnable;
    private Set<String> submittedFormIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        // Get user data from intent
        userId = getIntent().getStringExtra("userId");
        userName = getIntent().getStringExtra("userName");

        // Set title
        setTitle("Student Dashboard");

        // Initialize views
        formsRecyclerView = findViewById(R.id.forms_recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        emptyTextView = findViewById(R.id.empty_text_view);
        userNameTextView = findViewById(R.id.user_name_text_view);
        currentTimeTextView = findViewById(R.id.current_time_text_view);

        // Set user name
        userNameTextView.setText("Current User: " + userName.toUpperCase());

        // Initialize time update handler
        timeUpdateHandler = new Handler(Looper.getMainLooper());
        timeUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                updateCurrentTime();
                timeUpdateHandler.postDelayed(this, 1000); // Update every second
            }
        };

        // Initialize data structures
        formList = new ArrayList<>();
        submittedFormIds = new HashSet<>();

        // Get student roll number
        FirebaseHelper.getCurrentUser(user -> {
            if (user != null && user.getRollNumber() != null) {
                rollNumber = user.getRollNumber();

                // Setup RecyclerView after getting the roll number
                setupRecyclerView();

                // Load forms
                loadForms();
            } else {
                Toast.makeText(this, "Error: Could not retrieve user information", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Setup SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this::loadForms);
    }

    private void setupRecyclerView() {
        formAdapter = new StudentFormAdapter(this, formList, rollNumber,
                new ArrayList<>(submittedFormIds),
                new StudentFormAdapter.OnFormClickListener() {
                    @Override
                    public void onFormClick(Form form, String status) {
                        handleFormClick(form, status);
                    }

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onRequestAccess(Form form, String requestType) {
                        showRequestAccessDialog(form, requestType);
                    }
                });

        formsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        formsRecyclerView.setAdapter(formAdapter);
    }

    private void handleFormClick(Form form, String status) {
        switch (status) {
            case "Submitted":
                // Show the submission details
                FirebaseHelper.getStudentSubmission(form.getFormId(), userId, submission -> {
                    if (submission != null) {
                        Intent intent = new Intent(this, ViewSubmissionActivity.class);
                        intent.putExtra("formId", form.getFormId());
                        intent.putExtra("submissionId", submission.getSubmissionId());
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Could not retrieve your submission", Toast.LENGTH_SHORT).show();
                    }
                });
                break;

            case "Active":
                // Allow the student to fill the form
                Intent intent = new Intent(this, FormSubmissionActivity.class);
                intent.putExtra("formId", form.getFormId());
                intent.putExtra("userId", userId);
                startActivity(intent);
                break;

            case "Inactive":
                Toast.makeText(this, "This form is currently inactive", Toast.LENGTH_SHORT).show();
                break;

            case "Access Denied":
                Toast.makeText(this, "You do not have access to this form", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showRequestAccessDialog(Form form, String requestType) {
        // First check if there's already a pending request
        FirebaseHelper.checkPendingAccessRequest(form.getFormId(), userId, hasPendingRequest -> {
            if (hasPendingRequest) {
                Toast.makeText(this, "You already have a pending request for this form", Toast.LENGTH_SHORT).show();
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            String title;
            String message;

            switch (requestType) {
                case "ACCESS":
                    title = "Request Access";
                    message = "Please explain why you need access to this form:";
                    break;
                case "RESUBMIT":
                    title = "Request Resubmission";
                    message = "Please explain why you need to resubmit this form:";
                    break;
                case "ACTIVATE":
                    title = "Request Activation";
                    message = "Please explain why this form should be activated:";
                    break;
                default:
                    title = "Request";
                    message = "Please provide details for your request:";
                    break;
            }

            builder.setTitle(title);

            View dialogView = getLayoutInflater().inflate(R.layout.dialog_request_access, null);
            builder.setView(dialogView);

            TextView messageTextView = dialogView.findViewById(R.id.message_text_view);
            EditText reasonEditText = dialogView.findViewById(R.id.reason_edit_text);

            messageTextView.setText(message);

            builder.setPositiveButton("Submit Request", null);
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();

            // Override the positive button to prevent automatic dismissal
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String reason = reasonEditText.getText().toString().trim();

                if (reason.isEmpty()) {
                    reasonEditText.setError("Please provide a reason");
                    return;
                }

                // Create and submit the access request
                AccessRequest request = new AccessRequest(
                        null, form.getFormId(), userId, rollNumber, userName, requestType
                );
                request.setMessage(reason);

                FirebaseHelper.createAccessRequest(request, success -> {
                    if (success) {
                        Toast.makeText(StudentDashboardActivity.this,
                                "Request submitted successfully",
                                Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(StudentDashboardActivity.this,
                                "Failed to submit request",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });
    }


    private void loadForms() {
        swipeRefreshLayout.setRefreshing(true);

        // First get all forms (active and inactive)
        FirebaseHelper.getAllForms(rollNumber, forms -> {
            // Then get all forms the student has submitted
            getSubmittedFormIds(forms, () -> {
                formList.clear();
                formList.addAll(forms);

                if (formAdapter != null) {
                    formAdapter.notifyDataSetChanged();
                }

                swipeRefreshLayout.setRefreshing(false);

                // Show empty view if no forms
                if (formList.isEmpty()) {
                    emptyTextView.setVisibility(View.VISIBLE);
                    formsRecyclerView.setVisibility(View.GONE);
                } else {
                    emptyTextView.setVisibility(View.GONE);
                    formsRecyclerView.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    private void getSubmittedFormIds(List<Form> forms, Runnable onComplete) {
        submittedFormIds.clear();

        if (forms.isEmpty()) {
            onComplete.run();
            return;
        }

        final int[] processedCount = {0};
        final int totalCount = forms.size();

        for (Form form : forms) {
            FirebaseHelper.hasStudentSubmittedForm(form.getFormId(), userId, hasSubmitted -> {
                if (hasSubmitted) {
                    submittedFormIds.add(form.getFormId());
                }

                processedCount[0]++;
                if (processedCount[0] >= totalCount) {
                    onComplete.run();
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateCurrentTime() {
        // Format current time as IST
        String currentTime = TimeUtils.getCurrentTimeIST();
        currentTimeTextView.setText(currentTime);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadForms();

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_logout) {
            showLogoutConfirmationDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    FirebaseHelper.logoutUser();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("No", null)
                .show();
    }
}