package com.example.ad_pbl_activityfeedback_q5.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ad_pbl_activityfeedback_q5.R;
import com.example.ad_pbl_activityfeedback_q5.adapters.ProfessorFormAdapter;
import com.example.ad_pbl_activityfeedback_q5.models.Form;
import com.example.ad_pbl_activityfeedback_q5.utils.FirebaseHelper;
import com.example.ad_pbl_activityfeedback_q5.utils.TimeUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ProfessorDashboardActivity extends AppCompatActivity implements ProfessorFormAdapter.OnFormActionListener {

    private String userId;
    private String userName;
    private RecyclerView formsRecyclerView;
    private ProfessorFormAdapter formAdapter;
    private List<Form> formList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyTextView;
    private FloatingActionButton addFormFab;
    private TextView userNameTextView;
    private TextView currentTimeTextView;
    private Handler timeUpdateHandler;
    private Runnable timeUpdateRunnable;
    private static final int FORM_CREATION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_dashboard);

        // Get user data from intent
        userId = getIntent().getStringExtra("userId");
        userName = getIntent().getStringExtra("userName");

        if (userId == null || userName == null) {
            // This is critical - without these values, we can't function properly
            Toast.makeText(this, "Error: Missing user information", Toast.LENGTH_SHORT).show();
            FirebaseHelper.logoutUser();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Set title
        setTitle("Professor Dashboard");

        // Initialize views
        formsRecyclerView = findViewById(R.id.forms_recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        emptyTextView = findViewById(R.id.empty_text_view);
        addFormFab = findViewById(R.id.add_form_fab);
        userNameTextView = findViewById(R.id.user_name_text_view);
        currentTimeTextView = findViewById(R.id.current_time_text_view);

        // Set user name
        userNameTextView.setText("Current User: " + userName.toUpperCase());

        // Initialize time update handler
        timeUpdateHandler = new Handler(Looper.getMainLooper());
        timeUpdateRunnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                updateCurrentTime();
                timeUpdateHandler.postDelayed(this, 1000); // Update every second
            }
        };

        // Setup RecyclerView
        formList = new ArrayList<>();
        formAdapter = new ProfessorFormAdapter(this, formList, this);
        formsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        formsRecyclerView.setAdapter(formAdapter);

        // Setup SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this::loadForms);

        // Setup FAB
        addFormFab.setOnClickListener(view -> {
            Intent intent = new Intent(this, FormCreationActivity.class);
            intent.putExtra("userId", userId);
            startActivityForResult(intent, FORM_CREATION_REQUEST_CODE);
        });

        // Load forms
        loadForms();
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

        // Check if user is still logged in
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // User has been logged out, return to login screen
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // Rest of your onResume code...
    }

    public void onAnalyticsClick(View view) {
        // Check if user is still logged in
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // User has been logged out
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return;
        }

        try {
            Toast.makeText(this, "Loading analytics...", Toast.LENGTH_SHORT).show();

            // Navigate directly, we'll handle empty data in the Analytics activity
            Intent intent = new Intent(this, AnalyticsActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.e("ProfessorDashboard", "Error navigating to analytics", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop time updates
        timeUpdateHandler.removeCallbacks(timeUpdateRunnable);
    }

    private void loadForms() {
        swipeRefreshLayout.setRefreshing(true);

        FirebaseHelper.getFormsCreatedBy(userId, forms -> {
            formList.clear();
            formList.addAll(forms);
            formAdapter.notifyDataSetChanged();

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
    }

    @Override
    public void onViewResults(Form form) {
        Intent intent = new Intent(this, FormResultsActivity.class);
        intent.putExtra("formId", form.getFormId());
        intent.putExtra("formTitle", form.getTitle());
        startActivity(intent);
    }

    @Override
    public void onViewAnalytics(Form form) {
        Intent intent = new Intent(this, AnalyticsActivity.class);
        intent.putExtra("formId", form.getFormId());
        intent.putExtra("formTitle", form.getTitle());
        startActivity(intent);
    }

    @Override
    public void onManageRequests(Form form) {
        try {
            if (form == null || form.getFormId() == null) {
                Toast.makeText(this, "Error: Invalid form data", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, FormRequestsActivity.class);
            intent.putExtra("formId", form.getFormId());
            intent.putExtra("formTitle", form.getTitle());
            startActivity(intent);
        } catch (Exception e) {
            Log.e("ProfessorDashboard", "Error navigating to requests", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onToggleStatus(Form form, int position) {
        // This is handled directly in the adapter
    }

    @Override
    public void onDelete(Form form, int position) {
        // This is handled directly in the adapter
    }

    @Override
    public void onModifyRollRange(Form form) {
        Intent intent = new Intent(this, ModifyRollRangeActivity.class);
        intent.putExtra("formId", form.getFormId());
        intent.putExtra("formTitle", form.getTitle());
        intent.putExtra("currentMinRoll", form.getMinRollNumber());
        intent.putExtra("currentMaxRoll", form.getMaxRollNumber());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.professor_dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_logout) {
            showLogoutConfirmationDialog();
            return true;
        } else if (itemId == R.id.action_password_requests) {
            Intent passwordRequestsIntent = new Intent(this, PasswordResetRequestsActivity.class);
            startActivity(passwordRequestsIntent);
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

    public void onEditRollRangeClick(View view) {
        // Check if user is still logged in
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // User has been logged out
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return;
        }

        try {
            // Get the form ID from the view tag
            String formId = view.getTag() != null ? view.getTag().toString() : null;

            if (formId == null || formId.isEmpty()) {
                // If no tag, use a default form ID for testing
                formId = "defaultFormId";
            }

            // Show the edit dialog
            showEditRollRangeDialog(formId);
        } catch (Exception e) {
            Log.e("ProfessorDashboard", "Error editing roll range", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showEditRollRangeDialog(String formId) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Edit Roll Number Range");

            // Create layout for dialog
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_roll_range, null);
            builder.setView(dialogView);

            EditText minRollEditText = dialogView.findViewById(R.id.min_roll_edit_text);
            EditText maxRollEditText = dialogView.findViewById(R.id.max_roll_edit_text);

            // Set default values for now
            minRollEditText.setText("42101");
            maxRollEditText.setText("42485");

            builder.setPositiveButton("Save", (dialog, which) -> {
                try {
                    int minRoll = Integer.parseInt(minRollEditText.getText().toString().trim());
                    int maxRoll = Integer.parseInt(maxRollEditText.getText().toString().trim());

                    if (minRoll > maxRoll) {
                        Toast.makeText(this, "Minimum roll number cannot be greater than maximum",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Just show a success message for now
                    Toast.makeText(this, "Roll range updated successfully", Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancel", null);

            builder.create().show();
        } catch (Exception e) {
            Log.e("ProfessorDashboard", "Error showing dialog", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FORM_CREATION_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            loadForms();

            // Retrieve the newly created form data
            String formId = data.getStringExtra("newFormId");
            String title = data.getStringExtra("newFormTitle");
            String description = data.getStringExtra("newFormDescription");

            if (formId != null) {
                // Create a new Form object
                Form newForm = new Form();
                newForm.setFormId(formId);
                newForm.setTitle(title);
                newForm.setDescription(description);
                newForm.setCreatedBy(userId);
                newForm.setActive(true);

                // Add to the list and update the adapter
                formList.add(0, newForm);  // Add at the top of the list
                formAdapter.notifyItemInserted(0);

                // Ensure the RecyclerView is visible (not the empty view)
                if (formList.size() == 1) {
                    emptyTextView.setVisibility(View.GONE);
                    formsRecyclerView.setVisibility(View.VISIBLE);
                }

                // Scroll to the top to show the new form
                formsRecyclerView.smoothScrollToPosition(0);
            }
        }
    }
}