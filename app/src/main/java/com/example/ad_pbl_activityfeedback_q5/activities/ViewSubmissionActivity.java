package com.example.ad_pbl_activityfeedback_q5.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ad_pbl_activityfeedback_q5.R;
import com.example.ad_pbl_activityfeedback_q5.adapters.ViewSubmissionAdapter;
import com.example.ad_pbl_activityfeedback_q5.models.Form;
import com.example.ad_pbl_activityfeedback_q5.models.Question;
import com.example.ad_pbl_activityfeedback_q5.models.Submission;
import com.example.ad_pbl_activityfeedback_q5.utils.DateTimeConverter;
import com.example.ad_pbl_activityfeedback_q5.utils.FirebaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewSubmissionActivity extends AppCompatActivity {

    private String formId;
    private String submissionId;
    private TextView formTitleTextView;
    private TextView submittedDateTextView;
    private RecyclerView answersRecyclerView;
    private ProgressBar progressBar;
    private ViewSubmissionAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_submission);

        // Get data from intent
        formId = getIntent().getStringExtra("formId");
        submissionId = getIntent().getStringExtra("submissionId");

        if (formId == null || submissionId == null) {
            Toast.makeText(this, "Error: Missing form or submission ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Your Submission");
        }

        // Initialize views
        formTitleTextView = findViewById(R.id.form_title_text_view);
        submittedDateTextView = findViewById(R.id.submitted_date_text_view);
        answersRecyclerView = findViewById(R.id.answers_recycler_view);
        progressBar = findViewById(R.id.progress_bar);

        // Setup RecyclerView
        answersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load the form and submission
        loadData();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadData() {
        progressBar.setVisibility(View.VISIBLE);

        // First load the form to get questions
        FirebaseHelper.getForm(formId, form -> {
            if (form != null) {
                formTitleTextView.setText(form.getTitle());

                // Now load the submission
                loadSubmission(form);
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Error: Could not load form", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadSubmission(Form form) {
        FirebaseHelper.getFormSubmissions(formId, submissions -> {
            Submission userSubmission = null;
            for (Submission submission : submissions) {
                if (submission.getSubmissionId().equals(submissionId)) {
                    userSubmission = submission;
                    break;
                }
            }

            if (userSubmission != null) {
                // Display submission date
                submittedDateTextView.setText("Submitted: " +
                        DateTimeConverter.formatForDisplay(userSubmission.getSubmittedAt()));

                // Prepare data for adapter
                List<Map<String, Object>> answersList = prepareAnswersList(form, userSubmission);

                // Set up adapter
                adapter = new ViewSubmissionAdapter(this, answersList);
                answersRecyclerView.setAdapter(adapter);
            } else {
                Toast.makeText(this, "Error: Could not find your submission", Toast.LENGTH_SHORT).show();
            }

            progressBar.setVisibility(View.GONE);
        });
    }

    private List<Map<String, Object>> prepareAnswersList(Form form, Submission submission) {
        List<Map<String, Object>> answersList = new ArrayList<>();

        // Process each question and answer
        for (Map.Entry<String, Question> entry : form.getQuestions().entrySet()) {
            String questionId = entry.getKey();
            Question question = entry.getValue();
            Object answer = submission.getAnswers().get(questionId);

            Map<String, Object> item = new HashMap<>();
            item.put("question", question);
            item.put("answer", answer);

            answersList.add(item);
        }

        return answersList;
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