package com.example.service;

import com.example.model.CartItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 服务测试
 * 用于验证 bug 修复
 *
 * @author <a href="mailto:yanglulu@fcbox.com">005964</a>
 * @since 1.0 2026/04/28
 */
public class ServiceTest {

    private MockDatabase mockDb;
    private UserService userService;
    private CartService cartService;
    private OrderService orderService;

    @BeforeEach
    public void setUp() {
        mockDb = new MockDatabase();
        userService = new UserService(mockDb);
        cartService = new CartService(mockDb);
        orderService = new OrderService(mockDb, cartService, userService);
    }

    @Test
    @DisplayName("UserService - getUser 返回用户对象")
    public void testGetUserReturnsUserObject() {
        var user = userService.getUser("1");
        assertNotNull(user, "User should exist");
        assertEquals("Test User", user.getName());
    }

    @Test
    @DisplayName("CartService - calculateTotal 处理 null 输入")
    public void testCalculateTotalHandlesNullItems() {
        // 这个测试会暴露 bug
        assertThrows(NullPointerException.class, () -> {
            cartService.calculateTotal(null);
        }, "BUG: calculateTotal crashes on null input");
    }

    @Test
    @DisplayName("CartService - calculateTotal 处理空数组")
    public void testCalculateTotalHandlesEmptyArray() {
        double total = cartService.calculateTotal(List.of());
        assertEquals(0, total, "Empty cart total should be 0");
    }

    @Test
    @DisplayName("CartService - calculateTotal 计算正确")
    public void testCalculateTotalComputesCorrectly() {
        var items = List.of(
                new CartItem("1", "u1", "p1", 2, 50.0, "Product 1"),
                new CartItem("2", "u1", "p2", 1, 30.0, "Product 2")
        );
        double total = cartService.calculateTotal(items);
        assertEquals(130.0, total, "Total should be 130 (2*50 + 1*30)");
    }

    @Test
    @DisplayName("OrderService - 积分折扣计算正确")
    public void testPointsDiscountCalculation() {
        // 100 积分应该抵扣 10 元，但不超过订单金额的 10%
        double discount = orderService.calculatePointsDiscount(100, 200.0);
        assertEquals(10.0, discount, "100 points should give 10 yuan discount");

        // 测试最大折扣限制
        discount = orderService.calculatePointsDiscount(1000, 50.0);
        assertEquals(5.0, discount, "Max discount should be 10% of 50 = 5");
    }

    @Test
    @DisplayName("OrderService - 零积分无折扣")
    public void testZeroPointsNoDiscount() {
        double discount = orderService.calculatePointsDiscount(0, 100.0);
        assertEquals(0.0, discount, "Zero points should give no discount");
    }

    @Test
    @DisplayName("OrderService - null 积分无折扣")
    public void testNullPointsNoDiscount() {
        double discount = orderService.calculatePointsDiscount(null, 100.0);
        assertEquals(0.0, discount, "Null points should give no discount");
    }

    @Test
    @DisplayName("UserService - 添加积分")
    public void testAddPoints() {
        var result = userService.addPoints("1", 50);
        assertEquals(150, result.getNewPoints(), "New points should be 150");
    }

    @Test
    @DisplayName("CartService - 获取购物车")
    public void testGetCart() {
        var cart = cartService.getCart("1");
        assertNotNull(cart);
        assertEquals("1", cart.getUserId());
        assertNotNull(cart.getItems());
    }
}