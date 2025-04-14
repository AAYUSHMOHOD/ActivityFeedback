package com.example.ad_pbl_activityfeedback_q5.activities;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ad_pbl_activityfeedback_q5.R;
import com.example.ad_pbl_activityfeedback_q5.adapters.AccessRequestAdapter;
import com.example.ad_pbl_activityfeedback_q5.models.AccessRequest;
import com.example.ad_pbl_activityfeedback_q5.utils.FirebaseHelper;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class FormRequestsActivity extends AppCompatActivity implements AccessRequestAdapter.OnRequestActionListener {

    private String formId;
    private String formTitle;
    private RecyclerView requestsRecyclerView;
    private AccessRequestAdapter requestAdapter;
    private List<AccessRequest> requestList;
    private TextView emptyTextView;
    private Button approveAllButton;
    private ProgressBar progressBar;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_requests);

        // Check if user is still logged in
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get data from intent
        formId = getIntent().getStringExtra("formId");
        formTitle = getIntent().getStringExtra("formTitle");

        if (formId == null) {
            Toast.makeText(this, "Error: Form ID not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Access Requests: " + formTitle);
        }

        try {
            // Initialize views
            requestsRecyclerView = findViewById(R.id.requests_recycler_view);
            emptyTextView = findViewById(R.id.empty_text_view);
            approveAllButton = findViewById(R.id.approve_all_button);
            progressBar = findViewById(R.id.progress_bar);

            // Setup RecyclerView
            requestList = new ArrayList<>();
            requestAdapter = new AccessRequestAdapter(this, requestList, this);
            requestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            requestsRecyclerView.setAdapter(requestAdapter);

            // Setup approve all button
            approveAllButton.setOnClickListener(v -> approveAllRequests());

            // Load requests
            loadRequests();
        } catch (Exception e) {
            Log.e("FormRequests", "Error in onCreate", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadRequests() {
        try {
            progressBar.setVisibility(View.VISIBLE);

            FirebaseHelper.getFormPendingAccessRequests(formId, requests -> {
                runOnUiThread(() -> {
                    try {
                        requestList.clear();
                        requestList.addAll(requests);
                        requestAdapter.notifyDataSetChanged();

                        progressBar.setVisibility(View.GONE);

                        // Update UI based on whether there are requests
                        if (requestList.isEmpty()) {
                            emptyTextView.setVisibility(View.VISIBLE);
                            requestsRecyclerView.setVisibility(View.GONE);
                            approveAllButton.setVisibility(View.GONE);
                        } else {
                            emptyTextView.setVisibility(View.GONE);
                            requestsRecyclerView.setVisibility(View.VISIBLE);
                            approveAllButton.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        Log.e("FormRequests", "Error updating UI", e);
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });
        } catch (Exception e) {
            Log.e("FormRequests", "Error loading requests", e);
            Toast.makeText(this, "Error loading requests: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onApprove(AccessRequest request, int position) {
        try {
            progressBar.setVisibility(View.VISIBLE);

            FirebaseHelper.approveAccessRequest(request.getRequestId(), success -> {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    if (success) {
                        Toast.makeText(this, "Request approved", Toast.LENGTH_SHORT).show();
                        requestList.remove(position);
                        requestAdapter.notifyItemRemoved(position);

                        // Update empty state
                        if (requestList.isEmpty()) {
                            emptyTextView.setVisibility(View.VISIBLE);
                            requestsRecyclerView.setVisibility(View.GONE);
                            approveAllButton.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(this, "Failed to approve request", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        } catch (Exception e) {
            Log.e("FormRequests", "Error approving request", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReject(AccessRequest request, int position) {
        try {
            progressBar.setVisibility(View.VISIBLE);

            FirebaseHelper.rejectAccessRequest(request.getRequestId(), success -> {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    if (success) {
                        Toast.makeText(this, "Request rejected", Toast.LENGTH_SHORT).show();
                        requestList.remove(position);
                        requestAdapter.notifyItemRemoved(position);

                        // Update empty state
                        if (requestList.isEmpty()) {
                            emptyTextView.setVisibility(View.VISIBLE);
                            requestsRecyclerView.setVisibility(View.GONE);
                            approveAllButton.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(this, "Failed to reject request", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        } catch (Exception e) {
            Log.e("FormRequests", "Error rejecting request", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void approveAllRequests() {
        if (requestList.isEmpty()) {
            return;
        }

        try {
            progressBar.setVisibility(View.VISIBLE);
            approveAllButton.setEnabled(false);

            FirebaseHelper.approveAllRequests(formId, success -> {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    approveAllButton.setEnabled(true);

                    if (success) {
                        Toast.makeText(this, "All requests approved", Toast.LENGTH_SHORT).show();
                        requestList.clear();
                        requestAdapter.notifyDataSetChanged();

                        // Update empty state
                        emptyTextView.setVisibility(View.VISIBLE);
                        requestsRecyclerView.setVisibility(View.GONE);
                        approveAllButton.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(this, "Failed to approve all requests", Toast.LENGTH_SHORT).show();
                        // Refresh the list to get current state
                        loadRequests();
                    }
                });
            });
        } catch (Exception e) {
            Log.e("FormRequests", "Error approving all requests", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            approveAllButton.setEnabled(true);
        }
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