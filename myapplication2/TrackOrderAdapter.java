package com.example.myapplication2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication2.models.OrderSummary;

import java.util.List;

public class TrackOrderAdapter extends RecyclerView.Adapter<TrackOrderAdapter.ViewHolder> {

    private List<OrderSummary> orderList;
    private OnOrderClickListener listener;

    public TrackOrderAdapter(List<OrderSummary> orderList) {
        this.orderList = orderList;
    }

    public interface OnOrderClickListener {
        void onOrderClicked(int orderId);
    }

    public void setOnOrderClickListener(OnOrderClickListener listener) {
        this.listener = listener;
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

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClicked(order.getOrder_id());
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCode, tvStatus, tvTotal;

        ViewHolder(View itemView) {
            super(itemView);
            tvCode = itemView.findViewById(R.id.tv_order_code);
            tvStatus = itemView.findViewById(R.id.tv_order_status);
            tvTotal = itemView.findViewById(R.id.tv_order_total);
        }
    }
}
