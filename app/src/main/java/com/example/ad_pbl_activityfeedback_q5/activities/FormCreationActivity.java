package com.example.ad_pbl_activityfeedback_q5.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.example.ad_pbl_activityfeedback_q5.R;
import com.example.ad_pbl_activityfeedback_q5.adapters.QuestionAdapter;
import com.example.ad_pbl_activityfeedback_q5.models.Form;
import com.example.ad_pbl_activityfeedback_q5.models.Question;
import com.example.ad_pbl_activityfeedback_q5.utils.FirebaseHelper;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormCreationActivity extends AppCompatActivity {

    private String userId;
    private EditText titleEditText, descriptionEditText;
    private EditText minRollEditText, maxRollEditText;
    private CheckBox limitRollsCheckBox;
    private LinearLayout rollRangeLayout;
    private Button addQuestionButton, saveFormButton;
    private RecyclerView questionsRecyclerView;
    private QuestionAdapter questionAdapter;
    private List<Question> questionList;
    private ProgressBar progressBar;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_creation);

        // Get user ID from intent
        userId = getIntent().getStringExtra("userId");

        // Set up ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Create Feedback Form");
        }

        // Initialize views
        titleEditText = findViewById(R.id.title_edit_text);
        descriptionEditText = findViewById(R.id.description_edit_text);
        minRollEditText = findViewById(R.id.min_roll_edit_text);
        maxRollEditText = findViewById(R.id.max_roll_edit_text);
        limitRollsCheckBox = findViewById(R.id.limit_rolls_checkbox);
        rollRangeLayout = findViewById(R.id.roll_range_layout);
        addQuestionButton = findViewById(R.id.add_question_button);
        saveFormButton = findViewById(R.id.save_form_button);
        questionsRecyclerView = findViewById(R.id.questions_recycler_view);
        progressBar = findViewById(R.id.progress_bar);

        // Setup roll number range visibility
        limitRollsCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            rollRangeLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        // Setup RecyclerView for questions
        questionList = new ArrayList<>();
        questionAdapter = new QuestionAdapter(this, questionList);
        questionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        questionsRecyclerView.setAdapter(questionAdapter);

        // Setup add question button
        addQuestionButton.setOnClickListener(v -> showAddQuestionDialog());

        // Setup save form button
        saveFormButton.setOnClickListener(v -> saveForm());
    }

    private void showAddQuestionDialog() {
        // Clear focus from any EditText and hide keyboard
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.clearFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }

        // Create dialog for adding a new question
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.item_question_input, null);
        builder.setView(dialogView);

        EditText questionTextEditText = dialogView.findViewById(R.id.question_text_edit_text);
        RadioGroup questionTypeRadioGroup = dialogView.findViewById(R.id.question_type_radio_group);
        TextInputLayout optionsLayout = dialogView.findViewById(R.id.options_layout);
        EditText optionsEditText = dialogView.findViewById(R.id.options_edit_text);
        TextInputLayout ratingMinLayout = dialogView.findViewById(R.id.rating_min_layout);
        EditText ratingMinEditText = dialogView.findViewById(R.id.rating_min_edit_text);
        TextInputLayout ratingMaxLayout = dialogView.findViewById(R.id.rating_max_layout);
        EditText ratingMaxEditText = dialogView.findViewById(R.id.rating_max_edit_text);

        // Show/hide fields based on question type
        questionTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRadioButton = dialogView.findViewById(checkedId);
            String questionType = selectedRadioButton.getText().toString().toLowerCase();

            switch (questionType) {
                case "text":
                    optionsLayout.setVisibility(View.GONE);
                    ratingMinLayout.setVisibility(View.GONE);
                    ratingMaxLayout.setVisibility(View.GONE);
                    break;
                case "multiple choice":
                    optionsLayout.setVisibility(View.VISIBLE);
                    ratingMinLayout.setVisibility(View.GONE);
                    ratingMaxLayout.setVisibility(View.GONE);
                    break;
                case "rating":
                    optionsLayout.setVisibility(View.GONE);
                    ratingMinLayout.setVisibility(View.VISIBLE);
                    ratingMaxLayout.setVisibility(View.VISIBLE);
                    break;
            }
        });

        builder.setTitle("Add Question");
        builder.setPositiveButton("Add", null); // We'll override this below
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        // Override the positive button to prevent automatic dismissal and validate
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String questionText = questionTextEditText.getText().toString().trim();
            if (questionText.isEmpty()) {
                questionTextEditText.setError("Question text is required");
                return;
            }

            int selectedTypeId = questionTypeRadioGroup.getCheckedRadioButtonId();
            if (selectedTypeId == -1) {
                Toast.makeText(this, "Please select a question type", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedType = dialogView.findViewById(selectedTypeId);
            String questionType = selectedType.getText().toString().toLowerCase();

            // Create question based on type
            Question question = new Question();
            String questionId = String.valueOf(System.currentTimeMillis());
            question.setQuestionId(questionId);
            question.setText(questionText);

            switch (questionType) {
                case "text":
                    question.setType("text");
                    break;

                case "multiple choice":
                    String optionsText = optionsEditText.getText().toString().trim();
                    if (optionsText.isEmpty()) {
                        optionsEditText.setError("Options are required");
                        return;
                    }

                    // Split options by comma or new line
                    String[] options = optionsText.split("[,\n]");
                    if (options.length < 2) {
                        optionsEditText.setError("At least 2 options are required");
                        return;
                    }

                    List<String> optionsList = new ArrayList<>();
                    for (String option : options) {
                        String trimmedOption = option.trim();
                        if (!trimmedOption.isEmpty()) {
                            optionsList.add(trimmedOption);
                        }
                    }

                    question.setType("multiple_choice");
                    question.setOptions(optionsList);
                    break;

                case "rating":
                    String minText = ratingMinEditText.getText().toString().trim();
                    String maxText = ratingMaxEditText.getText().toString().trim();

                    if (minText.isEmpty() || maxText.isEmpty()) {
                        if (minText.isEmpty()) ratingMinEditText.setError("Required");
                        if (maxText.isEmpty()) ratingMaxEditText.setError("Required");
                        return;
                    }

                    try {
                        int min = Integer.parseInt(minText);
                        int max = Integer.parseInt(maxText);

                        if (min >= max) {
                            ratingMinEditText.setError("Min must be less than Max");
                            return;
                        }

                        question.setType("rating");
                        question.setMin(min);
                        question.setMax(max);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Invalid min or max value", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    break;
            }

            // Add question to list and update adapter
            questionList.add(question);
            questionAdapter.notifyItemInserted(questionList.size() - 1);

            // Scroll to the newly added question
            questionsRecyclerView.smoothScrollToPosition(questionList.size() - 1);

            dialog.dismiss();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveForm() {
        // Validate inputs
        String title = titleEditText.getText().toString().trim();
        if (title.isEmpty()) {
            titleEditText.setError("Title is required");
            titleEditText.requestFocus();
            return;
        }

        String description = descriptionEditText.getText().toString().trim();
        if (description.isEmpty()) {
            descriptionEditText.setError("Description is required");
            descriptionEditText.requestFocus();
            return;
        }

        if (questionList.isEmpty()) {
            Toast.makeText(this, "Please add at least one question", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate roll number range if enabled
        int minRoll = 0;
        int maxRoll = 0;

        if (limitRollsCheckBox.isChecked()) {
            String minText = minRollEditText.getText().toString().trim();
            String maxText = maxRollEditText.getText().toString().trim();

            if (minText.isEmpty() || maxText.isEmpty()) {
                if (minText.isEmpty()) minRollEditText.setError("Required");
                if (maxText.isEmpty()) maxRollEditText.setError("Required");
                return;
            }

            try {
                minRoll = Integer.parseInt(minText);
                maxRoll = Integer.parseInt(maxText);

                if (minRoll > maxRoll) {
                    minRollEditText.setError("Min must be less than Max");
                    return;
                }

                // Validate range is within allowed roll numbers
                if (minRoll < 42101 || maxRoll > 42485) {
                    Toast.makeText(this, "Roll numbers must be between 42101 and 42485", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid roll number range", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Show progress
        progressBar.setVisibility(View.VISIBLE);
        saveFormButton.setEnabled(false);

        // Create form
        Form form = new Form(null, title, description, userId);

        // Add roll number range if enabled
        if (limitRollsCheckBox.isChecked()) {
            form.setMinRollNumber(minRoll);
            form.setMaxRollNumber(maxRoll);
        }

        // Add questions to form
        Map<String, Question> questionsMap = new HashMap<>();
        for (Question question : questionList) {
            questionsMap.put(question.getQuestionId(), question);
        }
        form.setQuestions(questionsMap);

        // Save to Firebase
        FirebaseHelper.createForm(form, formId -> {
            if (formId != null) {
                Toast.makeText(this, "Form created successfully!", Toast.LENGTH_SHORT).show();

                // Send the form back to ProfessorDashboardActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("newFormId", formId);
                resultIntent.putExtra("newFormTitle", form.getTitle());
                resultIntent.putExtra("newFormDescription", form.getDescription());
                setResult(RESULT_OK, resultIntent);

                finish();
            } else {
                Toast.makeText(this, "Failed to create form", Toast.LENGTH_SHORT).show();
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