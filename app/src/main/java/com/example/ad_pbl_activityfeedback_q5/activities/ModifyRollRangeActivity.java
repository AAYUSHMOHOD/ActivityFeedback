package com.example.ad_pbl_activityfeedback_q5.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ad_pbl_activityfeedback_q5.R;
import com.example.ad_pbl_activityfeedback_q5.utils.FirebaseHelper;
import com.google.firebase.auth.FirebaseAuth;

public class ModifyRollRangeActivity extends AppCompatActivity {

    private String formId;
    private String formTitle;
    private EditText minRollEditText;
    private EditText maxRollEditText;
    private Button saveButton;
    private ProgressBar progressBar;
    private int currentMinRoll;
    private int currentMaxRoll;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_roll_range);

        // Check if user is still logged in
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get data from intent
        formId = getIntent().getStringExtra("formId");
        formTitle = getIntent().getStringExtra("formTitle");
        currentMinRoll = getIntent().getIntExtra("currentMinRoll", 0);
        currentMaxRoll = getIntent().getIntExtra("currentMaxRoll", 0);

        if (formId == null) {
            Toast.makeText(this, "Error: Form ID not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Edit Roll Range: " + formTitle);
        }

        // Initialize views
        minRollEditText = findViewById(R.id.min_roll_edit_text);
        maxRollEditText = findViewById(R.id.max_roll_edit_text);
        saveButton = findViewById(R.id.save_button);
        progressBar = findViewById(R.id.progress_bar);

        // Set current values
        if (currentMinRoll > 0) {
            minRollEditText.setText(String.valueOf(currentMinRoll));
        }
        if (currentMaxRoll > 0) {
            maxRollEditText.setText(String.valueOf(currentMaxRoll));
        }

        // Setup save button
        saveButton.setOnClickListener(v -> saveRollRange());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveRollRange() {
        // Validate inputs
        String minText = minRollEditText.getText().toString().trim();
        String maxText = maxRollEditText.getText().toString().trim();

        if (minText.isEmpty() || maxText.isEmpty()) {
            if (minText.isEmpty()) minRollEditText.setError("Required");
            if (maxText.isEmpty()) maxRollEditText.setError("Required");
            return;
        }

        try {
            int minRoll = Integer.parseInt(minText);
            int maxRoll = Integer.parseInt(maxText);

            if (minRoll > maxRoll) {
                minRollEditText.setError("Min must be less than Max");
                return;
            }

            // Show progress
            progressBar.setVisibility(View.VISIBLE);
            saveButton.setEnabled(false);

            // Save to Firebase
            FirebaseHelper.updateFormRollNumberRange(formId, minRoll, maxRoll, success -> {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    saveButton.setEnabled(true);

                    if (success) {
                        Toast.makeText(this, "Roll number range updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to update roll number range", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid roll number format", Toast.LENGTH_SHORT).show();
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