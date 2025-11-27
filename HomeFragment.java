package com.example.myapplication2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication2.api.SizeOption;
import com.example.myapplication2.api.ApiClient;
import com.example.myapplication2.ApiService;
import com.example.myapplication2.api.MenuItem;
import com.example.myapplication2.api.MenuResponse;
import com.example.myapplication2.models.Notification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private TextView greetingText;
    private RecyclerView featuredRecycler;
    private FeaturedAdapter featuredAdapter;
    private ImageButton notificationBell;
    private TextView notificationBadge;

    private Button viewMenuButton, btnFoods, btnDrinks, btnPastries, trackOrderButton;

    private int unreadCount = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        greetingText = view.findViewById(R.id.greeting_text);
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String firstName = prefs.getString("first_name", "");
        greetingText.setText("Hello, " + firstName + "\nWhat would you like today?");

        featuredRecycler = view.findViewById(R.id.featuredRecycler);
        featuredRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        featuredAdapter = new FeaturedAdapter(this::onFeaturedItemClicked);
        featuredRecycler.setAdapter(featuredAdapter);
        loadFeaturedItems();

        viewMenuButton = view.findViewById(R.id.view_menu_button);
        btnFoods = view.findViewById(R.id.foods_button);
        btnDrinks = view.findViewById(R.id.drinks_button);
        btnPastries = view.findViewById(R.id.pastries_button);
        trackOrderButton = view.findViewById(R.id.track_order_button);

        notificationBell = view.findViewById(R.id.notification_bell);
        notificationBadge = view.findViewById(R.id.notification_badge);

        if (getArguments() != null) {
            unreadCount = getArguments().getInt("unread_count", 0);
            updateNotificationBadge(unreadCount);
        }

        notificationBell.setOnClickListener(v -> {
            NotificationFragment nf = new NotificationFragment();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, nf)
                    .addToBackStack(null)
                    .commit();

            setUnreadCount(0);
        });

        viewMenuButton.setOnClickListener(v -> navigateToMenu("all"));
        btnFoods.setOnClickListener(v -> navigateToMenu("Food"));
        btnDrinks.setOnClickListener(v -> navigateToMenu("Drinks"));
        btnPastries.setOnClickListener(v -> navigateToMenu("Pastry"));
        trackOrderButton.setOnClickListener(v -> {
            TrackOrderFragment trackOrderFragment = new TrackOrderFragment();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, trackOrderFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    public void updateNotificationBadge(int count) {
        if (notificationBadge == null) return;
        if (count > 0) {
            String display = count > 99 ? "99+" : String.valueOf(count);
            notificationBadge.setText(display);
            notificationBadge.setVisibility(View.VISIBLE);
        } else {
            notificationBadge.setVisibility(View.GONE);
        }
    }

    public void setUnreadCount(int count) {
        unreadCount = count;
        updateNotificationBadge(unreadCount);
    }

    private void navigateToMenu(String category) {
        MenuFragment menuFragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putString("category", category);
        menuFragment.setArguments(args);

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, menuFragment)
                .addToBackStack(null)
                .commit();
    }

    private void onFeaturedItemClicked(MenuItem item) {
        List<SizeOption> sizes = new ArrayList<>();
        SizeOption defaultSize = new SizeOption();
        defaultSize.item_id = item.id;
        defaultSize.size = "Regular";
        defaultSize.price = item.price;
        sizes.add(defaultSize);

        MenuItemDialogFragment dialog = MenuItemDialogFragment.newInstance(
                item.id,
                item.name,
                item.image_url,
                sizes
        );
        dialog.show(getParentFragmentManager(), "MenuItemDialog");
    }

    private void loadFeaturedItems() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getFeatured().enqueue(new Callback<MenuResponse>() {
            @Override
            public void onResponse(Call<MenuResponse> call, Response<MenuResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    List<MenuItem> featuredItems = response.body().menu;
                    featuredAdapter.setItems(featuredItems != null ? featuredItems : Collections.emptyList());
                } else {
                    featuredAdapter.setItems(Collections.emptyList());
                }
            }

            @Override
            public void onFailure(Call<MenuResponse> call, Throwable t) {
                t.printStackTrace();
                featuredAdapter.setItems(Collections.emptyList());
            }
        });
    }
}
