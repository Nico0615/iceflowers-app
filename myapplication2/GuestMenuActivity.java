package com.example.myapplication2;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import android.widget.Button;

public class GuestMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_menu);

        Button backButton = findViewById(R.id.back_to_welcome_button);
        backButton.setOnClickListener(v -> {
            finish(); // returns to WelcomeActivity
        });


        // Load MenuFragment as usual
        MenuFragment menuFragment = new MenuFragment();

        // Pass info to the fragment that this is a guest
        Bundle args = new Bundle();
        args.putBoolean("isGuest", true);
        menuFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, menuFragment);
        transaction.commit();
    }
}
