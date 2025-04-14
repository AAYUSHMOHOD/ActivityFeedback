package com.example.ad_pbl_activityfeedback_q5.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ad_pbl_activityfeedback_q5.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnalyticsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_TEXT = 0;
    private static final int TYPE_MULTIPLE_CHOICE = 1;
    private static final int TYPE_RATING = 2;

    private final Context context;
    private final List<Map<String, Object>> questionsData;

    public AnalyticsAdapter(Context context, List<Map<String, Object>> questionsData) {
        this.context = context;
        this.questionsData = questionsData;
    }

    @Override
    public int getItemViewType(int position) {
        String questionType = (String) questionsData.get(position).get("questionType");
        switch (questionType) {
            case "multiple_choice":
                return TYPE_MULTIPLE_CHOICE;
            case "rating":
                return TYPE_RATING;
            default:
                return TYPE_TEXT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType) {
            case TYPE_MULTIPLE_CHOICE:
                View mcView = inflater.inflate(R.layout.item_chart_analytics, parent, false);
                return new ChartViewHolder(mcView);

            case TYPE_RATING:
                View ratingView = inflater.inflate(R.layout.item_chart_analytics, parent, false);
                return new ChartViewHolder(ratingView);

            default: // TYPE_TEXT
                View textView = inflater.inflate(R.layout.item_text_analytics, parent, false);
                return new TextViewHolder(textView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Map<String, Object> questionData = questionsData.get(position);
        String questionText = (String) questionData.get("questionText");
        String questionType = (String) questionData.get("questionType");

        switch (holder.getItemViewType()) {
            case TYPE_MULTIPLE_CHOICE:
                ChartViewHolder mcHolder = (ChartViewHolder) holder;
                mcHolder.questionTextView.setText(questionText);
                setupPieChart(mcHolder, questionData);
                break;

            case TYPE_RATING:
                ChartViewHolder ratingHolder = (ChartViewHolder) holder;
                ratingHolder.questionTextView.setText(questionText);
                setupBarChart(ratingHolder, questionData);
                break;

            default: // TYPE_TEXT
                TextViewHolder textHolder = (TextViewHolder) holder;
                textHolder.questionTextView.setText(questionText);
                setupTextResponses(textHolder, questionData);
                break;
        }
    }

    private void setupPieChart(ChartViewHolder holder, Map<String, Object> questionData) {
        Map<String, Integer> optionCounts = (Map<String, Integer>) questionData.get("optionCounts");
        if (optionCounts == null) {
            holder.pieChart.setVisibility(View.GONE);
            return;
        }

        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : optionCounts.entrySet()) {
            if (entry.getValue() > 0) { // Only add non-zero entries
                entries.add(new PieEntry(entry.getValue(), entry.getKey()));
            }
        }

        if (entries.isEmpty()) {
            holder.pieChart.setVisibility(View.GONE);
            return;
        }

        PieDataSet dataSet = new PieDataSet(entries, "Options");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData data = new PieData(dataSet);

        holder.pieChart.setVisibility(View.VISIBLE);
        holder.barChart.setVisibility(View.GONE);

        holder.pieChart.setData(data);
        holder.pieChart.getDescription().setEnabled(false);
        holder.pieChart.setCenterText("Responses");
        holder.pieChart.setEntryLabelColor(Color.BLACK);
        holder.pieChart.setEntryLabelTextSize(11f);
        holder.pieChart.setHoleRadius(40f);
        holder.pieChart.setTransparentCircleRadius(45f);
        holder.pieChart.setUsePercentValues(true);
        holder.pieChart.setDrawEntryLabels(false);
        holder.pieChart.getLegend().setEnabled(true);
        holder.pieChart.getLegend().setTextSize(12f);
        holder.pieChart.getLegend().setWordWrapEnabled(true);
        holder.pieChart.animate();
        holder.pieChart.invalidate();
    }

    private void setupBarChart(ChartViewHolder holder, Map<String, Object> questionData) {
        Map<String, Integer> ratingCounts = (Map<String, Integer>) questionData.get("ratingCounts");
        Number averageValue = (Number) questionData.get("average");

        if (ratingCounts == null) {
            holder.barChart.setVisibility(View.GONE);
            return;
        }

        double average = averageValue != null ? averageValue.doubleValue() : 0;

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;

        // Sort the entries by rating value
        Integer[] ratings = new Integer[ratingCounts.size()];
        int i = 0;
        for (String key : ratingCounts.keySet()) {
            try {
                ratings[i++] = Integer.parseInt(key);
            } catch (NumberFormatException e) {
                // Ignore invalid ratings
            }
        }
        // Sort the ratings
        java.util.Arrays.sort(ratings);

        // Create entries in sorted order
        for (Integer rating : ratings) {
            int count = ratingCounts.get(rating.toString());
            entries.add(new BarEntry(index, count));
            labels.add(rating.toString());
            index++;
        }

        if (entries.isEmpty()) {
            holder.barChart.setVisibility(View.GONE);
            return;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Ratings");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.7f);

        holder.barChart.setVisibility(View.VISIBLE);
        holder.pieChart.setVisibility(View.GONE);

        // Setup X axis with labels
        XAxis xAxis = holder.barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        holder.barChart.getAxisRight().setEnabled(false); // Hide right axis
        holder.barChart.setDrawValueAboveBar(true);
        holder.barChart.setData(data);
        holder.barChart.getDescription().setText("Average: " + String.format("%.1f", average));
        holder.barChart.getDescription().setTextSize(12f);
        holder.barChart.setFitBars(true);
        holder.barChart.animateY(1000);
        holder.barChart.invalidate();
    }

    private void setupTextResponses(TextViewHolder holder, Map<String, Object> questionData) {
        List<String> responses = (List<String>) questionData.get("responses");

        StringBuilder responseText = new StringBuilder();
        if (responses == null || responses.isEmpty()) {
            responseText.append("No responses yet");
        } else {
            // Add count at the top
            responseText.append("Total Responses: ").append(responses.size()).append("\n\n");

            // List all responses
            for (int i = 0; i < responses.size(); i++) {
                responseText.append(i + 1).append(". ").append(responses.get(i));
                if (i < responses.size() - 1) {
                    responseText.append("\n\n");
                }
            }
        }

        holder.responsesTextView.setText(responseText.toString());
    }

    @Override
    public int getItemCount() {
        return questionsData.size();
    }

    static class TextViewHolder extends RecyclerView.ViewHolder {
        TextView questionTextView;
        TextView responsesTextView;

        TextViewHolder(View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.question_text_view);
            responsesTextView = itemView.findViewById(R.id.responses_text_view);
        }
    }

    static class ChartViewHolder extends RecyclerView.ViewHolder {
        TextView questionTextView;
        PieChart pieChart;
        BarChart barChart;

        ChartViewHolder(View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.question_text_view);
            pieChart = itemView.findViewById(R.id.pie_chart);
            barChart = itemView.findViewById(R.id.bar_chart);
        }
    }
}