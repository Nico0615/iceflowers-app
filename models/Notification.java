package com.example.myapplication2.models;

import com.google.gson.annotations.SerializedName;

public class Notification {
    @SerializedName("id")
    public int id;

    @SerializedName("order_id")
    public int orderId;

    @SerializedName("message")
    public String message;

    @SerializedName("created_at")
    public String createdAt;

    @SerializedName("is_read")
    public int isReadInt;

    public boolean isRead() {
        return isReadInt == 1;
    }

    public void setRead(boolean read) {
        this.isReadInt = read ? 1 : 0;
    }
}
