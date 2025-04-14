package com.example.ad_pbl_activityfeedback_q5.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ad_pbl_activityfeedback_q5.R;
import com.example.ad_pbl_activityfeedback_q5.adapters.AnalyticsAdapter;
import com.example.ad_pbl_activityfeedback_q5.utils.FirebaseHelper;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnalyticsActivity extends AppCompatActivity {

    private static final String TAG = "AnalyticsActivity";
    private ProgressBar progressBar;
    private TextView emptyTextView;
    private RecyclerView analyticsRecyclerView;
    private String formId;
    private String formTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_analytics);

            // Get the form ID and title from the intent (if available)
            formId = getIntent().getStringExtra("formId");
            formTitle = getIntent().getStringExtra("formTitle");

            String title = "Analytics";
            if (formTitle != null && !formTitle.isEmpty()) {
                title += ": " + formTitle;
            }

            // Set up ActionBar
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle(title);
            }

            // Initialize views
            progressBar = findViewById(R.id.progress_bar);
            emptyTextView = findViewById(R.id.empty_text_view);
            analyticsRecyclerView = findViewById(R.id.analytics_recycler_view);
            analyticsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            // Check if user is still logged in
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Load analytics data
            loadAnalyticsData();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Error initializing analytics: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadAnalyticsData() {
        try {
            progressBar.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
            analyticsRecyclerView.setVisibility(View.GONE);

            if (formId == null || formId.isEmpty()) {
                // No specific form chosen - this is a general analytics page
                progressBar.setVisibility(View.GONE);
                emptyTextView.setVisibility(View.VISIBLE);
                emptyTextView.setText("Please select a specific form to view analytics");
                return;
            }

            // Get analytics data for the form
            FirebaseHelper.getFormAnalytics(formId, analyticsData -> {
                progressBar.setVisibility(View.GONE);

                // Check if there is data
                Integer totalSubmissions = (Integer) analyticsData.get("totalSubmissions");
                if (totalSubmissions == null || totalSubmissions == 0) {
                    emptyTextView.setVisibility(View.VISIBLE);
                    analyticsRecyclerView.setVisibility(View.GONE);
                    emptyTextView.setText("No submissions found for this form");
                    return;
                }

                // Extract question analytics
                Map<String, Map<String, Object>> questionsData =
                        (Map<String, Map<String, Object>>) analyticsData.get("questions");

                if (questionsData == null || questionsData.isEmpty()) {
                    emptyTextView.setVisibility(View.VISIBLE);
                    analyticsRecyclerView.setVisibility(View.GONE);
                    emptyTextView.setText("No question data available");
                    return;
                }

                // Prepare data for adapter
                List<Map<String, Object>> questionsList = new ArrayList<>(questionsData.values());

                // Display the data in RecyclerView
                AnalyticsAdapter analyticsAdapter = new AnalyticsAdapter(this, questionsList);
                analyticsRecyclerView.setAdapter(analyticsAdapter);

                analyticsRecyclerView.setVisibility(View.VISIBLE);
                emptyTextView.setVisibility(View.GONE);
            });
        } catch (Exception e) {
            Log.e(TAG, "Error loading analytics data", e);
            progressBar.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText("Error loading data: " + e.getMessage());
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