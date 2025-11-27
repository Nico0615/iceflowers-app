package com.example.myapplication2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication2.api.ApiClient;
import com.example.myapplication2.ApiService;
import com.example.myapplication2.models.OrderResponse;
import com.example.myapplication2.models.CartItemResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private Button checkoutButton;
    private CartManager cartManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        cartRecyclerView = view.findViewById(R.id.cartRecyclerView);
        checkoutButton = view.findViewById(R.id.checkoutButton);

        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("loggedInUserId", -1);

        if (userId == -1) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return view;
        }

        cartManager = CartManager.getInstance(requireContext());
        cartManager.setUser(userId);

        loadLocalCart();

        checkoutButton.setOnClickListener(v -> {
            double total = cartManager.getTotalPrice();
            if (total > 0) {
                placeOrder(total, userId);
            } else {
                Toast.makeText(getContext(), "Cart is empty", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void loadLocalCart() {
        cartAdapter = new CartAdapter(getContext(), cartManager.getCartItems());
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartRecyclerView.setAdapter(cartAdapter);
    }

    private void placeOrder(double totalAmount, int userId) {
        JSONArray itemsArray = new JSONArray();
        try {
            for (CartItem item : cartManager.getCartItems()) {
                JSONObject obj = new JSONObject();
                obj.put("product_id", item.getItemId());
                obj.put("quantity", item.getQuantity());
                obj.put("subtotal", item.getQuantity() * item.getPrice());
                itemsArray.put(obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String itemsJson = itemsArray.toString();

        ApiService apiService = ApiClient.getApiService();
        Call<OrderResponse> call = apiService.checkout(userId, totalAmount, itemsJson);

        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OrderResponse orderResponse = response.body();
                    if (orderResponse.isSuccess()) {
                        new AlertDialog.Builder(requireContext())
                                .setTitle("Order Confirmed")
                                .setMessage("Your pickup code: " + orderResponse.getPickup_code())
                                .setPositiveButton("OK", (dialog, which) -> {
                                    cartManager.clearCart();
                                    cartAdapter.refreshData();
                                })
                                .show();
                    } else {
                        Toast.makeText(getContext(), orderResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to place order", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
