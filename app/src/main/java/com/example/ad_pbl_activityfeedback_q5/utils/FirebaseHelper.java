package com.example.ad_pbl_activityfeedback_q5.utils;

import android.app.Activity;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.ad_pbl_activityfeedback_q5.models.AccessRequest;
import com.example.ad_pbl_activityfeedback_q5.models.Form;
import com.example.ad_pbl_activityfeedback_q5.models.Question;
import com.example.ad_pbl_activityfeedback_q5.models.Submission;
import com.example.ad_pbl_activityfeedback_q5.models.User;
import com.example.ad_pbl_activityfeedback_q5.models.PasswordResetRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class FirebaseHelper {
    private static final String TAG = "FirebaseHelper";
    private static final String DATABASE_URL = "YOUR_DATABASE_URL";

    // Student roll number range
    private static final int MIN_ROLL_NUMBER = 42101;
    private static final int MAX_ROLL_NUMBER = 42485;

    private static FirebaseDatabase database;
    private static FirebaseAuth auth;

    // Initialize Firebase components
    public static void initialize() {
        if (database == null) {
            database = FirebaseDatabase.getInstance(DATABASE_URL);
//            database.setPersistenceEnabled(true);
        }

        if (auth == null) {
            auth = FirebaseAuth.getInstance();
        }
    }

    // Authentication methods
    public static void registerUser(String email, String password, String name, String role,
                                    String rollOrId, OnCompleteListener<AuthResult> listener) {
        // Validate roll number for students
        if ("student".equals(role) && !isValidRollNumber(rollOrId)) {
            Task<AuthResult> failedTask = new Task<AuthResult>() {
                @Override
                public boolean isComplete() {
                    return true;
                }

                @Override
                public boolean isSuccessful() {
                    return false;
                }

                @Override
                public AuthResult getResult() {
                    return null;
                }

                @Override
                public <X extends Throwable> AuthResult getResult(@NonNull Class<X> exceptionType) throws X {
                    throw (X) new IllegalArgumentException("Invalid roll number. Must be between " +
                            MIN_ROLL_NUMBER + " and " + MAX_ROLL_NUMBER);
                }

                @Override
                public boolean isCanceled() {
                    return false;
                }

                @Override
                public Exception getException() {
                    return new IllegalArgumentException("Invalid roll number. Must be between " +
                            MIN_ROLL_NUMBER + " and " + MAX_ROLL_NUMBER);
                }

                @Override
                public Task<AuthResult> addOnSuccessListener(@NonNull OnSuccessListener<? super AuthResult> onSuccessListener) {
                    return this;
                }

                @NonNull
                @Override
                public Task<AuthResult> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super AuthResult> onSuccessListener) {
                    return this;
                }

                @NonNull
                @Override
                public Task<AuthResult> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super AuthResult> onSuccessListener) {
                    return this;
                }

                @NonNull
                @Override
                public Task<AuthResult> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
                    onFailureListener.onFailure(getException());
                    return this;
                }

                @NonNull
                @Override
                public Task<AuthResult> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
                    onFailureListener.onFailure(getException());
                    return this;
                }

                @NonNull
                @Override
                public Task<AuthResult> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
                    onFailureListener.onFailure(getException());
                    return this;
                }

                @NonNull
                @Override
                public Task<AuthResult> addOnCompleteListener(@NonNull OnCompleteListener<AuthResult> onCompleteListener) {
                    onCompleteListener.onComplete(this);
                    return this;
                }

                @NonNull
                @Override
                public Task<AuthResult> addOnCompleteListener(@NonNull Executor executor, @NonNull OnCompleteListener<AuthResult> onCompleteListener) {
                    onCompleteListener.onComplete(this);
                    return this;
                }

                @NonNull
                @Override
                public Task<AuthResult> addOnCompleteListener(@NonNull Activity activity, @NonNull OnCompleteListener<AuthResult> onCompleteListener) {
                    onCompleteListener.onComplete(this);
                    return this;
                }
            };

            listener.onComplete(failedTask);
            return;
        }

        // Check if roll number or professor ID is already in use
        checkIfIdentifierExists(role, rollOrId, exists -> {
            if (exists) {
                // Create a failed task with appropriate message
                String identifier = "student".equals(role) ? "Roll Number" : "Professor ID";

                Task<AuthResult> failedTask = new Task<AuthResult>() {
                    @Override
                    public boolean isComplete() {
                        return true;
                    }

                    @Override
                    public boolean isSuccessful() {
                        return false;
                    }

                    @Override
                    public AuthResult getResult() {
                        return null;
                    }

                    @Override
                    public <X extends Throwable> AuthResult getResult(@NonNull Class<X> exceptionType) throws X {
                        throw (X) new IllegalArgumentException(identifier + " already in use.");
                    }

                    @Override
                    public boolean isCanceled() {
                        return false;
                    }

                    @Override
                    public Exception getException() {
                        return new IllegalArgumentException(identifier + " already in use.");
                    }

                    @Override
                    public Task<AuthResult> addOnSuccessListener(@NonNull OnSuccessListener<? super AuthResult> onSuccessListener) {
                        return this;
                    }

                    @NonNull
                    @Override
                    public Task<AuthResult> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super AuthResult> onSuccessListener) {
                        return this;
                    }

                    @NonNull
                    @Override
                    public Task<AuthResult> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super AuthResult> onSuccessListener) {
                        return this;
                    }

                    @NonNull
                    @Override
                    public Task<AuthResult> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
                        onFailureListener.onFailure(getException());
                        return this;
                    }

                    @NonNull
                    @Override
                    public Task<AuthResult> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
                        onFailureListener.onFailure(getException());
                        return this;
                    }

                    @NonNull
                    @Override
                    public Task<AuthResult> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
                        onFailureListener.onFailure(getException());
                        return this;
                    }

                    @NonNull
                    @Override
                    public Task<AuthResult> addOnCompleteListener(@NonNull OnCompleteListener<AuthResult> onCompleteListener) {
                        onCompleteListener.onComplete(this);
                        return this;
                    }

                    @NonNull
                    @Override
                    public Task<AuthResult> addOnCompleteListener(@NonNull Executor executor, @NonNull OnCompleteListener<AuthResult> onCompleteListener) {
                        onCompleteListener.onComplete(this);
                        return this;
                    }

                    @NonNull
                    @Override
                    public Task<AuthResult> addOnCompleteListener(@NonNull Activity activity, @NonNull OnCompleteListener<AuthResult> onCompleteListener) {
                        onCompleteListener.onComplete(this);
                        return this;
                    }
                };

                listener.onComplete(failedTask);
            } else {
                // Proceed with registration
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null) {
                                String userId = task.getResult().getUser().getUid();
                                User newUser;

                                if ("student".equals(role)) {
                                    newUser = new User(userId, email, name, role);
                                    newUser.setRollNumber(rollOrId);
                                } else {
                                    newUser = new User(userId, email, name, role);
                                    newUser.setProfessorId(rollOrId);
                                }

                                database.getReference("users").child(userId).setValue(newUser);

                                // Subscribe to topic for notifications
                                if ("student".equals(role)) {
                                    FirebaseMessaging.getInstance().subscribeToTopic("student_" + rollOrId);
                                }
                            }
                            listener.onComplete(task);
                        });
            }
        });
    }

    private static boolean isValidRollNumber(String rollNumber) {
        try {
            int roll = Integer.parseInt(rollNumber);
            return roll >= MIN_ROLL_NUMBER && roll <= MAX_ROLL_NUMBER;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static void checkIfIdentifierExists(String role, String identifier, Consumer<Boolean> callback) {
        String field = "student".equals(role) ? "rollNumber" : "professorId";

        database.getReference("users")
                .orderByChild(field)
                .equalTo(identifier)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        callback.accept(snapshot.exists() && snapshot.getChildrenCount() > 0);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error checking identifier", error.toException());
                        callback.accept(false);
                    }
                });
    }

    public static void loginUser(String email, String password, OnCompleteListener<AuthResult> listener) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(listener);
    }

    // Reset password functionality
    public static void resetPassword(String email, OnCompleteListener<Void> listener) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(listener);
    }

    public static void logoutUser() {
        auth.signOut();
    }

    public static String getCurrentUserId() {
        if (auth.getCurrentUser() != null) {
            return auth.getCurrentUser().getUid();
        }
        return null;
    }

    // User methods
    public static void getCurrentUser(Consumer<User> callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.accept(null);
            return;
        }

        database.getReference("users").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            user.setUserId(userId);
                        }
                        callback.accept(user);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error getting user", error.toException());
                        callback.accept(null);
                    }
                });
    }

    // Form methods
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void createForm(Form form, Consumer<Boolean> callback) {
        DatabaseReference formsRef = database.getReference("forms");
        String formId = form.getFormId() != null ? form.getFormId() : formsRef.push().getKey();
        form.setFormId(formId);

        // Convert LocalDateTime to String for Firebase
        Map<String, Object> formValues = new HashMap<>();
        formValues.put("formId", form.getFormId());
        formValues.put("title", form.getTitle());
        formValues.put("description", form.getDescription());
        formValues.put("createdBy", form.getCreatedBy());
        formValues.put("createdAt", DateTimeConverter.toString(form.getCreatedAt()));
        formValues.put("lastModified", DateTimeConverter.toString(form.getLastModified()));
        formValues.put("isActive", form.isActive());
        formValues.put("questions", form.getQuestions());
        formValues.put("allowedRollNumbers", form.getAllowedRollNumbers());
        formValues.put("minRollNumber", form.getMinRollNumber());
        formValues.put("maxRollNumber", form.getMaxRollNumber());

        formsRef.child(formId).setValue(formValues)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Send notification to students if form is active
                        if (form.isActive() && form.getMinRollNumber() > 0 && form.getMaxRollNumber() > 0) {
                            sendNewFormNotification(form);
                        }
                        callback.accept(true);
                    } else {
                        callback.accept(false);
                    }
                });
    }

    private static void sendNewFormNotification(Form form) {
        // Implementation for sending notifications would be here
        // This is a placeholder for the notification logic
        Log.d(TAG, "Notification would be sent for new form: " + form.getTitle());
    }

    public static void getForm(String formId, Consumer<Form> callback) {
        database.getReference("forms").child(formId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Form form = parseFormFromSnapshot(snapshot);
                        callback.accept(form);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error getting form", error.toException());
                        callback.accept(null);
                    }
                });
    }

    public static void getFormsCreatedBy(String professorId, Consumer<List<Form>> callback) {
        database.getReference("forms")
                .orderByChild("createdBy")
                .equalTo(professorId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Form> forms = new ArrayList<>();
                        for (DataSnapshot formSnapshot : snapshot.getChildren()) {
                            Form form = parseFormFromSnapshot(formSnapshot);
                            if (form != null) {
                                forms.add(form);
                            }
                        }
                        callback.accept(forms);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error getting forms", error.toException());
                        callback.accept(new ArrayList<>());
                    }
                });
    }

    // Get all forms (both active and inactive) for students
    public static void getAllForms(String studentRollNumber, Consumer<List<Form>> callback) {
        database.getReference("forms")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Form> forms = new ArrayList<>();
                        for (DataSnapshot formSnapshot : snapshot.getChildren()) {
                            Form form = parseFormFromSnapshot(formSnapshot);
                            if (form != null) {
                                forms.add(form);
                            }
                        }
                        callback.accept(forms);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error getting forms", error.toException());
                        callback.accept(new ArrayList<>());
                    }
                });
    }

    public static void getActiveForms(Consumer<List<Form>> callback) {
        database.getReference("forms")
                .orderByChild("isActive")
                .equalTo(true)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Form> forms = new ArrayList<>();
                        for (DataSnapshot formSnapshot : snapshot.getChildren()) {
                            Form form = parseFormFromSnapshot(formSnapshot);
                            if (form != null) {
                                forms.add(form);
                            }
                        }
                        callback.accept(forms);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error getting forms", error.toException());
                        callback.accept(new ArrayList<>());
                    }
                });
    }

    // Method to check if student has access to a form
    public static void checkFormAccess(String formId, String rollNumber, Consumer<String> callback) {
        database.getReference("forms").child(formId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Form form = parseFormFromSnapshot(snapshot);
                        if (form != null) {
                            if (!form.isActive()) {
                                callback.accept("INACTIVE");
                            } else if (!form.isRollNumberAllowed(rollNumber)) {
                                callback.accept("ACCESS_DENIED");
                            } else {
                                callback.accept("ALLOWED");
                            }
                        } else {
                            callback.accept("ERROR");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error checking form access", error.toException());
                        callback.accept("ERROR");
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static Form parseFormFromSnapshot(DataSnapshot snapshot) {
        try {
            Form form = new Form();
            form.setFormId(snapshot.getKey());
            form.setTitle(snapshot.child("title").getValue(String.class));
            form.setDescription(snapshot.child("description").getValue(String.class));
            form.setCreatedBy(snapshot.child("createdBy").getValue(String.class));

            // Parse dates - handle with more error checking
            String createdAtStr = snapshot.child("createdAt").getValue(String.class);
            form.setCreatedAt(DateTimeConverter.toLocalDateTime(createdAtStr));

            String lastModifiedStr = snapshot.child("lastModified").getValue(String.class);
            form.setLastModified(DateTimeConverter.toLocalDateTime(lastModifiedStr));

            // Handle boolean with null check
            Boolean isActive = snapshot.child("isActive").getValue(Boolean.class);
            form.setActive(isActive != null ? isActive : true); // Default to true if null

            // Parse allowed roll numbers
            if (snapshot.hasChild("allowedRollNumbers")) {
                List<String> allowedRolls = new ArrayList<>();
                for (DataSnapshot rollSnapshot : snapshot.child("allowedRollNumbers").getChildren()) {
                    String roll = rollSnapshot.getValue(String.class);
                    if (roll != null) {
                        allowedRolls.add(roll);
                    }
                }
                form.setAllowedRollNumbers(allowedRolls);
            }

            // Parse roll number range
            if (snapshot.hasChild("minRollNumber")) {
                Integer minRoll = snapshot.child("minRollNumber").getValue(Integer.class);
                form.setMinRollNumber(minRoll != null ? minRoll : 0);
            }

            if (snapshot.hasChild("maxRollNumber")) {
                Integer maxRoll = snapshot.child("maxRollNumber").getValue(Integer.class);
                form.setMaxRollNumber(maxRoll != null ? maxRoll : 0);
            }

            // Parse questions with better error handling
            Map<String, Question> questions = new HashMap<>();
            if (snapshot.hasChild("questions")) {
                for (DataSnapshot questionSnapshot : snapshot.child("questions").getChildren()) {
                    try {
                        String questionId = questionSnapshot.getKey();
                        String type = questionSnapshot.child("type").getValue(String.class);
                        String text = questionSnapshot.child("text").getValue(String.class);

                        if (questionId != null && type != null && text != null) {
                            Question question = new Question(questionId, type, text);

                            // Handle different question types
                            if ("multiple_choice".equals(type) && questionSnapshot.hasChild("options")) {
                                List<String> options = new ArrayList<>();
                                for (DataSnapshot optionSnapshot : questionSnapshot.child("options").getChildren()) {
                                    String option = optionSnapshot.getValue(String.class);
                                    if (option != null) {
                                        options.add(option);
                                    }
                                }
                                question.setOptions(options);
                            } else if ("rating".equals(type)) {
                                Integer min = questionSnapshot.child("min").getValue(Integer.class);
                                Integer max = questionSnapshot.child("max").getValue(Integer.class);
                                question.setMin(min != null ? min : 1);
                                question.setMax(max != null ? max : 5);
                            }

                            questions.put(questionId, question);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing question", e);
                        // Continue with other questions
                    }
                }
            }
            form.setQuestions(questions);
            return form;
        } catch (Exception e) {
            Log.e(TAG, "Error parsing form", e);
            return null;
        }
    }

    // Submission methods
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void submitForm(Submission submission, Consumer<Boolean> callback) {
        DatabaseReference submissionsRef = database.getReference("submissions");
        String submissionId = submission.getSubmissionId() != null ?
                submission.getSubmissionId() : submissionsRef.push().getKey();
        submission.setSubmissionId(submissionId);

        // Convert LocalDateTime to String for Firebase
        Map<String, Object> submissionValues = new HashMap<>();
        submissionValues.put("submissionId", submission.getSubmissionId());
        submissionValues.put("formId", submission.getFormId());
        submissionValues.put("studentId", submission.getStudentId());
        submissionValues.put("submittedAt", DateTimeConverter.toString(submission.getSubmittedAt()));
        submissionValues.put("answers", submission.getAnswers());

        submissionsRef.child(submissionId).setValue(submissionValues)
                .addOnCompleteListener(task -> callback.accept(task.isSuccessful()));
    }

    public static void getFormSubmissions(String formId, Consumer<List<Submission>> callback) {
        database.getReference("submissions")
                .orderByChild("formId")
                .equalTo(formId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Submission> submissions = new ArrayList<>();
                        for (DataSnapshot submissionSnapshot : snapshot.getChildren()) {
                            Submission submission = parseSubmissionFromSnapshot(submissionSnapshot);
                            if (submission != null) {
                                submissions.add(submission);
                            }
                        }
                        callback.accept(submissions);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error getting submissions", error.toException());
                        callback.accept(new ArrayList<>());
                    }
                });
    }

    public static void getStudentSubmission(String formId, String studentId, Consumer<Submission> callback) {
        database.getReference("submissions")
                .orderByChild("formId")
                .equalTo(formId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Submission foundSubmission = null;
                        for (DataSnapshot submissionSnapshot : snapshot.getChildren()) {
                            String submissionStudentId = submissionSnapshot.child("studentId").getValue(String.class);
                            if (studentId.equals(submissionStudentId)) {
                                foundSubmission = parseSubmissionFromSnapshot(submissionSnapshot);
                                break;
                            }
                        }
                        callback.accept(foundSubmission);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error getting student submission", error.toException());
                        callback.accept(null);
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static Submission parseSubmissionFromSnapshot(DataSnapshot snapshot) {
        try {
            Submission submission = new Submission();
            submission.setSubmissionId(snapshot.getKey());
            submission.setFormId(snapshot.child("formId").getValue(String.class));
            submission.setStudentId(snapshot.child("studentId").getValue(String.class));

            // Parse date
            String submittedAtStr = snapshot.child("submittedAt").getValue(String.class);
            submission.setSubmittedAt(DateTimeConverter.toLocalDateTime(submittedAtStr));

            // Parse answers
            Map<String, Object> answers = new HashMap<>();
            if (snapshot.hasChild("answers")) {
                for (DataSnapshot answerSnapshot : snapshot.child("answers").getChildren()) {
                    answers.put(answerSnapshot.getKey(), answerSnapshot.getValue());
                }
            }
            submission.setAnswers(answers);
            return submission;
        } catch (Exception e) {
            Log.e(TAG, "Error parsing submission", e);
            return null;
        }
    }

    public static void hasStudentSubmittedForm(String formId, String studentId, Consumer<Boolean> callback) {
        database.getReference("submissions")
                .orderByChild("formId")
                .equalTo(formId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean hasSubmitted = false;
                        for (DataSnapshot submissionSnapshot : snapshot.getChildren()) {
                            String submissionStudentId = submissionSnapshot.child("studentId").getValue(String.class);
                            if (studentId.equals(submissionStudentId)) {
                                hasSubmitted = true;
                                break;
                            }
                        }
                        callback.accept(hasSubmitted);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error checking submission status", error.toException());
                        callback.accept(false);
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void updateFormStatus(String formId, boolean isActive, LocalDateTime lastModified, Consumer<Boolean> callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("isActive", isActive);
        updates.put("lastModified", DateTimeConverter.toString(lastModified));

        database.getReference("forms").child(formId)
                .updateChildren(updates)
                .addOnCompleteListener(task -> callback.accept(task.isSuccessful()));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void updateFormRollNumberRange(String formId, int minRollNumber, int maxRollNumber, Consumer<Boolean> callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("minRollNumber", minRollNumber);
        updates.put("maxRollNumber", maxRollNumber);
        updates.put("lastModified", DateTimeConverter.toString(LocalDateTime.now()));

        database.getReference("forms").child(formId)
                .updateChildren(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Send notifications to students in this roll number range
                        sendFormAccessNotifications(formId, minRollNumber, maxRollNumber);
                        callback.accept(true);
                    } else {
                        callback.accept(false);
                    }
                });
    }

    private static void sendFormAccessNotifications(String formId, int minRollNumber, int maxRollNumber) {
        // Implementation for sending notifications to students in range
        Log.d(TAG, "Notifications would be sent to students with roll numbers from "
                + minRollNumber + " to " + maxRollNumber);
    }

    public static void deleteForm(String formId, Consumer<Boolean> callback) {
        // First, delete all submissions for this form
        database.getReference("submissions")
                .orderByChild("formId")
                .equalTo(formId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Batch delete all submissions
                        Map<String, Object> submissionUpdates = new HashMap<>();
                        for (DataSnapshot submissionSnapshot : snapshot.getChildren()) {
                            submissionUpdates.put(submissionSnapshot.getKey(), null);
                        }

                        if (!submissionUpdates.isEmpty()) {
                            database.getReference("submissions").updateChildren(submissionUpdates);
                        }

                        // Also delete all access requests for this form
                        database.getReference("accessRequests")
                                .orderByChild("formId")
                                .equalTo(formId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Map<String, Object> requestUpdates = new HashMap<>();
                                        for (DataSnapshot requestSnapshot : snapshot.getChildren()) {
                                            requestUpdates.put(requestSnapshot.getKey(), null);
                                        }

                                        if (!requestUpdates.isEmpty()) {
                                            database.getReference("accessRequests").updateChildren(requestUpdates);
                                        }

                                        // Now delete the form itself
                                        database.getReference("forms").child(formId).removeValue()
                                                .addOnCompleteListener(task -> callback.accept(task.isSuccessful()));
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e(TAG, "Error deleting access requests", error.toException());
                                        // Still proceed with form deletion
                                        database.getReference("forms").child(formId).removeValue()
                                                .addOnCompleteListener(task -> callback.accept(task.isSuccessful()));
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error deleting submissions", error.toException());
                        callback.accept(false);
                    }
                });
    }

    // Access Request Methods
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void createAccessRequest(AccessRequest request, Consumer<Boolean> callback) {
        DatabaseReference requestsRef = database.getReference("accessRequests");
        String requestId = request.getRequestId() != null ? request.getRequestId() : requestsRef.push().getKey();
        request.setRequestId(requestId);

        Map<String, Object> requestValues = new HashMap<>();
        requestValues.put("requestId", request.getRequestId());
        requestValues.put("formId", request.getFormId());
        requestValues.put("studentId", request.getStudentId());
        requestValues.put("studentRollNumber", request.getStudentRollNumber());
        requestValues.put("studentName", request.getStudentName());
        requestValues.put("requestType", request.getRequestType());
        requestValues.put("requestStatus", request.getRequestStatus());
        requestValues.put("requestedAt", DateTimeConverter.toString(request.getRequestedAt()));
        if (request.getMessage() != null) {
            requestValues.put("message", request.getMessage());
        }

        requestsRef.child(requestId).setValue(requestValues)
                .addOnCompleteListener(task -> callback.accept(task.isSuccessful()));
    }

    public static void getFormAccessRequests(String formId, Consumer<List<AccessRequest>> callback) {
        database.getReference("accessRequests")
                .orderByChild("formId")
                .equalTo(formId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<AccessRequest> requests = new ArrayList<>();
                        for (DataSnapshot requestSnapshot : snapshot.getChildren()) {
                            AccessRequest request = parseRequestFromSnapshot(requestSnapshot);
                            if (request != null) {
                                requests.add(request);
                            }
                        }
                        callback.accept(requests);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error getting access requests", error.toException());
                        callback.accept(new ArrayList<>());
                    }
                });
    }

    public static void getFormPendingAccessRequests(String formId, Consumer<List<AccessRequest>> callback) {
        database.getReference("accessRequests")
                .orderByChild("formId")
                .equalTo(formId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<AccessRequest> requests = new ArrayList<>();
                        for (DataSnapshot requestSnapshot : snapshot.getChildren()) {
                            if ("PENDING".equals(requestSnapshot.child("requestStatus").getValue(String.class))) {
                                AccessRequest request = parseRequestFromSnapshot(requestSnapshot);
                                if (request != null) {
                                    requests.add(request);
                                }
                            }
                        }
                        callback.accept(requests);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error getting pending access requests", error.toException());
                        callback.accept(new ArrayList<>());
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static AccessRequest parseRequestFromSnapshot(DataSnapshot snapshot) {
        try {
            AccessRequest request = new AccessRequest();
            request.setRequestId(snapshot.getKey());
            request.setFormId(snapshot.child("formId").getValue(String.class));
            request.setStudentId(snapshot.child("studentId").getValue(String.class));
            request.setStudentRollNumber(snapshot.child("studentRollNumber").getValue(String.class));
            request.setStudentName(snapshot.child("studentName").getValue(String.class));
            request.setRequestType(snapshot.child("requestType").getValue(String.class));
            request.setRequestStatus(snapshot.child("requestStatus").getValue(String.class));

            // Parse dates
            String requestedAtStr = snapshot.child("requestedAt").getValue(String.class);
            request.setRequestedAt(DateTimeConverter.toLocalDateTime(requestedAtStr));

            if (snapshot.hasChild("respondedAt")) {
                String respondedAtStr = snapshot.child("respondedAt").getValue(String.class);
                request.setRespondedAt(DateTimeConverter.toLocalDateTime(respondedAtStr));
            }

            if (snapshot.hasChild("message")) {
                request.setMessage(snapshot.child("message").getValue(String.class));
            }

            return request;
        } catch (Exception e) {
            Log.e(TAG, "Error parsing access request", e);
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void approveAccessRequest(String requestId, Consumer<Boolean> callback) {
        database.getReference("accessRequests").child(requestId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        AccessRequest request = parseRequestFromSnapshot(snapshot);
                        if (request == null) {
                            callback.accept(false);
                            return;
                        }

                        // Update request status
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("requestStatus", "APPROVED");
                        updates.put("respondedAt", DateTimeConverter.toString(LocalDateTime.now()));

                        database.getReference("accessRequests").child(requestId)
                                .updateChildren(updates)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        handleApprovedRequest(request, callback);
                                    } else {
                                        callback.accept(false);
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error approving request", error.toException());
                        callback.accept(false);
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void rejectAccessRequest(String requestId, Consumer<Boolean> callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("requestStatus", "REJECTED");
        updates.put("respondedAt", DateTimeConverter.toString(LocalDateTime.now()));

        database.getReference("accessRequests").child(requestId)
                .updateChildren(updates)
                .addOnCompleteListener(task -> callback.accept(task.isSuccessful()));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void handleApprovedRequest(AccessRequest request, Consumer<Boolean> callback) {
        switch (request.getRequestType()) {
            case "ACCESS":
                // Add student roll number to allowed list
                addRollNumberToForm(request.getFormId(), request.getStudentRollNumber(), callback);
                break;
            case "RESUBMIT":
                // Delete previous submission to allow resubmission
                deleteStudentSubmission(request.getFormId(), request.getStudentId(), callback);
                break;
            case "ACTIVATE":
                // Reactivate the form
                updateFormStatus(request.getFormId(), true, LocalDateTime.now(), callback);
                break;
            default:
                callback.accept(false);
                break;
        }
    }

    private static void addRollNumberToForm(String formId, String rollNumber, Consumer<Boolean> callback) {
        database.getReference("forms").child(formId).child("allowedRollNumbers")
                .push().setValue(rollNumber)
                .addOnCompleteListener(task -> callback.accept(task.isSuccessful()));
    }

    private static void deleteStudentSubmission(String formId, String studentId, Consumer<Boolean> callback) {
        database.getReference("submissions")
                .orderByChild("formId")
                .equalTo(formId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot submissionSnapshot : snapshot.getChildren()) {
                            String submissionStudentId = submissionSnapshot.child("studentId").getValue(String.class);
                            if (studentId.equals(submissionStudentId)) {
                                submissionSnapshot.getRef().removeValue()
                                        .addOnCompleteListener(task -> callback.accept(task.isSuccessful()));
                                return;
                            }
                        }
                        callback.accept(false);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error deleting student submission", error.toException());
                        callback.accept(false);
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void approveAllRequests(String formId, Consumer<Boolean> callback) {
        getFormPendingAccessRequests(formId, requests -> {
            if (requests.isEmpty()) {
                callback.accept(true);
                return;
            }

            final int[] completedCount = {0};
            final boolean[] allSuccessful = {true};

            for (AccessRequest request : requests) {
                approveAccessRequest(request.getRequestId(), success -> {
                    if (!success) {
                        allSuccessful[0] = false;
                    }
                    completedCount[0]++;

                    if (completedCount[0] == requests.size()) {
                        callback.accept(allSuccessful[0]);
                    }
                });
            }
        });
    }

    public static void getAllProfessors(Consumer<List<User>> callback) {
        database.getReference("users")
                .orderByChild("role")
                .equalTo("professor")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<User> professors = new ArrayList<>();
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            User user = userSnapshot.getValue(User.class);
                            if (user != null) {
                                user.setUserId(userSnapshot.getKey());
                                professors.add(user);
                            }
                        }
                        callback.accept(professors);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error getting professors", error.toException());
                        callback.accept(new ArrayList<>());
                    }
                });
    }

    public static void sendPasswordResetRequest(String studentEmail, String rollNumber, String professorId, OnCompleteListener<Void> listener) {
        // First verify email and roll number match
        verifyStudentEmailAndRoll(studentEmail, rollNumber, isValid -> {
            if (!isValid) {
                // Email and roll number don't match
                listener.onComplete(Tasks.forException(
                        new Exception("Email and roll number do not match any student account")));
                return;
            }
            Map<String, Object> request = new HashMap<>();
            request.put("studentEmail", studentEmail);
            request.put("studentRollNumber", rollNumber);
            request.put("requestedAt", ServerValue.TIMESTAMP);
            request.put("status", "pending");

            database.getReference("passwordResetRequests")
                    .push()
                    .setValue(request)
                    .addOnCompleteListener(listener);

            // Send notification to professor (optional)
            database.getReference("users").child(professorId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User professor = snapshot.getValue(User.class);
                            if (professor != null && professor.getEmail() != null) {
                                // Here you would typically send an email notification to the professor
                                // For now, let's just log it
                                Log.d(TAG, "Password reset request sent to professor: " + professor.getEmail());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "Error getting professor details", error.toException());
                        }
                    });
        });
    }

    public static void getPendingPasswordResetRequests(Consumer<List<PasswordResetRequest>> callback) {
        try {
            database.getReference("passwordResetRequests")
                    .orderByChild("status")
                    .equalTo("pending")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            List<PasswordResetRequest> requests = new ArrayList<>();
                            for (DataSnapshot requestSnapshot : snapshot.getChildren()) {
                                try {
                                    String requestId = requestSnapshot.getKey();
                                    String studentEmail = requestSnapshot.child("studentEmail").getValue(String.class);
                                    String studentRollNumber = requestSnapshot.child("studentRollNumber").getValue(String.class);
                                    String status = requestSnapshot.child("status").getValue(String.class);

                                    // Get timestamp - handle both Long and String types
                                    long requestedAt = 0;
                                    Object timestampObj = requestSnapshot.child("requestedAt").getValue();
                                    if (timestampObj instanceof Long) {
                                        requestedAt = (Long) timestampObj;
                                    } else if (timestampObj instanceof String) {
                                        try {
                                            requestedAt = Long.parseLong((String) timestampObj);
                                        } catch (NumberFormatException e) {
                                            Log.e(TAG, "Error parsing timestamp", e);
                                        }
                                    }

                                    PasswordResetRequest request = new PasswordResetRequest(
                                            requestId, studentEmail, studentRollNumber, requestedAt, status);
                                    requests.add(request);

                                } catch (Exception e) {
                                    Log.e(TAG, "Error parsing password reset request", e);
                                }
                            }
                            callback.accept(requests);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "Error getting password reset requests", error.toException());
                            callback.accept(new ArrayList<>());
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error in getPendingPasswordResetRequests", e);
            callback.accept(new ArrayList<>());
        }
    }

    public static void resetPasswordForStudent(String studentEmail, String rollNumber, Consumer<Boolean> callback) {
        if (studentEmail == null || studentEmail.isEmpty()) {
            callback.accept(false);
            return;
        }

        try {
            // Send password reset email
            auth.sendPasswordResetEmail(studentEmail)
                    .addOnCompleteListener(task -> {
                        boolean success = task.isSuccessful();

                        if (success) {
                            // Store password reset information in the database for reference
                            Map<String, Object> resetInfo = new HashMap<>();
                            resetInfo.put("email", studentEmail);
                            resetInfo.put("rollNumber", rollNumber);
                            resetInfo.put("resetTime", ServerValue.TIMESTAMP);
                            resetInfo.put("defaultPasswordFormat", "Pict_" + rollNumber);

                            database.getReference("passwordResetRecords")
                                    .push()
                                    .setValue(resetInfo);

                            Log.d(TAG, "Password reset email sent to: " + studentEmail);
                        } else if (task.getException() != null) {
                            Log.e(TAG, "Error sending password reset email", task.getException());
                        }

                        callback.accept(success);
                    });
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error in resetPasswordForStudent", e);
            callback.accept(false);
        }
    }

    public static void getUserByEmail(String email, Consumer<User> callback) {
        if (email == null || email.isEmpty()) {
            callback.accept(null);
            return;
        }

        database.getReference("users")
                .orderByChild("email")
                .equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = null;
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            user = userSnapshot.getValue(User.class);
                            if (user != null) {
                                user.setUserId(userSnapshot.getKey());
                                break; // Take the first matching user
                            }
                        }
                        callback.accept(user);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error getting user by email", error.toException());
                        callback.accept(null);
                    }
                });
    }

    public static void markPasswordResetRequestAsApproved(String requestId, Consumer<Boolean> callback) {
        if (requestId == null || requestId.isEmpty()) {
            callback.accept(false);
            return;
        }

        try {
            Map<String, Object> updates = new HashMap<>();
            updates.put("status", "approved");
            updates.put("approvedAt", ServerValue.TIMESTAMP);

            database.getReference("passwordResetRequests").child(requestId)
                    .updateChildren(updates)
                    .addOnCompleteListener(task -> callback.accept(task.isSuccessful()));
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error in markPasswordResetRequestAsApproved", e);
            callback.accept(false);
        }
    }

    public static void markPasswordResetRequestAsRejected(String requestId, Consumer<Boolean> callback) {
        if (requestId == null || requestId.isEmpty()) {
            callback.accept(false);
            return;
        }

        try {
            Map<String, Object> updates = new HashMap<>();
            updates.put("status", "rejected");
            updates.put("rejectedAt", ServerValue.TIMESTAMP);

            database.getReference("passwordResetRequests").child(requestId)
                    .updateChildren(updates)
                    .addOnCompleteListener(task -> callback.accept(task.isSuccessful()));
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error in markPasswordResetRequestAsRejected", e);
            callback.accept(false);
        }
    }

    public static void getUserById(String userId, Consumer<User> callback) {
        if (userId == null) {
            callback.accept(null);
            return;
        }

        database.getReference("users").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            user.setUserId(userId);
                        }
                        callback.accept(user);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error getting user", error.toException());
                        callback.accept(null);
                    }
                });
    }

    public static void checkIfSubmissionsExist(Consumer<Boolean> callback) {
        database.getReference("submissions")
                .limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        callback.accept(snapshot.exists() && snapshot.getChildrenCount() > 0);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error checking submissions", error.toException());
                        callback.accept(false);
                    }
                });
    }

    public static void checkFormSubmissions(String formId, Consumer<Boolean> callback) {
        if (formId == null || formId.isEmpty()) {
            callback.accept(false);
            return;
        }

        database.getReference("submissions")
                .orderByChild("formId")
                .equalTo(formId)
                .limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        callback.accept(snapshot.exists() && snapshot.getChildrenCount() > 0);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error checking form submissions", error.toException());
                        callback.accept(false);
                    }
                });
    }

    public static void getFormRollRange(String formId, BiConsumer<Integer, Integer> callback) {
        if (formId == null || formId.isEmpty()) {
            callback.accept(0, 0);
            return;
        }

        database.getReference("forms").child(formId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int minRoll = 0;
                        int maxRoll = 0;

                        if (snapshot.exists()) {
                            if (snapshot.hasChild("minRoll")) {
                                Object minRollObj = snapshot.child("minRoll").getValue();
                                if (minRollObj instanceof Long) {
                                    minRoll = ((Long) minRollObj).intValue();
                                } else if (minRollObj instanceof Integer) {
                                    minRoll = (Integer) minRollObj;
                                } else if (minRollObj instanceof String) {
                                    try {
                                        minRoll = Integer.parseInt((String) minRollObj);
                                    } catch (NumberFormatException ignored) {}
                                }
                            }

                            if (snapshot.hasChild("maxRoll")) {
                                Object maxRollObj = snapshot.child("maxRoll").getValue();
                                if (maxRollObj instanceof Long) {
                                    maxRoll = ((Long) maxRollObj).intValue();
                                } else if (maxRollObj instanceof Integer) {
                                    maxRoll = (Integer) maxRollObj;
                                } else if (maxRollObj instanceof String) {
                                    try {
                                        maxRoll = Integer.parseInt((String) maxRollObj);
                                    } catch (NumberFormatException ignored) {}
                                }
                            }
                        }

                        callback.accept(minRoll, maxRoll);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error getting form roll range", error.toException());
                        callback.accept(0, 0);
                    }
                });
    }

    public static void updateFormRollRange(String formId, int minRoll, int maxRoll, Consumer<Boolean> callback) {
        if (formId == null || formId.isEmpty()) {
            callback.accept(false);
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("minRoll", minRoll);
        updates.put("maxRoll", maxRoll);

        database.getReference("forms").child(formId)
                .updateChildren(updates)
                .addOnCompleteListener(task -> callback.accept(task.isSuccessful()));
    }

    public static void checkExistingPasswordResetRequest(String email, Consumer<Boolean> callback) {
        if (email == null || email.isEmpty()) {
            callback.accept(false);
            return;
        }

        database.getReference("passwordResetRequests")
                .orderByChild("studentEmail")
                .equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean exists = false;

                        for (DataSnapshot requestSnapshot : snapshot.getChildren()) {
                            String status = requestSnapshot.child("status").getValue(String.class);
                            if ("pending".equals(status)) {
                                exists = true;
                                break;
                            }
                        }

                        callback.accept(exists);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error checking existing password reset request", error.toException());
                        callback.accept(false);
                    }
                });
    }

    public static void getFormAnalytics(String formId, Consumer<Map<String, Object>> callback) {
        // First get the form to get questions
        database.getReference("forms").child(formId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot formSnapshot) {
                        try {
                            Form form = parseFormFromSnapshot(formSnapshot);
                            if (form == null) {
                                callback.accept(new HashMap<>());
                                return;
                            }

                            // Then get all submissions for this form
                            getFormSubmissions(formId, submissions -> {
                                Map<String, Object> analytics = new HashMap<>();
                                analytics.put("totalSubmissions", submissions.size());

                                // Process each question and preserve order
                                Map<String, Map<String, Object>> questionAnalytics = new LinkedHashMap<>(); // Using LinkedHashMap to maintain order

                                // Get questions in the original order if possible
                                DataSnapshot questionsSnapshot = formSnapshot.child("questions");
                                for (DataSnapshot questionSnapshot : questionsSnapshot.getChildren()) {
                                    String questionId = questionSnapshot.getKey();
                                    Question question = form.getQuestions().get(questionId);

                                    if (question != null) {
                                        Map<String, Object> questionStats = analyzeQuestion(question, submissions);
                                        // Add the question order as metadata
                                        questionStats.put("questionOrder", questionAnalytics.size());
                                        questionAnalytics.put(questionId, questionStats);
                                    }
                                }

                                analytics.put("questions", questionAnalytics);
                                callback.accept(analytics);
                            });
                        } catch (Exception e) {
                            Log.e("FirebaseHelper", "Error analyzing form data", e);
                            callback.accept(new HashMap<>());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FirebaseHelper", "Error getting form for analytics", error.toException());
                        callback.accept(new HashMap<>());
                    }
                });
    }

    private static Map<String, Object> analyzeQuestion(Question question, List<Submission> submissions) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("questionText", question.getText());
        stats.put("questionType", question.getType());

        // Different analysis based on question type
        switch (question.getType()) {
            case "text":
                // For text, collect all responses
                List<String> textResponses = new ArrayList<>();
                for (Submission submission : submissions) {
                    if (submission.getAnswers() != null && submission.getAnswers().containsKey(question.getQuestionId())) {
                        Object answer = submission.getAnswers().get(question.getQuestionId());
                        if (answer != null) {
                            textResponses.add(answer.toString());
                        }
                    }
                }
                stats.put("responses", textResponses);
                break;

            case "multiple_choice":
                // For multiple choice, count occurrences of each option
                Map<String, Integer> optionCounts = new HashMap<>();

                // Initialize counts for all options
                if (question.getOptions() != null) {
                    for (String option : question.getOptions()) {
                        optionCounts.put(option, 0);
                    }
                }

                for (Submission submission : submissions) {
                    if (submission.getAnswers() != null && submission.getAnswers().containsKey(question.getQuestionId())) {
                        Object answer = submission.getAnswers().get(question.getQuestionId());
                        if (answer != null) {
                            String selectedOption = answer.toString();
                            optionCounts.put(selectedOption, optionCounts.getOrDefault(selectedOption, 0) + 1);
                        }
                    }
                }
                stats.put("optionCounts", optionCounts);
                break;

            case "rating":
                // For rating, calculate average and distribution
                List<Integer> ratings = new ArrayList<>();
                Map<String, Integer> ratingCounts = new HashMap<>();

                // Initialize counts for all possible ratings
                for (int i = question.getMin(); i <= question.getMax(); i++) {
                    ratingCounts.put(String.valueOf(i), 0);
                }

                for (Submission submission : submissions) {
                    if (submission.getAnswers() != null && submission.getAnswers().containsKey(question.getQuestionId())) {
                        Object answer = submission.getAnswers().get(question.getQuestionId());
                        if (answer != null) {
                            try {
                                int rating;
                                if (answer instanceof Long) {
                                    rating = ((Long) answer).intValue();
                                } else if (answer instanceof Integer) {
                                    rating = (Integer) answer;
                                } else {
                                    rating = Integer.parseInt(answer.toString());
                                }

                                ratings.add(rating);
                                String ratingKey = String.valueOf(rating);
                                ratingCounts.put(ratingKey, ratingCounts.getOrDefault(ratingKey, 0) + 1);
                            } catch (NumberFormatException e) {
                                Log.e("FirebaseHelper", "Error parsing rating", e);
                            }
                        }
                    }
                }

                // Calculate average if there are ratings
                double average = 0;
                if (!ratings.isEmpty()) {
                    int sum = 0;
                    for (int rating : ratings) {
                        sum += rating;
                    }
                    average = (double) sum / ratings.size();
                }

                stats.put("average", average);
                stats.put("ratingCounts", ratingCounts);
                break;
        }

        return stats;
    }

    public static void checkPendingAccessRequest(String formId, String studentId, Consumer<Boolean> callback) {
        database.getReference("accessRequests")
                .orderByChild("formId")
                .equalTo(formId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean hasPending = false;
                        for (DataSnapshot requestSnapshot : snapshot.getChildren()) {
                            String reqStudentId = requestSnapshot.child("studentId").getValue(String.class);
                            String status = requestSnapshot.child("requestStatus").getValue(String.class);
                            if (studentId.equals(reqStudentId) && "PENDING".equals(status)) {
                                hasPending = true;
                                break;
                            }
                        }
                        callback.accept(hasPending);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error checking pending requests", error.toException());
                        callback.accept(false);
                    }
                });
    }

    public static void verifyStudentEmailAndRoll(String email, String rollNumber, Consumer<Boolean> callback) {
        database.getReference("users")
                .orderByChild("role")
                .equalTo("student")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean isValid = false;
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            String userEmail = userSnapshot.child("email").getValue(String.class);
                            String userRoll = userSnapshot.child("rollNumber").getValue(String.class);
                            if (email != null && email.equalsIgnoreCase(userEmail) &&
                                    rollNumber != null && rollNumber.equals(userRoll)) {
                                isValid = true;
                                break;
                            }
                        }
                        callback.accept(isValid);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error verifying student data", error.toException());
                        callback.accept(false);
                    }
                });
    }
}