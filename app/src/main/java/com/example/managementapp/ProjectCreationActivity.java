package com.example.managementapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class ProjectCreationActivity extends AppCompatActivity {

    private EditText editProjectTitle, editProjectDescription, editStartDate, editEndDate;
    private Button btnCreateProject;
    private FirebaseFirestore db;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_creation);

        // Initialize Firestore and Firebase Auth
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        editProjectTitle = findViewById(R.id.editProjectTitle);
        editProjectDescription = findViewById(R.id.editProjectDescription);
        editStartDate = findViewById(R.id.editStartDate);
        editEndDate = findViewById(R.id.editEndDate);
        btnCreateProject = findViewById(R.id.btnCreateProject);
        ImageButton backButton = findViewById(R.id.back_button);

        btnCreateProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Here, save the project to Firestore and then return to MainActivity.
                saveProjectToFirestore();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProjectCreationActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
    }

    private void saveProjectToFirestore() {
        String title = editProjectTitle.getText().toString().trim();
        String description = editProjectDescription.getText().toString().trim();
        String startDate = editStartDate.getText().toString();
        String endDate = editEndDate.getText().toString();

        // Validation check
        if(title.isEmpty() || description.isEmpty()) {
            Toast.makeText(ProjectCreationActivity.this, "Enter project details!", Toast.LENGTH_SHORT).show();
            return;  // Stop further execution
        }

        Project project = new Project(title, description, startDate, endDate);

        if (user != null) {
            String userId = user.getUid();

            // Adding the project to the user's specific sub-collection
            db.collection("users").document(userId).collection("projects")
                    .add(project)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(ProjectCreationActivity.this, "Project added successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ProjectCreationActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Error writing document", e);
                            Toast.makeText(ProjectCreationActivity.this, "Failed to add project", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(ProjectCreationActivity.this, "User not authenticated.", Toast.LENGTH_SHORT).show();
        }
    }
}
