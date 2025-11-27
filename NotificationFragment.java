package com.example.myapplication2;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication2.api.ApiClient;
import com.example.myapplication2.ApiService;
import com.example.myapplication2.models.Notification;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private TextView emptyText;
    private TextView notificationBadge;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notifications, container, false);


        recyclerView = view.findViewById(R.id.notification_recycler);
        emptyText = view.findViewById(R.id.empty_text);
        notificationBadge = view.findViewById(R.id.notification_badge);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotificationAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setNotificationClickListener((notification, position) -> {
            markNotificationAsRead(notification, position);
        });

        fetchNotifications();

        return view;
    }

    private void fetchNotifications() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("loggedInUserId", 0);

        if (userId == 0) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getNotifications(userId).enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Notification> notifications = response.body();

                    if (!notifications.isEmpty()) {
                        adapter.setItems(notifications);
                        emptyText.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        adapter.setItems(Collections.emptyList());
                        emptyText.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }

                    updateNotificationBadge(notifications);

                } else {
                    adapter.setItems(Collections.emptyList());
                    emptyText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    updateNotificationBadge(Collections.emptyList());
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                t.printStackTrace();
                adapter.setItems(Collections.emptyList());
                emptyText.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                updateNotificationBadge(Collections.emptyList());
            }
        });
    }

    private void markNotificationAsRead(Notification notification, int position) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("loggedInUserId", 0);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.markNotificationAsRead(notification.id, userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    adapter.markAsRead(position);
                    updateNotificationBadge(adapter.getItems());
                } else {
                    Toast.makeText(getContext(), "Failed to mark as read", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), "Error marking as read", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateNotificationBadge(List<Notification> notifications) {
        boolean hasUnread = false;
        for (Notification n : notifications) {
            if (!n.isRead()) {
                hasUnread = true;
                break;
            }
        }

        if (notificationBadge != null) {
            notificationBadge.setVisibility(hasUnread ? View.VISIBLE : View.GONE);
        }
    }
}
