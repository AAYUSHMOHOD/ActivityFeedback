package com.example.ad_pbl_activityfeedback_q5.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ad_pbl_activityfeedback_q5.R;
import com.example.ad_pbl_activityfeedback_q5.models.Question;

import java.util.List;
import java.util.Map;

public class ViewSubmissionAdapter extends RecyclerView.Adapter<ViewSubmissionAdapter.AnswerViewHolder> {

    private final Context context;
    private final List<Map<String, Object>> answers;

    public ViewSubmissionAdapter(Context context, List<Map<String, Object>> answers) {
        this.context = context;
        this.answers = answers;
    }

    @NonNull
    @Override
    public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view_answer, parent, false);
        return new AnswerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerViewHolder holder, int position) {
        Map<String, Object> item = answers.get(position);
        Question question = (Question) item.get("question");
        Object answer = item.get("answer");

        // Display question
        holder.questionTextView.setText(question.getText());

        // Display answer based on question type
        String answerText;

        if (answer == null) {
            answerText = "No answer provided";
        } else {
            switch (question.getType()) {
                case "text":
                    answerText = answer.toString();
                    break;
                case "multiple_choice":
                    answerText = "Selected: " + answer.toString();
                    break;
                case "rating":
                    try {
                        int rating;
                        if (answer instanceof Long) {
                            rating = ((Long) answer).intValue();
                        } else if (answer instanceof Integer) {
                            rating = (Integer) answer;
                        } else {
                            rating = Integer.parseInt(answer.toString());
                        }
                        answerText = "Rating: " + rating + " out of " + question.getMax();
                    } catch (NumberFormatException e) {
                        answerText = "Invalid rating";
                    }
                    break;
                default:
                    answerText = answer.toString();
                    break;
            }
        }

        holder.answerTextView.setText(answerText);
    }

    @Override
    public int getItemCount() {
        return answers.size();
    }

    static class AnswerViewHolder extends RecyclerView.ViewHolder {
        TextView questionTextView;
        TextView answerTextView;

        AnswerViewHolder(View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.view_question_text_view);
            answerTextView = itemView.findViewById(R.id.view_answer_text_view);
        }
    }
}