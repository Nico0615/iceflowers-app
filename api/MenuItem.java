package com.example.myapplication2.api;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class MenuItem implements Serializable {

    @SerializedName(value = "id", alternate = {"item_id"})
    public int id;

    @SerializedName(value = "name", alternate = {"item_name"})
    public String name;

    @SerializedName("description")
    public String description;

    @SerializedName("image_url")
    public String image_url;

    @SerializedName("category")
    public String category;

    @SerializedName(value = "price", alternate = {"item_price"})
    public double price;

    @SerializedName("is_featured")
    public int is_featured;

    public List<SizeOption> sizes;
}
