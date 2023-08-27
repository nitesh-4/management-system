package com.example.managementapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class TaskDetailActivity extends AppCompatActivity {

    private Task task;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private TextView tvTitle, tvDescription, tvStartDate, tvEndDate, tvProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        // Initialize Firestore and FirebaseAuth
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        tvTitle = findViewById(R.id.tvDetailTitle);
        tvDescription = findViewById(R.id.tvDetailDescription);
        tvStartDate = findViewById(R.id.tvDetailStartDate);
        tvEndDate = findViewById(R.id.tvDetailEndDate);
        tvProgress = findViewById(R.id.tvDetailProgress);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(TaskDetailActivity.this, ProjectTaskActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        Button deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(v -> {
            if (task != null && task.getId() != null && task.getProjectId() != null) {
                deleteTaskFromFirestore(task.getId(), task.getProjectId());
            } else {
                Toast.makeText(TaskDetailActivity.this, "Error fetching task data.", Toast.LENGTH_SHORT).show();
            }
        });

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

    private void deleteTaskFromFirestore(String taskId, String projectId) {
        String userId = auth.getCurrentUser().getUid();

        db.collection("users").document(userId)
                .collection("projects").document(projectId)
                .collection("tasks").document(taskId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(TaskDetailActivity.this, "Task deleted successfully.", Toast.LENGTH_SHORT).show();
                    // Navigate back to the ProjectTaskActivity after successful deletion
                    Intent intent = new Intent(TaskDetailActivity.this, ProjectTaskActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(TaskDetailActivity.this, "Error deleting task.", Toast.LENGTH_SHORT).show();
                });
    }
}
