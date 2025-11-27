package com.example.myapplication2;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication2.models.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final List<Notification> items = new ArrayList<>();
    private NotificationClickListener clickListener;

    public interface NotificationClickListener {
        void onNotificationClick(Notification notification, int position);
    }

    public void setNotificationClickListener(NotificationClickListener listener) {
        this.clickListener = listener;
    }

    public void setItems(List<Notification> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void addNotification(Notification item) {
        items.add(0, item);
        notifyItemInserted(0);
    }

    public void markAsRead(int position) {
        if (position >= 0 && position < items.size()) {
            items.get(position).setRead(true);
            notifyItemChanged(position);

            notifyItemChanged(position);
        }
    }

    public List<Notification> getItems() {
        return items;
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        Notification item = items.get(position);
        holder.message.setText(item.message != null ? item.message : "");
        holder.time.setText(item.createdAt != null ? item.createdAt : "");

        // Visual distinction for read/unread notifications
        if (item.isRead()) {
            holder.message.setAlpha(0.5f);
            holder.message.setTypeface(null, Typeface.NORMAL);
        } else {
            holder.message.setAlpha(1f);
            holder.message.setTypeface(null, Typeface.BOLD);
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null && !item.isRead()) { // Only allow click if unread
                clickListener.onNotificationClick(item, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView message, time;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.notification_message);
            time = itemView.findViewById(R.id.notification_time);
        }
    }
}
