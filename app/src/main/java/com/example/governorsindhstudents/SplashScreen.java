package com.example.governorsindhstudents;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spashcreen);

        // Delay to simulate splash screen
        new Handler().postDelayed(() -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            Intent intent;

            if (currentUser != null) {
                // User is logged in, go to HomeActivity
                intent = new Intent(SplashScreen.this, HomeActivity.class);
            } else {
                // User is not logged in, go to MainActivity
                intent = new Intent(SplashScreen.this, MainActivity.class);
            }
            startActivity(intent);
            finish(); // Close SplashScreen
        }, 1000); // 1-second delay
    }
}
