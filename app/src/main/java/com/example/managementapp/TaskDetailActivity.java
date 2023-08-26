package com.example.managementapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TaskDetailActivity extends AppCompatActivity {

    private Task task;

    private TextView tvTitle, tvDescription, tvStartDate, tvEndDate, tvProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        tvTitle = findViewById(R.id.tvDetailTitle);
        tvDescription = findViewById(R.id.tvDetailDescription);
        tvStartDate = findViewById(R.id.tvDetailStartDate);
        tvEndDate = findViewById(R.id.tvDetailEndDate);
        tvProgress = findViewById(R.id.tvDetailProgress);

        Intent intent = getIntent();
        if (intent.hasExtra("selected_task")) {
            task = (Task) intent.getSerializableExtra("selected_task");

            // Display the task's details
            tvTitle.setText(task.getTitle());
            tvDescription.setText(task.getDescription());
            tvStartDate.setText("Start Date: " + task.getStartDate());
            tvEndDate.setText("End Date: " + task.getEndDate());
            tvProgress.setText("Progress: " + task.getProgress() + "%");
        }
    }
}
