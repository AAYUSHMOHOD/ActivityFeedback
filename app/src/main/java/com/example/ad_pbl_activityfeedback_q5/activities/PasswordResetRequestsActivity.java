package com.example.ad_pbl_activityfeedback_q5.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ad_pbl_activityfeedback_q5.R;
import com.example.ad_pbl_activityfeedback_q5.adapters.PasswordResetRequestAdapter;
import com.example.ad_pbl_activityfeedback_q5.models.PasswordResetRequest;
import com.example.ad_pbl_activityfeedback_q5.utils.FirebaseHelper;

import java.util.ArrayList;
import java.util.List;

public class PasswordResetRequestsActivity extends AppCompatActivity {

    private static final String TAG = "PasswordResetRequests";
    private RecyclerView requestsRecyclerView;
    private TextView emptyTextView;
    private ProgressBar progressBar;
    private List<PasswordResetRequest> requestsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_password_reset_requests);

            // Debug log
            Log.d("PasswordResetReq", "Activity created, setting content view");

            // Set up ActionBar
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Password Reset Requests");
            }

            // Initialize views
            try {
                requestsRecyclerView = findViewById(R.id.requests_recycler_view);
                if (requestsRecyclerView == null) {
                    Log.e("PasswordResetReq", "Failed to find recycler view");
                }

                emptyTextView = findViewById(R.id.empty_text_view);
                if (emptyTextView == null) {
                    Log.e("PasswordResetReq", "Failed to find empty text view");
                }

                progressBar = findViewById(R.id.progress_bar);
                if (progressBar == null) {
                    Log.e("PasswordResetReq", "Failed to find progress bar");
                }
            } catch (Exception e) {
                Log.e("PasswordResetReq", "Error finding views", e);
                Toast.makeText(this, "Error initializing views", Toast.LENGTH_SHORT).show();
                return;
            }

            // Setup RecyclerView
            requestsList = new ArrayList<>();
            requestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            // Add empty adapter
            PasswordResetRequestAdapter adapter = new PasswordResetRequestAdapter(
                    this,
                    requestsList,
                    new PasswordResetRequestAdapter.OnRequestActionListener() {
                        @Override
                        public void onApprove(PasswordResetRequest request, int position) {
                            handleApproveRequest(request, position);
                        }

                        @Override
                        public void onReject(PasswordResetRequest request, int position) {
                            handleRejectRequest(request, position);
                        }
                    });
            requestsRecyclerView.setAdapter(adapter);

            // Load password reset requests
            loadRequests();
        } catch (Exception e) {
            Log.e("PasswordResetReq", "Fatal error in onCreate", e);
            Toast.makeText(this, "Error initializing activity: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish(); // Close the activity if there's a critical error
        }
    }

    private void loadRequests() {
        try {
            progressBar.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);

            Log.d("PasswordResetReq", "Loading password reset requests");

            FirebaseHelper.getPendingPasswordResetRequests(requests -> {
                try {
                    progressBar.setVisibility(View.GONE);

                    Log.d("PasswordResetReq", "Loaded " + (requests != null ? requests.size() : 0) + " requests");

                    requestsList.clear();
                    if (requests != null) {
                        requestsList.addAll(requests);
                    }

                    if (requestsRecyclerView.getAdapter() != null) {
                        requestsRecyclerView.getAdapter().notifyDataSetChanged();
                    } else {
                        Log.e("PasswordResetReq", "Adapter is null when trying to update");
                    }

                    // Show empty view if no requests
                    if (requestsList.isEmpty()) {
                        emptyTextView.setVisibility(View.VISIBLE);
                        requestsRecyclerView.setVisibility(View.GONE);
                    } else {
                        emptyTextView.setVisibility(View.GONE);
                        requestsRecyclerView.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    Log.e("PasswordResetReq", "Error updating UI after loading requests", e);
                    Toast.makeText(PasswordResetRequestsActivity.this,
                            "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e("PasswordResetReq", "Error loading requests", e);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

            // Show empty state
            emptyTextView.setVisibility(View.VISIBLE);
            requestsRecyclerView.setVisibility(View.GONE);
        }
    }

    private void handleApproveRequest(PasswordResetRequest request, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Password Reset Information");
        builder.setMessage(
                "You're about to reset password of student with mail " + request.getStudentEmail() + "\n\n" +
                        "Please inform the student that their new password should be: Pict_" + request.getStudentRollNumber()
        );

        builder.setPositiveButton("Reset Password", (dialog, which) -> {
            progressBar.setVisibility(View.VISIBLE);

            FirebaseHelper.resetPasswordForStudent(request.getStudentEmail(), request.getStudentRollNumber(), success -> {
                progressBar.setVisibility(View.GONE);

                if (success) {
                    FirebaseHelper.markPasswordResetRequestAsApproved(request.getRequestId(), taskSucceeded -> {
                        if (taskSucceeded) {
                            Toast.makeText(this,
                                    "Password reset done for " + request.getStudentEmail(),
                                    Toast.LENGTH_LONG).show();

                            // Remove this request from the list
                            requestsList.remove(position);
                            if (requestsRecyclerView.getAdapter() != null) {
                                requestsRecyclerView.getAdapter().notifyItemRemoved(position);
                            }

                            // Show empty state if needed
                            if (requestsList.isEmpty()) {
                                emptyTextView.setVisibility(View.VISIBLE);
                                requestsRecyclerView.setVisibility(View.GONE);
                            }
                        } else {
                            Toast.makeText(this, "Failed to update request status", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(this, "Failed to reset password", Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private void handleRejectRequest(PasswordResetRequest request, int position) {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseHelper.markPasswordResetRequestAsRejected(request.getRequestId(), success -> {
            progressBar.setVisibility(View.GONE);

            if (success) {
                Toast.makeText(this, "Request rejected", Toast.LENGTH_SHORT).show();
                requestsList.remove(position);

                // Update the adapter
                if (requestsRecyclerView.getAdapter() != null) {
                    requestsRecyclerView.getAdapter().notifyItemRemoved(position);
                }

                // Update empty state
                if (requestsList.isEmpty()) {
                    emptyTextView.setVisibility(View.VISIBLE);
                    requestsRecyclerView.setVisibility(View.GONE);
                }
            } else {
                Toast.makeText(this, "Failed to reject request", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}