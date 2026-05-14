package com.example.service;

import com.example.model.Order;
import com.example.model.User;

import java.util.*;

/**
 * 模拟数据库实现
 *
 * @author <a href="mailto:yanglulu@fcbox.com">005964</a>
 * @since 1.0 2026/04/28
 */
public class MockDatabase implements Database {
    private final Map<String, List<Map<String, Object>>> data;

    public MockDatabase() {
        this.data = new HashMap<>();
        initializeData();
    }

    private void initializeData() {
        // 初始化用户数据
        List<Map<String, Object>> users = new ArrayList<>();
        Map<String, Object> user = new HashMap<>();
        user.put("id", "1");
        user.put("name", "Test User");
        user.put("email", "test@example.com");
        user.put("points", 100);
        users.add(user);
        data.put("users", users);

        // 初始化商品数据
        List<Map<String, Object>> products = new ArrayList<>();
        Map<String, Object> product1 = new HashMap<>();
        product1.put("id", "p1");
        product1.put("name", "Product 1");
        product1.put("price", 50.0);
        products.add(product1);

        Map<String, Object> product2 = new HashMap<>();
        product2.put("id", "p2");
        product2.put("name", "Product 2");
        product2.put("price", 30.0);
        products.add(product2);
        data.put("products", products);

        // 初始化购物车
        data.put("cart_items", new ArrayList<>());

        // 初始化订单
        data.put("orders", new ArrayList<>());
    }

    @Override
    public List<Object> query(String sql, Object... params) {
        System.out.println("Mock DB query: " + sql.trim().substring(0, Math.min(50, sql.length())));

        List<Object> result = new ArrayList<>();

        // 简单的 SQL 解析
        if (sql.toLowerCase().contains("select * from users where id")) {
            List<Map<String, Object>> users = data.get("users");
            for (Map<String, Object> u : users) {
                if (u.get("id").equals(params[0])) {
                    result.add(createUser(u));
                }
            }
        } else if (sql.toLowerCase().contains("select * from cart_items")) {
            result.addAll(data.get("cart_items"));
        } else if (sql.toLowerCase().contains("select") && sql.toLowerCase().contains("from products")) {
            result.addAll(data.get("products"));
        }

        return result;
    }

    @Override
    public int update(String sql, Object... params) {
        System.out.println("Mock DB update: " + sql.trim().substring(0, Math.min(50, sql.length())));

        if (sql.toLowerCase().contains("update users set points")) {
            List<Map<String, Object>> users = data.get("users");
            for (Map<String, Object> u : users) {
                if (u.get("id").equals(params[1])) {
                    u.put("points", (Integer) params[0]);
                    return 1;
                }
            }
        } else if (sql.toLowerCase().contains("update cart_items set quantity")) {
            return 1;
        } else if (sql.toLowerCase().contains("delete from cart_items")) {
            return 1;
        } else if (sql.toLowerCase().contains("update orders set status")) {
            return 1;
        }

        return 0;
    }

    @Override
    public Object insert(String sql, Object... params) {
        System.out.println("Mock DB insert: " + sql.trim().substring(0, Math.min(50, sql.length())));

        if (sql.toLowerCase().contains("insert into orders")) {
            Map<String, Object> order = new HashMap<>();
            order.put("id", "order-" + System.currentTimeMillis());
            order.put("user_id", params[0]);
            order.put("subtotal", params[1]);
            order.put("shipping_fee", params[2]);
            order.put("discount", params[3]);
            order.put("total", params[4]);
            order.put("status", "pending");
            order.put("created_at", new Date().toString());
            data.get("orders").add(order);
            return createOrder(order);
        } else if (sql.toLowerCase().contains("insert into order_items")) {
            return new Object();
        } else if (sql.toLowerCase().contains("insert into cart_items")) {
            return 1;
        }

        return null;
    }

    private User createUser(Map<String, Object> data) {
        User user = new User();
        user.setId((String) data.get("id"));
        user.setName((String) data.get("name"));
        user.setEmail((String) data.get("email"));
        user.setPoints((Integer) data.get("points"));
        return user;
    }

    private Order createOrder(Map<String, Object> data) {
        Order order = new Order();
        order.setId((String) data.get("id"));
        order.setUserId((String) data.get("user_id"));
        order.setSubtotal((Double) data.get("subtotal"));
        order.setShippingFee((Double) data.get("shipping_fee"));
        order.setDiscount((Double) data.get("discount"));
        order.setTotal((Double) data.get("total"));
        order.setStatus((String) data.get("status"));
        order.setCreatedAt((String) data.get("created_at"));
        return order;
    }
}
