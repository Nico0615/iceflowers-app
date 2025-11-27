package com.example.myapplication2.models;

import com.google.gson.annotations.SerializedName;

public class UserUpdateResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
