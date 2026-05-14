package com.example.service;

import com.example.model.Cart;
import com.example.model.CartItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 购物车服务
 * 包含边界条件 bug 供练习
 *
 * @author <a href="mailto:yanglulu@fcbox.com">005964</a>
 * @since 1.0 2026/04/28
 */
public class CartService {
    private final Database db;

    public CartService(Database db) {
        this.db = db;
    }

    /**
     * 获取购物车
     * @param userId 用户 ID
     * @return 购物车对象
     */
    public Cart getCart(String userId) {
        List<Object> items = db.query(
                "SELECT ci.*, p.name, p.price " +
                "FROM cart_items ci " +
                "JOIN products p ON p.id = ci.product_id " +
                "WHERE ci.user_id = ?",
                userId
        );

        List<CartItem> cartItems = items != null ?
                items.stream().map(obj -> (CartItem) obj).toList() :
                new ArrayList<>();

        return new Cart(
                userId,
                cartItems,
                calculateTotal(cartItems)
        );
    }

    /**
     * 计算购物车总价
     * BUG: 没有处理空数组的情况
     *
     * @param items 购物车项列表
     * @return 总价
     */
    public double calculateTotal(List<CartItem> items) {
        // BUG: 如果 items 是 null，会报错
        // NullPointerException
        if (items == null) {
            return 0.0;
        }
        return items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    /**
     * 添加商品到购物车
     * BUG: 没有检查商品是否存在
     *
     * @param userId 用户 ID
     * @param productId 商品 ID
     * @param quantity 数量
     * @return 更新后的购物车
     */
    public Cart addItem(String userId, String productId, int quantity) {
        // BUG: 没有验证 productId 是否有效
        // 如果产品不存在，后续 getCart 会出问题

        // 检查是否已在购物车
        List<Object> existing = db.query(
                "SELECT * FROM cart_items WHERE user_id = ? AND product_id = ?",
                userId, productId
        );

        if (existing != null && !existing.isEmpty()) {
            // 更新数量
            db.update(
                    "UPDATE cart_items SET quantity = quantity + ? WHERE user_id = ? AND product_id = ?",
                    quantity, userId, productId
            );
        } else {
            // 新增
            db.update(
                    "INSERT INTO cart_items (user_id, product_id, quantity) VALUES (?, ?, ?)",
                    userId, productId, quantity
            );
        }

        return getCart(userId);
    }

    /**
     * 更新商品数量
     * BUG: 允许负数数量
     *
     * @param userId 用户 ID
     * @param productId 商品 ID
     * @param quantity 数量
     * @return 更新后的购物车
     */
    public Cart updateQuantity(String userId, String productId, int quantity) {
        // BUG: 没有检查 quantity 是否为正数
        // 负数数量会导致数据异常

        if (quantity == 0) {
            return removeItem(userId, productId);
        }

        db.update(
                "UPDATE cart_items SET quantity = ? WHERE user_id = ? AND product_id = ?",
                quantity, userId, productId
        );

        return getCart(userId);
    }

    /**
     * 移除商品
     * @param userId 用户 ID
     * @param productId 商品 ID
     * @return 更新后的购物车
     */
    public Cart removeItem(String userId, String productId) {
        db.update(
                "DELETE FROM cart_items WHERE user_id = ? AND product_id = ?",
                userId, productId
        );

        return getCart(userId);
    }

    /**
     * 清空购物车
     * @param userId 用户 ID
     */
    public void clearCart(String userId) {
        db.update(
                "DELETE FROM cart_items WHERE user_id = ?",
                userId
        );
    }
}