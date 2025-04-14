package com.example.ad_pbl_activityfeedback_q5.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ad_pbl_activityfeedback_q5.R;
import com.example.ad_pbl_activityfeedback_q5.models.PasswordResetRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PasswordResetRequestAdapter extends RecyclerView.Adapter<PasswordResetRequestAdapter.RequestViewHolder> {

    private final Context context;
    private final List<PasswordResetRequest> requests;
    private final OnRequestActionListener listener;

    public interface OnRequestActionListener {
        void onApprove(PasswordResetRequest request, int position);
        void onReject(PasswordResetRequest request, int position);
    }

    public PasswordResetRequestAdapter(Context context, List<PasswordResetRequest> requests, OnRequestActionListener listener) {
        this.context = context;
        this.requests = requests;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_password_reset_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return requests != null ? requests.size() : 0;
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        PasswordResetRequest request = requests.get(position);

        // Set email
        holder.emailTextView.setText(request.getStudentEmail());

        // Set roll number and password format
        String rollNumber = request.getStudentRollNumber();
        if (rollNumber != null && !rollNumber.isEmpty()) {
            holder.rollNumberTextView.setVisibility(View.VISIBLE);
            holder.rollNumberTextView.setText("Roll Number: " + rollNumber);

            // Display the password format
            holder.passwordFormatTextView.setVisibility(View.VISIBLE);
            holder.passwordFormatTextView.setText("When approved, password will be: Pict_" + rollNumber);
        } else {
            holder.rollNumberTextView.setVisibility(View.GONE);
            holder.passwordFormatTextView.setVisibility(View.GONE);
        }

        // Format date
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String formattedDate = dateFormat.format(new Date(request.getRequestedAt()));
            holder.dateTextView.setText("Requested: " + formattedDate);
        } catch (Exception e) {
            holder.dateTextView.setText("Requested: Unknown date");
        }

        // Set button click listeners
        holder.approveButton.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION && listener != null) {
                listener.onApprove(request, adapterPosition);
            }
        });

        holder.rejectButton.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION && listener != null) {
                listener.onReject(request, adapterPosition);
            }
        });
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView emailTextView;
        TextView rollNumberTextView;
        TextView dateTextView;
        TextView passwordFormatTextView;
        Button approveButton;
        Button rejectButton;

        RequestViewHolder(View itemView) {
            super(itemView);
            emailTextView = itemView.findViewById(R.id.email_text_view);
            rollNumberTextView = itemView.findViewById(R.id.roll_number_text_view);
            dateTextView = itemView.findViewById(R.id.date_text_view);
            passwordFormatTextView = itemView.findViewById(R.id.password_format_text_view);
            approveButton = itemView.findViewById(R.id.approve_button);
            rejectButton = itemView.findViewById(R.id.reject_button);
        }
    }
}