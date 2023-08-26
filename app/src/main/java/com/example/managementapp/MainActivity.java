package com.example.managementapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.bumptech.glide.Glide;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    TextView textView;
    TextView emptyProjectsText; // This is the "CREATE NEW PROJECTS" TextView
    RecyclerView projectsRecyclerView;

    FirebaseUser user;
    ProjectAdapter projectAdapter;
    List<Project> projects = new ArrayList<>();

    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.welcomeText);
        emptyProjectsText = findViewById(R.id.emptyProjectsText); // Assume you added this TextView to your XML
        projectsRecyclerView = findViewById(R.id.projectsRecyclerView);




        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            String welcomeMessage = "Welcome, " + user.getDisplayName();
            textView.setText(welcomeMessage);
        }

        ImageButton profileImageButton = findViewById(R.id.profileImageButton);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        StorageReference userImageRef = storageReference.child("userImages/" + user.getUid() + ".jpg");

        userImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(MainActivity.this)
                        .load(uri.toString())
                        .circleCrop()
                        .into(profileImageButton);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
        profileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton fabAddProject = findViewById(R.id.fab_add_project);
        fabAddProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProjectCreationActivity.class);
                startActivity(intent);
            }
        });


        projectsRecyclerView = findViewById(R.id.projectsRecyclerView);
        projectsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        projectAdapter = new ProjectAdapter(this, projects);
        projectsRecyclerView.setAdapter(projectAdapter);

        db = FirebaseFirestore.getInstance();
        fetchProjectsFromFirestore();

    }


    @Override
    protected void onResume() {
        super.onResume();
        fetchProjectsFromFirestore();
    }




    private void fetchProjectsFromFirestore() {
        if(user == null) return; // Safety check

        String currentUserId = user.getUid();  // Get the user ID of the authenticated user

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Change the query to get the projects related to the specific user
        db.collection("users").document(currentUserId).collection("projects")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            projects.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Project project = document.toObject(Project.class);
                                project.setId(document.getId());
                                projects.add(project);
                            }
                            projectAdapter.notifyDataSetChanged();

                            // Handling for empty projects
                            if(projects.size() == 0) {
                                emptyProjectsText.setVisibility(View.VISIBLE);
                                projectsRecyclerView.setVisibility(View.GONE);
                            } else {
                                emptyProjectsText.setVisibility(View.GONE);
                                projectsRecyclerView.setVisibility(View.VISIBLE);
                            }
                        } else {
                            // Handle the error...
                        }
                    }
                });
    }

}