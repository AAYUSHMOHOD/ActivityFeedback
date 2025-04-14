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
import androidx.recyclerview.widget.RecyclerView;

import com.example.ad_pbl_activityfeedback_q5.R;
import com.example.ad_pbl_activityfeedback_q5.models.AccessRequest;
import com.example.ad_pbl_activityfeedback_q5.utils.DateTimeConverter;

import java.util.List;

public class AccessRequestAdapter extends RecyclerView.Adapter<AccessRequestAdapter.RequestViewHolder> {

    private final Context context;
    private final List<AccessRequest> requests;
    private final OnRequestActionListener listener;

    public interface OnRequestActionListener {
        void onApprove(AccessRequest request, int position);
        void onReject(AccessRequest request, int position);
    }

    public AccessRequestAdapter(Context context, List<AccessRequest> requests, OnRequestActionListener listener) {
        this.context = context;
        this.requests = requests;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_access_request, parent, false);
        return new RequestViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        AccessRequest request = requests.get(position);

        String requestType;
        switch (request.getRequestType()) {
            case "ACCESS":
                requestType = "Access Request";
                break;
            case "RESUBMIT":
                requestType = "Resubmission Request";
                break;
            case "ACTIVATE":
                requestType = "Activation Request";
                break;
            default:
                requestType = "Request";
                break;
        }

        holder.typeTextView.setText(requestType);
        holder.studentTextView.setText("From: " + request.getStudentName() + " (Roll: " + request.getStudentRollNumber() + ")");
        holder.dateTextView.setText("Requested: " + DateTimeConverter.formatForDisplay(request.getRequestedAt()));

        if (request.getMessage() != null && !request.getMessage().isEmpty()) {
            holder.messageTextView.setText("Message: " + request.getMessage());
            holder.messageTextView.setVisibility(View.VISIBLE);
        } else {
            holder.messageTextView.setVisibility(View.GONE);
        }

        holder.approveButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onApprove(request, holder.getAdapterPosition());
            }
        });

        holder.rejectButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReject(request, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView typeTextView;
        TextView studentTextView;
        TextView dateTextView;
        TextView messageTextView;
        Button approveButton;
        Button rejectButton;

        RequestViewHolder(View itemView) {
            super(itemView);
            typeTextView = itemView.findViewById(R.id.request_type_text_view);
            studentTextView = itemView.findViewById(R.id.student_text_view);
            dateTextView = itemView.findViewById(R.id.date_text_view);
            messageTextView = itemView.findViewById(R.id.message_text_view);
            approveButton = itemView.findViewById(R.id.approve_button);
            rejectButton = itemView.findViewById(R.id.reject_button);
        }
    }
}