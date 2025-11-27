package com.example.myapplication2.api;

import java.util.List;

public class MenuResponse {
    public boolean success;
    public List<MenuItem> menu;
    public int current_page;
    public int total_pages;
    public boolean has_more;
}
