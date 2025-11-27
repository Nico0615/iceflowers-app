package com.example.myapplication2;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("first_name")
    private String firstName;

    public boolean isSuccess() {
        return status != null && status.equalsIgnoreCase("success");
    }

    public String getMessage() {
        return message != null ? message : "Unknown error";
    }

    public int getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName != null ? firstName : "";
    }
}
