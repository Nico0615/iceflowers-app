package com.example.myapplication2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myapplication2.ApiService;
import com.example.myapplication2.api.ApiClient;
import com.example.myapplication2.models.Notification;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private boolean isNavigating = false;
    private int unreadNotifications = 0;

    private Handler handler = new Handler(Looper.getMainLooper());
    private final int POLL_INTERVAL = 5000; // 5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigation = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null) {
            loadHomeFragment();
        }

        bottomNavigation.setOnItemSelectedListener(item -> {
            if (isNavigating) return true;
            isNavigating = true;

            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                if (!(currentFragment instanceof HomeFragment)) selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_menu) {
                if (!(currentFragment instanceof MenuFragment)) selectedFragment = new MenuFragment();
            } else if (itemId == R.id.nav_cart) {
                if (!(currentFragment instanceof CartFragment)) selectedFragment = new CartFragment();
            } else if (itemId == R.id.nav_profile) {
                if (!(currentFragment instanceof SettingsFragment)) selectedFragment = new SettingsFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .runOnCommit(() -> isNavigating = false)
                        .commit();
            } else {
                isNavigating = false;
            }

            return true;
        });

        startNotificationPolling();
    }

    private void loadHomeFragment() {
        HomeFragment homeFragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt("unread_count", unreadNotifications);
        homeFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, homeFragment)
                .commit();
    }

    private void startNotificationPolling() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchNotifications();
                handler.postDelayed(this, POLL_INTERVAL);
            }
        }, POLL_INTERVAL);
    }

    private void fetchNotifications() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("loggedInUserId", 0);
        if (userId == 0) {
            Log.e("NOTIFICATIONS", "User ID not found, cannot fetch notifications.");
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<Notification>> call = apiService.getNotifications(userId);
        call.enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    unreadNotifications = 0;
                    for (Notification n : response.body()) {
                        if (!n.isRead()) unreadNotifications++;
                    }
                    Log.d("NOTIFICATIONS", "Unread notifications: " + unreadNotifications);

                    Fragment frag = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    if (frag instanceof HomeFragment) {
                        ((HomeFragment) frag).updateNotificationBadge(unreadNotifications);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                Log.e("NOTIFICATIONS", "Failed to fetch notifications", t);
            }
        });
    }
}
