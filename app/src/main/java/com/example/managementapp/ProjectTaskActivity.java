package com.example.managementapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import androidx.recyclerview.widget.LinearLayoutManager;


import java.util.ArrayList;
import java.util.List;

public class ProjectTaskActivity extends AppCompatActivity {
     TextView projectTitle, projectDescription, projectDates;
    TextView noTasksTextView;
     RecyclerView tasksRecyclerView;
     FloatingActionButton createTaskButton;
     Button deleteProjectButton, optionsButton;
    FirebaseAuth auth = FirebaseAuth.getInstance();  // Initialize auth here
     Project selectedProject;
     FirebaseFirestore db = FirebaseFirestore.getInstance();
     TaskAdapter taskAdapter;

    List<Task> tasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_task);

        if (getIntent().hasExtra("selected_project")) {
            selectedProject = (Project) getIntent().getSerializableExtra("selected_project");
            if (selectedProject.getId() == null) {
                Toast.makeText(this, "Project ID not found!", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        } else {
            Toast.makeText(this, "No project data!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        noTasksTextView = findViewById(R.id.noTasksTextView);
        projectTitle = findViewById(R.id.projectTitle);
        projectDescription = findViewById(R.id.projectDescription);
        projectDates = findViewById(R.id.projectDates);
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(this,tasks);
        tasksRecyclerView.setAdapter(taskAdapter);


        deleteProjectButton = findViewById(R.id.deleteProjectButton);
        optionsButton = findViewById(R.id.optionsButton);
        ImageButton backButton = findViewById(R.id.back_button);

        projectTitle.setText(selectedProject.getTitle());
        projectDescription.setText(selectedProject.getDescription());
        projectDates.setText(selectedProject.getStartDate() + " - " + selectedProject.getEndDate());

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProjectTaskActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        deleteProjectButton.setOnClickListener(v -> deleteProjectFromFirestore());

        createTaskButton = findViewById(R.id.createTaskButton);
        createTaskButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProjectTaskActivity.this, TaskCreationActivity.class);
            intent.putExtra("projectId", selectedProject.getId());
            intent.putExtra("selected_project", selectedProject);  // Pass the entire project
            startActivity(intent);
        });



        optionsButton.setOnClickListener(this::showPopupMenu);
        db = FirebaseFirestore.getInstance();
        fetchTasksForProject();


    }

    // ... The start of your ProjectTaskActivity code remains the same ...

    private void fetchTasksForProject() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(ProjectTaskActivity.this, "User not authenticated!", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUserId = currentUser.getUid();

        db.collection("users")
                .document(currentUserId)
                .collection("projects")
                .document(selectedProject.getId())
                .collection("tasks")
                .get()
                .addOnCompleteListener(taskResult -> {
                    if (taskResult.isSuccessful()) {
                        List<Task> tasksList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : taskResult.getResult()) {
                            Task taskItem = document.toObject(Task.class);
                            tasksList.add(taskItem);
                        }

                        if(taskAdapter == null) {
                            taskAdapter = new TaskAdapter(this, tasksList);
                            tasksRecyclerView.setAdapter(taskAdapter);
                        } else {
                            taskAdapter.updateData(tasksList);
                            taskAdapter.notifyDataSetChanged();
                        }

                        // Hide or show the noTasksTextView based on the tasks list size
                        if (tasksList.isEmpty()) {
                            noTasksTextView.setVisibility(View.VISIBLE);
                        } else {
                            noTasksTextView.setVisibility(View.GONE);
                        }

                        Log.d("DEBUG", "Adapter set with tasks: " + tasksList.size());
                    } else {
                        Toast.makeText(ProjectTaskActivity.this, "Error fetching tasks.", Toast.LENGTH_SHORT).show();
                        Log.e("DEBUG", "Error fetching tasks: ", taskResult.getException());
                    }
                });
    }



    // ... The rest of your ProjectTaskActivity code remains the same ...


    private void deleteProjectFromFirestore() {
        if (auth == null || auth.getCurrentUser() == null) return;

        String currentUserId = auth.getCurrentUser().getUid();

        if (selectedProject == null || selectedProject.getId() == null || selectedProject.getId().isEmpty()) {
            Toast.makeText(ProjectTaskActivity.this, "Invalid project data", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users").document(currentUserId).collection("projects").document(selectedProject.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ProjectTaskActivity.this, "Project deleted successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProjectTaskActivity.this, MainActivity.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> Toast.makeText(ProjectTaskActivity.this, "Error deleting project", Toast.LENGTH_SHORT).show());
    }

    private void showPopupMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenuInflater().inflate(R.menu.options_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_pending) {
                // Handle "Pending" action
                return true;
            } else if (item.getItemId() == R.id.menu_completed) {
                // Handle "Completed" action
                return true;
            }
            return false;
        });

        popup.show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        fetchTasksForProject();
    }

}
