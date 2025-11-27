package com.example.myapplication2;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CartManager {

    private static CartManager instance;
    private final Context context;
    private int userId;
    private List<CartItem> cartItems;
    private final Gson gson = new Gson();

    private CartManager(Context context) {
        this.context = context.getApplicationContext();
        this.cartItems = new ArrayList<>();
    }

    public static synchronized CartManager getInstance(Context context) {
        if (instance == null) {
            instance = new CartManager(context);
        }
        return instance;
    }

    public void setUser(int userId) {
        this.userId = userId;
        loadCart();
    }

    // 🔹 Add or update existing cart item
    public void addItem(CartItem item) {
        if (cartItems == null) cartItems = new ArrayList<>();

        boolean found = false;
        for (CartItem c : cartItems) {
            if (c.getItemId() == item.getItemId()) {
                c.setQuantity(c.getQuantity() + item.getQuantity());
                found = true;
                break;
            }
        }

        if (!found) cartItems.add(item);
        saveCart();
    }

    // 🔹 Remove an item
    public void removeItem(int itemId) {
        if (cartItems == null) return;

        Iterator<CartItem> iterator = cartItems.iterator();
        while (iterator.hasNext()) {
            CartItem item = iterator.next();
            if (item.getItemId() == itemId) {
                iterator.remove();
                break;
            }
        }
        saveCart();
    }

    // 🔹 Update quantity of an item
    public void setQuantity(int itemId, int quantity) {
        for (CartItem item : cartItems) {
            if (item.getItemId() == itemId) {
                item.setQuantity(quantity);
                break;
            }
        }
        saveCart();
    }

    // 🔹 Get total price
    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }

    // 🔹 Get current cart list
    public List<CartItem> getCartItems() {
        return cartItems == null ? new ArrayList<>() : new ArrayList<>(cartItems);
    }

    // 🔹 Clear cart completely
    public void clearCart() {
        cartItems.clear();
        saveCart();
    }

    // 🔹 Save cart to SharedPreferences per user
    private void saveCart() {
        SharedPreferences prefs = context.getSharedPreferences("CartPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("cart_" + userId, gson.toJson(cartItems));
        editor.apply();
    }

    // 🔹 Load cart for the user
    private void loadCart() {
        SharedPreferences prefs = context.getSharedPreferences("CartPrefs", Context.MODE_PRIVATE);
        String json = prefs.getString("cart_" + userId, null);
        if (json != null) {
            Type type = new TypeToken<List<CartItem>>() {}.getType();
            cartItems = gson.fromJson(json, type);
        } else {
            cartItems = new ArrayList<>();
        }
    }
}
