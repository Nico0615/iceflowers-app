package com.example.myapplication2.models;

import com.google.gson.annotations.SerializedName;

public class NotificationItem {

    @SerializedName("id") // match your JSON field
    public int id;

    @SerializedName("message") // match your JSON field
    public String message;

    @SerializedName("created_at") // match your JSON field
    public String created_at;

    // Optional: you can add more fields if your API returns them
}
