package com.example.myapplication2;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication2.models.OrderItemSummary;
import com.example.myapplication2.models.OrderSummary;

import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    private List<OrderSummary> orderList;

    public OrderHistoryAdapter(List<OrderSummary> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_summary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderSummary order = orderList.get(position);

        holder.tvCode.setText("Order Code: " + order.getPickup_code());
        holder.tvStatus.setText("Status: " + order.getStatus());
        holder.tvTotal.setText("Total: ₱" + order.getTotal_amount());

        // Populate order items dynamically inside the card
        holder.layoutOrderItems.removeAllViews();
        for (OrderItemSummary item : order.getItems()) {
            TextView tvItem = new TextView(holder.itemView.getContext());
            tvItem.setText(item.getName() + " x" + item.getQuantity() + " - ₱" + item.getSubtotal());
            tvItem.setTextSize(14);
            holder.layoutOrderItems.addView(tvItem);
        }

        // Show detailed dialog on click
        holder.itemView.setOnClickListener(v -> showOrderDetailsDialog(holder.itemView.getContext(), order));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    private void showOrderDetailsDialog(Context context, OrderSummary order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Order #" + order.getPickup_code());

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 24, 24, 24);

        for (OrderItemSummary item : order.getItems()) {
            TextView tv = new TextView(context);
            tv.setText(item.getName() + " x" + item.getQuantity() + " - ₱" + item.getSubtotal());
            tv.setTextSize(16);
            layout.addView(tv);
        }

        TextView total = new TextView(context);
        total.setText("\nTotal: ₱" + order.getTotal_amount());
        total.setTextSize(18);
        total.setPadding(0, 12, 0, 0);
        layout.addView(total);

        builder.setView(layout);
        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCode, tvStatus, tvTotal;
        LinearLayout layoutOrderItems;

        ViewHolder(View itemView) {
            super(itemView);
            tvCode = itemView.findViewById(R.id.tv_order_code);
            tvStatus = itemView.findViewById(R.id.tv_order_status);
            tvTotal = itemView.findViewById(R.id.tv_order_total);
            layoutOrderItems = itemView.findViewById(R.id.layout_order_items);
        }
    }
}
