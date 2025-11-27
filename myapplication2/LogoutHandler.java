package com.example.myapplication2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

public class LogoutHandler {

    public static void logout(Context context) {
        // Clear SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

        // Optional: show a toast
        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Redirect to WelcomeActivity
        Intent intent = new Intent(context, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
