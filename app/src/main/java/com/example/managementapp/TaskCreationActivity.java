package com.example.managementapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class TaskCreationActivity extends AppCompatActivity {

    EditText etTaskTitle, etTaskDescription, etStartDate, etEndDate;
    SeekBar seekBarProgress;
    TextView tvProgressPercentage;
    Button btnCreateTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_creation);

        etTaskTitle = findViewById(R.id.etTaskTitle);
        etTaskDescription = findViewById(R.id.etTaskDescription);
        etStartDate = findViewById(R.id.etStartDate);
        etEndDate = findViewById(R.id.etEndDate);
        seekBarProgress = findViewById(R.id.seekBarProgress);
        tvProgressPercentage = findViewById(R.id.tvProgressPercentage);
        btnCreateTask = findViewById(R.id.btnCreateTask);

        seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvProgressPercentage.setText(progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnCreateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get values from EditTexts and SeekBar
                String title = etTaskTitle.getText().toString().trim();
                String description = etTaskDescription.getText().toString().trim();
                String startDate = etStartDate.getText().toString().trim();
                String endDate = etEndDate.getText().toString().trim();
                int progress = seekBarProgress.getProgress();
                String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                if (title.isEmpty()) {
                    Toast.makeText(TaskCreationActivity.this, "Please fill out the task title.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> task = new HashMap<>();
                task.put("title", title);
                task.put("description", description);
                task.put("startDate", startDate);
                task.put("endDate", endDate);
                task.put("progress", progress);

                // Get Firestore instance and store data
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users").document(currentUserId).collection("projects").document(getIntent().getStringExtra("projectId")).collection("tasks")
                        .add(task)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(TaskCreationActivity.this, "Task successfully added.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(TaskCreationActivity.this, ProjectTaskActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(TaskCreationActivity.this, "Error adding task.", Toast.LENGTH_SHORT).show();
                        });
            }
        });


        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(TaskCreationActivity.this, ProjectTaskActivity.class);
            startActivity(intent);
            finish(); // to close the current activity
        });
    }
}
