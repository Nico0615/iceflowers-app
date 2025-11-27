package com.example.myapplication2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication2.models.OrderSummary;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private final List<OrderSummary> orders;

    public OrderAdapter(List<OrderSummary> orders) {
        this.orders = orders;
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
        OrderSummary order = orders.get(position);
        holder.tvOrderCode.setText("Order Code: " + order.getPickup_code());

        String statusText = order.getStatus().equalsIgnoreCase("ready")
                ? "Status: READY FOR PICKUP"
                : "Status: " + order.getStatus();

        holder.tvOrderStatus.setText(statusText);
        holder.tvOrderTotal.setText("Total: ₱" + order.getTotal_amount());
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderCode, tvOrderStatus, tvOrderTotal;

        ViewHolder(View view) {
            super(view);
            tvOrderCode = view.findViewById(R.id.tv_order_code);
            tvOrderStatus = view.findViewById(R.id.tv_order_status);
            tvOrderTotal = view.findViewById(R.id.tv_order_total);
        }
    }
}
