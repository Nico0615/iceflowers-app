package com.example.myapplication2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    Button viewMenuButton, loginButton, registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ---------- 1️⃣ Check if user is already logged in ----------
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        int loggedInUserId = prefs.getInt("loggedInUserId", -1);

        if (loggedInUserId != -1) {
            // User already logged in → go straight to HomeActivity
            Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // Prevent WelcomeActivity from showing
            return; // Stop further execution
        }

        // ---------- 2️⃣ Show welcome screen for guests ----------
        setContentView(R.layout.activity_welcome);

        viewMenuButton = findViewById(R.id.view_menu_button);
        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);

        // Guest access → go to GuestMenuActivity
        viewMenuButton.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, GuestMenuActivity.class);
            startActivity(intent);
        });

        // Go to login screen (MainActivity)
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // Go to register screen
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
