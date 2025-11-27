package com.example.myapplication2.models;

import java.util.List;

public class OrderSummary {
    private int order_id;
    private String pickup_code;
    private String status;
    private double total_amount;
    private String created_at;
    private List<OrderItemSummary> items; // NEW: list of products in this order

    public int getOrder_id() { return order_id; }
    public String getPickup_code() { return pickup_code; }
    public String getStatus() { return status; }
    public double getTotal_amount() { return total_amount; }
    public String getCreated_at() { return created_at; }
    public List<OrderItemSummary> getItems() { return items; }
}
