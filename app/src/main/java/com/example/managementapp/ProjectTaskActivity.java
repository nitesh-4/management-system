package com.example.managementapp;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.firestore.FirebaseFirestore;

public class ProjectTaskActivity extends AppCompatActivity {
    private TextView projectTitle, projectDescription, projectDates;
    private RecyclerView tasksRecyclerView;
    private FloatingActionButton createTaskButton;
    private Button deleteProjectButton, optionsButton;
    FirebaseAuth auth = FirebaseAuth.getInstance();  // Initialize auth here
    private Project selectedProject;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        projectTitle = findViewById(R.id.projectTitle);
        projectDescription = findViewById(R.id.projectDescription);
        projectDates = findViewById(R.id.projectDates);
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);

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
            startActivity(intent);
        });

        optionsButton.setOnClickListener(this::showPopupMenu);

        fetchTasksForProject();
    }

    private void fetchTasksForProject() {
        // You can use the `tasksRecyclerView` to display the tasks here.
        // Fetch tasks associated with the selectedProject and update the tasksRecyclerView using an adapter.
        // Make sure to fetch the tasks from the user's sub-collection.
    }

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
}
