package com.example.ad_pbl_activityfeedback_q5.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ad_pbl_activityfeedback_q5.R;
import com.example.ad_pbl_activityfeedback_q5.models.Form;
import com.example.ad_pbl_activityfeedback_q5.utils.DateTimeConverter;
import com.example.ad_pbl_activityfeedback_q5.utils.FirebaseHelper;

import java.time.LocalDateTime;
import java.util.List;

public class ProfessorFormAdapter extends RecyclerView.Adapter<ProfessorFormAdapter.FormViewHolder> {

    private final Context context;
    private final List<Form> forms;
    private final OnFormActionListener listener;

    public interface OnFormActionListener {
        void onViewResults(Form form);
        void onViewAnalytics(Form form);
        void onManageRequests(Form form);
        void onToggleStatus(Form form, int position);
        void onDelete(Form form, int position);
        void onModifyRollRange(Form form);
    }

    public ProfessorFormAdapter(Context context, List<Form> forms, OnFormActionListener listener) {
        this.context = context;
        this.forms = forms;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FormViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_professor_form, parent, false);
        return new FormViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull FormViewHolder holder, int position) {
        Form form = forms.get(position);
        holder.titleTextView.setText(form.getTitle());
        holder.descriptionTextView.setText(form.getDescription());
        holder.createdAtTextView.setText("Created: " + DateTimeConverter.formatForDisplay(form.getCreatedAt()));

        // Set roll range info if available
        if (form.getMinRollNumber() > 0 && form.getMaxRollNumber() > 0) {
            holder.rollRangeTextView.setText("Roll Range: " + form.getMinRollNumber() + " - " + form.getMaxRollNumber());
            holder.rollRangeTextView.setVisibility(View.VISIBLE);
        } else {
            holder.rollRangeTextView.setVisibility(View.GONE);
        }

        // Set status and color
        if (form.isActive()) {
            holder.statusTextView.setText("Active");
            holder.statusTextView.setTextColor(context.getResources().getColor(R.color.colorSuccess));
        } else {
            holder.statusTextView.setText("Inactive");
            holder.statusTextView.setTextColor(context.getResources().getColor(R.color.colorError));
        }

        // Configure toggle button
        Button toggleButton = holder.toggleStatusButton;
        if (form.isActive()) {
            toggleButton.setText("Deactivate Form");
            toggleButton.setBackgroundResource(R.drawable.button_secondary);
        } else {
            toggleButton.setText("Activate Form");
            toggleButton.setBackgroundResource(R.drawable.button_primary);
            toggleButton.setTextColor(context.getResources().getColor(android.R.color.white));
        }

        toggleButton.setOnClickListener(v -> {
            // Toggle the active status
            form.setActive(!form.isActive());
            form.setLastModified(LocalDateTime.now());

            // Update in Firebase
            FirebaseHelper.updateFormStatus(form.getFormId(), form.isActive(), form.getLastModified(), success -> {
                if (success) {
                    notifyItemChanged(position);
                } else {
                    // Revert if failed
                    form.setActive(!form.isActive());
                    Toast.makeText(context, "Failed to update form status", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Setup action buttons
        holder.viewResultsButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewResults(form);
            }
        });

        holder.analyticsButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewAnalytics(form);
            }
        });

        holder.requestsButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onManageRequests(form);
            }
        });

        holder.rollRangeButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onModifyRollRange(form);
            }
        });

        // Setup delete button
        holder.deleteButton.setOnClickListener(v -> {
            // Show confirmation dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete Form");
            builder.setMessage("Are you sure you want to delete this form? This will also delete all submissions and requests for this form. This action cannot be undone.");
            builder.setPositiveButton("Delete", (dialog, which) -> {
                // Delete the form
                FirebaseHelper.deleteForm(form.getFormId(), success -> {
                    if (success) {
                        forms.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Form deleted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed to delete form", Toast.LENGTH_SHORT).show();
                    }
                });
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.show();
        });
    }

    @Override
    public int getItemCount() {
        return forms.size();
    }

    static class FormViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView titleTextView;
        TextView descriptionTextView;
        TextView createdAtTextView;
        TextView statusTextView;
        TextView rollRangeTextView;
        Button toggleStatusButton;
        Button viewResultsButton;
        Button analyticsButton;
        Button requestsButton;
        Button rollRangeButton;
        ImageButton deleteButton;

        FormViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.form_card_view);
            titleTextView = itemView.findViewById(R.id.form_title_text_view);
            descriptionTextView = itemView.findViewById(R.id.form_description_text_view);
            createdAtTextView = itemView.findViewById(R.id.form_created_at_text_view);
            statusTextView = itemView.findViewById(R.id.form_status_text_view);
            rollRangeTextView = itemView.findViewById(R.id.form_roll_range_text_view);
            toggleStatusButton = itemView.findViewById(R.id.toggle_status_button);
            viewResultsButton = itemView.findViewById(R.id.view_results_button);
            analyticsButton = itemView.findViewById(R.id.analytics_button);
            requestsButton = itemView.findViewById(R.id.requests_button);
            rollRangeButton = itemView.findViewById(R.id.roll_range_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}