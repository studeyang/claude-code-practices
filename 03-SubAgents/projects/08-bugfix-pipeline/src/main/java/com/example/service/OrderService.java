package com.example.service;

import com.example.model.Order;
import com.example.model.OrderOptions;

import java.util.List;

/**
 * 订单服务
 * 包含类型错误 bug 供练习
 *
 * @author <a href="mailto:yanglulu@fcbox.com">005964</a>
 * @since 1.0 2026/04/28
 */
public class OrderService {
    private final Database db;
    private final CartService cartService;
    private final UserService userService;

    public OrderService(Database db, CartService cartService, UserService userService) {
        this.db = db;
        this.cartService = cartService;
        this.userService = userService;
    }

    /**
     * 创建订单
     * BUG: 字符串和数字混用导致计算错误
     *
     * @param userId 用户 ID
     * @param options 选项
     * @return 订单对象
     */
    public Order createOrder(String userId, OrderOptions options) {
        if (options == null) {
            options = new OrderOptions();
        }

        // 获取购物车
        var cart = cartService.getCart(userId);
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // BUG: cart.total 可能是字符串（从某些数据库返回）
        // 直接比较会导致逻辑错误
        // 例如："100" > 50 为 true，但 "100" + 10 = "10010"
        double total = cart.getTotal();

        // 应用配送费
        // BUG: 如果 total 是字符串，这里会变成字符串拼接而不是数字相加
        double shippingFee = total > 100 ? 0 : 10;
        double finalTotal = total + shippingFee;  // "100" + 10 = "10010"

        // 检查用户积分
        var user = userService.getUser(userId);
        double pointsDiscount = calculatePointsDiscount(user.getPoints(), finalTotal);

        // 创建订单
        Object orderResult = db.insert(
                "INSERT INTO orders (user_id, subtotal, shipping_fee, discount, total, status) VALUES (?, ?, ?, ?, ?, 'pending') RETURNING *",
                userId, total, shippingFee, pointsDiscount, finalTotal - pointsDiscount
        );

        Order order = (Order) orderResult;

        // 创建订单项
        for (var item : cart.getItems()) {
            db.insert(
                    "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)",
                    order.getId(), item.getProductId(), item.getQuantity(), item.getPrice()
            );
        }

        // 清空购物车
        cartService.clearCart(userId);

        return order;
    }

    /**
     * 计算积分折扣
     * BUG: 没有类型转换
     *
     * @param points 积分
     * @param total 总金额
     * @return 折扣金额
     */
    public double calculatePointsDiscount(Integer points, double total) {
        // BUG: points 可能是字符串
        // "100" / 10 = 10 (JavaScript 会自动转换)
        // 但 "100" > 0 的比较可能不符合预期

        if (points == null || points <= 0) {
            return 0;
        }

        // 每 10 积分抵扣 1 元，最多抵扣订单金额的 10%
        double maxDiscount = total * 0.1;
        double pointsValue = points / 10.0;

        // BUG: Math.min 在字符串参与时行为异常
        return Math.min(pointsValue, maxDiscount);
    }

    /**
     * 获取订单
     * @param orderId 订单 ID
     * @return 订单对象
     */
    public Order getOrder(String orderId) {
        List<Object> result = db.query("SELECT * FROM orders WHERE id = ?", orderId);
        if (result != null && !result.isEmpty()) {
            return (Order) result.get(0);
        }
        return null;
    }

    /**
     * 获取用户订单列表
     * @param userId 用户 ID
     * @return 订单列表
     */
    public List<Order> getUserOrders(String userId) {
        List<Object> result = db.query("SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC", userId);
        if (result != null) {
            return result.stream().map(obj -> (Order) obj).toList();
        }
        return List.of();
    }

    /**
     * 更新订单状态
     * @param orderId 订单 ID
     * @param status 状态
     * @return 更新后的订单
     */
    public Order updateOrderStatus(String orderId, String status) {
        db.update("UPDATE orders SET status = ? WHERE id = ?", status, orderId);
        return getOrder(orderId);
    }
}