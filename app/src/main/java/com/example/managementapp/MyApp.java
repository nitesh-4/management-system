package com.example.managementapp;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Firebase here
        FirebaseApp.initializeApp(this);
    }
}
