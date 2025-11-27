package com.example.myapplication2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication2.api.ApiClient;
import com.example.myapplication2.ApiService;
import com.example.myapplication2.models.OrderItem;
import com.example.myapplication2.models.OrderResponse;
import com.example.myapplication2.models.OrderSummary;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.app.AlertDialog;

public class TrackOrderFragment extends Fragment {

    private RecyclerView ordersRecycler;
    private TrackOrderAdapter orderAdapter;
    private final List<OrderSummary> activeOrders = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track_order, container, false);

        ordersRecycler = view.findViewById(R.id.ordersRecycler);
        ordersRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        orderAdapter = new TrackOrderAdapter(activeOrders);
        ordersRecycler.setAdapter(orderAdapter);

        // Click listener to fetch order details
        orderAdapter.setOnOrderClickListener(orderId -> loadOrderDetails(orderId));

        fetchPendingOrders();

        return view;
    }

    private void fetchPendingOrders() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("loggedInUserId", -1);

        if (userId == -1) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getApiService();
        apiService.getUserOrders(userId).enqueue(new Callback<List<OrderSummary>>() {
            @Override
            public void onResponse(Call<List<OrderSummary>> call, Response<List<OrderSummary>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    activeOrders.clear();

                    for (OrderSummary order : response.body()) {
                        if (order.getStatus().equalsIgnoreCase("pending")
                                || order.getStatus().equalsIgnoreCase("ready")) {
                            activeOrders.add(order);
                        }
                    }

                    if (activeOrders.isEmpty()) {
                        Toast.makeText(getContext(), "You have no active orders", Toast.LENGTH_SHORT).show();
                    }

                    orderAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Failed to load orders", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<OrderSummary>> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadOrderDetails(int orderId) {
        ApiService apiService = ApiClient.getApiService();
        apiService.getOrderDetails(orderId).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OrderResponse order = response.body();
                    showOrderDialog(order);
                } else {
                    Toast.makeText(getContext(), "Failed to load order details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showOrderDialog(OrderResponse order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Order #" + order.getPickup_code());

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 24, 24, 24);

        for (OrderItem item : order.getItems()) {
            TextView tv = new TextView(getContext());
            tv.setText(item.getItem_name() + " x" + item.getQuantity() + " - ₱" + item.getSubtotal());
            tv.setTextSize(16);
            layout.addView(tv);
        }

        TextView total = new TextView(getContext());
        total.setText("\nTotal: ₱" + order.getTotal_amount());
        total.setTextSize(18);
        total.setPadding(0, 12, 0, 0);
        layout.addView(total);

        builder.setView(layout);
        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
