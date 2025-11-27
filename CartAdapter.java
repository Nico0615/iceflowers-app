package com.example.myapplication2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private final Context context;
    private final CartManager cartManager;
    private List<CartItem> cartItems;

    public CartAdapter(Context context, List<CartItem> items) {
        this.context = context;
        this.cartManager = CartManager.getInstance(context);
        this.cartItems = items;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        holder.name.setText(item.getName() != null ? item.getName() : "Unknown");
        holder.price.setText("₱" + String.format("%.2f", item.getPrice() * item.getQuantity()));
        holder.quantity.setText(String.valueOf(item.getQuantity()));

        holder.plusBtn.setOnClickListener(v -> {
            int newQty = item.getQuantity() + 1;
            item.setQuantity(newQty);
            cartManager.setQuantity(item.getItemId(), newQty);
            notifyItemChanged(position);
        });

        holder.minusBtn.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                int newQty = item.getQuantity() - 1;
                item.setQuantity(newQty);
                cartManager.setQuantity(item.getItemId(), newQty);
                notifyItemChanged(position);
            } else {
                Toast.makeText(context, "Minimum quantity is 1", Toast.LENGTH_SHORT).show();
            }
        });

        holder.removeBtn.setOnClickListener(v -> {
            cartManager.removeItem(item.getItemId());
            cartItems = cartManager.getCartItems();
            notifyDataSetChanged();
            Toast.makeText(context, "Item removed", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }

    public void refreshData() {
        this.cartItems = cartManager.getCartItems();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, quantity;
        TextView plusBtn, minusBtn, removeBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cartItemName);
            price = itemView.findViewById(R.id.cartItemPrice);
            quantity = itemView.findViewById(R.id.cartItemQuantity);
            plusBtn = itemView.findViewById(R.id.plusButton);
            minusBtn = itemView.findViewById(R.id.minusButton);
            removeBtn = itemView.findViewById(R.id.removeButton);
        }
    }
}
