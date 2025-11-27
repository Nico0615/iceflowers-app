package com.example.myapplication2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.myapplication2.api.ApiClient;
import com.example.myapplication2.ApiService;
import com.example.myapplication2.api.MenuItem;
import com.example.myapplication2.api.MenuResponse;
import com.example.myapplication2.api.SizeOption;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuFragment extends Fragment {

    private LinearLayout menuList;
    private ProgressBar loadingSpinner;
    private List<MenuItem> currentItems = new ArrayList<>();
    private TextView btnAll, btnFoods, btnPastries, btnDrinks;
    private EditText searchBar;

    private String currentCategory = "all";
    private String searchQuery = "";
    private int currentPage = 1;
    private int totalPages = 1;
    private static final int ITEMS_PER_PAGE = 7;

    // --- Prevent overlapping fetches ---
    private boolean isFetching = false;

    // --- Guest flag ---
    private boolean isGuest = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_menu, container, false);

        // Initialize UI elements
        btnAll = root.findViewById(R.id.filter_all);
        btnFoods = root.findViewById(R.id.filter_foods);
        btnPastries = root.findViewById(R.id.filter_pastries);
        btnDrinks = root.findViewById(R.id.filter_drinks);
        searchBar = root.findViewById(R.id.search_bar);
        menuList = root.findViewById(R.id.menuList);
        loadingSpinner = root.findViewById(R.id.loadingSpinner);

        // Handle arguments (search or category)
        if (getArguments() != null) {
            searchQuery = getArguments().getString("search_query", "");
            currentCategory = getArguments().getString("category", "all");
            isGuest = getArguments().getBoolean("isGuest", false); // 👈 detect guest flag
        }
        if (!searchQuery.isEmpty()) searchBar.setText(searchQuery);

        // Search listener
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString().trim();
                currentPage = 1;
                fetchMenu(currentPage);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Filter button logic
        btnAll.setOnClickListener(v -> switchCategory("all"));
        btnFoods.setOnClickListener(v -> switchCategory("Food"));
        btnPastries.setOnClickListener(v -> switchCategory("Pastry"));
        btnDrinks.setOnClickListener(v -> switchCategory("Drinks"));

        fetchMenu(currentPage);
        return root;
    }

    private void switchCategory(String category) {
        currentCategory = category;
        currentPage = 1;
        fetchMenu(currentPage);
    }

    private void fetchMenu(int page) {
        if (isFetching) return;
        isFetching = true;

        if (!isAdded() || getContext() == null) return;

        loadingSpinner.setVisibility(View.VISIBLE);
        menuList.removeAllViews();

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getMenuGrouped(currentPage, currentCategory, searchQuery).enqueue(new Callback<MenuResponse>() {
            @Override
            public void onResponse(Call<MenuResponse> call, Response<MenuResponse> response) {
                if (!isAdded() || getContext() == null) return; // ✅ fragment detached safety

                isFetching = false;
                loadingSpinner.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    currentItems = response.body().menu != null ? response.body().menu : new ArrayList<>();
                    totalPages = response.body().total_pages;
                    currentPage = response.body().current_page;
                    displayMenu();
                } else {
                    showNoItems();
                }
            }

            @Override
            public void onFailure(Call<MenuResponse> call, Throwable t) {
                if (!isAdded() || getContext() == null) return; // ✅ prevent NPE

                isFetching = false;
                loadingSpinner.setVisibility(View.GONE);
                t.printStackTrace();
                showNoItems();
            }
        });
    }

    private void showNoItems() {
        if (!isAdded() || getContext() == null) return;
        menuList.removeAllViews();
        TextView noItems = new TextView(requireContext());
        noItems.setText("No items found.");
        noItems.setTextSize(16);
        noItems.setTextColor(0xFF222222);
        noItems.setGravity(Gravity.CENTER);
        noItems.setPadding(0, 20, 0, 20);
        menuList.addView(noItems);
    }

    private void displayMenu() {
        if (!isAdded() || getContext() == null) return; // ✅ safety check

        menuList.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(requireContext());

        if (currentItems.isEmpty()) {
            showNoItems();
            return;
        }

        // Filter items by category and search
        List<MenuItem> filteredItems = new ArrayList<>();
        String lowerQuery = searchQuery.toLowerCase();
        for (MenuItem item : currentItems) {
            boolean matchesCategory = currentCategory.equalsIgnoreCase("all") ||
                    item.category.equalsIgnoreCase(currentCategory);
            boolean matchesSearch = lowerQuery.isEmpty() || item.name.toLowerCase().contains(lowerQuery);
            if (matchesCategory && matchesSearch) filteredItems.add(item);
        }

        // Display items
        for (MenuItem item : filteredItems) {
            View itemView = inflater.inflate(R.layout.menu_item_view, menuList, false);
            TextView name = itemView.findViewById(R.id.itemName);
            TextView price = itemView.findViewById(R.id.itemPrice);
            ImageView image = itemView.findViewById(R.id.itemImage);

            name.setText(item.name);
            if (item.sizes != null && !item.sizes.isEmpty()) {
                if (item.sizes.size() > 1) {
                    double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
                    for (SizeOption s : item.sizes) {
                        if (s.price < min) min = s.price;
                        if (s.price > max) max = s.price;
                    }
                    price.setText("₱" + String.format("%.2f", min) + " - ₱" + String.format("%.2f", max));
                } else {
                    price.setText("₱" + String.format("%.2f", item.sizes.get(0).price));
                }
            }

            Glide.with(requireContext())
                    .load(item.image_url)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(image);

            itemView.setOnClickListener(v -> {
                if (isGuest) {
                    showGuestDialog(requireContext());
                } else {
                    MenuItemDialogFragment dialog = MenuItemDialogFragment
                            .newInstance(item.id, item.name, item.image_url, item.sizes);
                    dialog.show(getParentFragmentManager(), "menuItemDialog");
                }
            });

            menuList.addView(itemView);
        }

        addCompactPagination(inflater);
        updateFilterUI();
    }

    private void addCompactPagination(LayoutInflater inflater) {
        if (totalPages <= 1) return;
        if (!isAdded() || getContext() == null) return;

        View paginationView = inflater.inflate(R.layout.pagination_layout, menuList, false);
        LinearLayout paginationContainer = paginationView.findViewById(R.id.pagination_container);

        // prev button
        Button prev = createPageButton("<", false);
        prev.setEnabled(currentPage > 1);
        prev.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                fetchMenu(currentPage);
            }
        });
        paginationContainer.addView(prev);

        // page numbers
        for (int i = 1; i <= totalPages; i++) {
            final int page = i;
            Button btn = createPageButton(String.valueOf(i), i == currentPage);
            btn.setOnClickListener(v -> {
                currentPage = page;
                fetchMenu(currentPage);
            });
            paginationContainer.addView(btn);
        }

        // next button
        Button next = createPageButton(">", false);
        next.setEnabled(currentPage < totalPages);
        next.setOnClickListener(v -> {
            if (currentPage < totalPages) {
                currentPage++;
                fetchMenu(currentPage);
            }
        });
        paginationContainer.addView(next);

        menuList.addView(paginationView);
    }

    private Button createPageButton(String text, boolean isActive) {
        Button btn = new Button(requireContext());
        btn.setText(text);
        btn.setAllCaps(false);
        btn.setTextSize(16f);
        btn.setIncludeFontPadding(false);
        btn.setMinWidth(0);
        btn.setMinHeight(0);
        btn.setMinimumWidth(0);
        btn.setMinimumHeight(0);
        btn.setPadding(20, 14, 20, 14);

        btn.setBackgroundResource(R.drawable.pagination_button_bg);
        btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                isActive ? 0xFF064C3B : 0xFFF2F2F2
        ));
        btn.setTextColor(isActive ? 0xFFFFFFFF : 0xFF222222);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(10, 0, 10, 0);
        btn.setLayoutParams(params);

        return btn;
    }

    private void updateFilterUI() {
        if (!isAdded()) return;
        btnAll.setAlpha(currentCategory.equalsIgnoreCase("all") ? 1f : 0.5f);
        btnFoods.setAlpha(currentCategory.equalsIgnoreCase("Food") ? 1f : 0.5f);
        btnPastries.setAlpha(currentCategory.equalsIgnoreCase("Pastry") ? 1f : 0.5f);
        btnDrinks.setAlpha(currentCategory.equalsIgnoreCase("Drinks") ? 1f : 0.5f);
    }

    // --- Guest Popup ---
    private void showGuestDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Guest Access")
                .setMessage("You do not have an account. Please register or login to make an order.")
                .setPositiveButton("Register", (dialog, which) -> {
                    startActivity(new Intent(context, RegisterActivity.class));
                })
                .setNegativeButton("Login", (dialog, which) -> {
                    startActivity(new Intent(context, MainActivity.class));
                })
                .setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
