package com.example.myapplication2.models;

public class OrderItem {
    private int quantity;
    private double subtotal;
    private int product_id;
    private String item_name;

    // getters
    public int getQuantity() { return quantity; }
    public double getSubtotal() { return subtotal; }
    public int getProduct_id() { return product_id; }
    public String getItem_name() { return item_name; }
}
