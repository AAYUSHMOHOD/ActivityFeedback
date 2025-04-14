package com.example.ad_pbl_activityfeedback_q5.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ad_pbl_activityfeedback_q5.R;
import com.example.ad_pbl_activityfeedback_q5.models.Form;
import com.example.ad_pbl_activityfeedback_q5.utils.DateTimeConverter;

import java.util.List;

public class StudentFormAdapter extends RecyclerView.Adapter<StudentFormAdapter.FormViewHolder> {

    private final Context context;
    private final List<Form> forms;
    private final OnFormClickListener listener;
    private final String studentRollNumber;
    private final List<String> submittedFormIds;

    public interface OnFormClickListener {
        void onFormClick(Form form, String status);
        void onRequestAccess(Form form, String requestType);
    }

    public StudentFormAdapter(Context context, List<Form> forms, String studentRollNumber,
                              List<String> submittedFormIds, OnFormClickListener listener) {
        this.context = context;
        this.forms = forms;
        this.listener = listener;
        this.studentRollNumber = studentRollNumber;
        this.submittedFormIds = submittedFormIds;
    }

    @NonNull
    @Override
    public FormViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_student_form, parent, false);
        return new FormViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull FormViewHolder holder, int position) {
        Form form = forms.get(position);
        holder.titleTextView.setText(form.getTitle());
        holder.descriptionTextView.setText(form.getDescription());
        holder.createdAtTextView.setText("Created: " + DateTimeConverter.formatForDisplay(form.getCreatedAt()));

        // Determine form status for the student
        String status = getFormStatus(form);

        holder.statusTextView.setText(status);

        // Set status color
        int statusColor;
        switch (status) {
            case "Submitted":
                statusColor = context.getResources().getColor(R.color.colorInfo);
                break;
            case "Active":
                statusColor = context.getResources().getColor(R.color.colorSuccess);
                break;
            case "Inactive":
                statusColor = context.getResources().getColor(R.color.colorWarning);
                break;
            case "Access Denied":
                statusColor = context.getResources().getColor(R.color.colorError);
                break;
            default:
                statusColor = context.getResources().getColor(android.R.color.darker_gray);
                break;
        }
        holder.statusTextView.setTextColor(statusColor);

        // Configure request button visibility and text
        if ("Access Denied".equals(status)) {
            holder.requestButton.setVisibility(View.VISIBLE);
            holder.requestButton.setText("Request Access");
            holder.requestButton.setOnClickListener(v ->
                    listener.onRequestAccess(form, "ACCESS"));
        } else if ("Inactive".equals(status)) {
            holder.requestButton.setVisibility(View.VISIBLE);
            holder.requestButton.setText("Request Activation");
            holder.requestButton.setOnClickListener(v ->
                    listener.onRequestAccess(form, "ACTIVATE"));
        } else if ("Submitted".equals(status)) {
            holder.requestButton.setVisibility(View.VISIBLE);
            holder.requestButton.setText("Request Resubmission");
            holder.requestButton.setOnClickListener(v ->
                    listener.onRequestAccess(form, "RESUBMIT"));
        } else {
            holder.requestButton.setVisibility(View.GONE);
        }

        // Set click listener for the whole item
        holder.cardView.setOnClickListener(v -> listener.onFormClick(form, status));
    }

    @Override
    public int getItemCount() {
        return forms.size();
    }

    private String getFormStatus(Form form) {
        // Check if student has already submitted this form
        if (submittedFormIds.contains(form.getFormId())) {
            return "Submitted";
        }

        // Check if form is active
        if (!form.isActive()) {
            return "Inactive";
        }

        // Check if student has access
        if (!form.isRollNumberAllowed(studentRollNumber)) {
            return "Access Denied";
        }

        return "Active";
    }

    static class FormViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        TextView createdAtTextView;
        TextView statusTextView;
        Button requestButton;
        CardView cardView;

        FormViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.form_title_text_view);
            descriptionTextView = itemView.findViewById(R.id.form_description_text_view);
            createdAtTextView = itemView.findViewById(R.id.form_created_at_text_view);
            statusTextView = itemView.findViewById(R.id.form_status_text_view);
            requestButton = itemView.findViewById(R.id.form_request_button);
            cardView = itemView.findViewById(R.id.form_card_view);
        }
    }
}