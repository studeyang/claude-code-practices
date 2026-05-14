package com.example.model;

import java.util.List;

/**
 * 购物车模型
 */
public class Cart {
    private String userId;
    private List<CartItem> items;
    private double total;

    public Cart() {}

    public Cart(String userId, List<CartItem> items, double total) {
        this.userId = userId;
        this.items = items;
        this.total = total;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "userId='" + userId + '\'' +
                ", items=" + items +
                ", total=" + total +
                '}';
    }
}
