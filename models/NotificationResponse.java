package com.example.myapplication2.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NotificationResponse {

    @SerializedName("success")
    public boolean success;

    @SerializedName("notifications")
    public List<NotificationItem> notifications;

    public static class NotificationItem {
        @SerializedName("id")
        public int id;

        @SerializedName("order_id")
        public int orderId;

        @SerializedName("message")
        public String message;

        @SerializedName("created_at")
        public String createdAt;
    }
}
