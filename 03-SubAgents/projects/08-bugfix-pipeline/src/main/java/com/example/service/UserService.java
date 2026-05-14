package com.example.service;

import com.example.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户服务
 * 包含一个竞态条件 bug 供练习
 *
 * @author <a href="mailto:yanglulu@fcbox.com">005964</a>
 * @since 1.0 2026/04/28
 */
public class UserService {
    private final Database db;
    private final Map<String, User> cache;

    public UserService(Database db) {
        this.db = db;
        this.cache = new ConcurrentHashMap<>();
    }

    /**
     * 获取用户
     * @param userId 用户 ID
     * @return 用户对象
     */
    public User getUser(String userId) {
        // 先查缓存
        if (cache.containsKey(userId)) {
            return cache.get(userId);
        }

        // 查数据库
        List<Object> result = db.query("SELECT * FROM users WHERE id = ?", userId);
        if (result != null && !result.isEmpty()) {
            User user = (User) result.get(0);
            cache.put(userId, user);
            return user;
        }
        return null;
    }

    /**
     * 更新用户积分（使用数据库原子操作，避免竞态条件）
     *
     * @param userId 用户 ID
     * @param points 积分
     * @return 更新结果
     */
    public PointUpdateResult addPoints(String userId, int points) {
        // 1. 查询用户信息用于验证和返回当前积分
        User user = getUser(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Integer userPoints = user.getPoints();
        int currentPoints = (userPoints != null) ? userPoints : 0;
        int newPoints = currentPoints + points;

        // 2. 使用数据库原子更新（points = points + ?）
        // 避免竞态条件：数据库在UPDATE时保证原子性
        int updatedRows = db.update("UPDATE users SET points = points + ? WHERE id = ?", points, userId);

        if (updatedRows == 0) {
            throw new RuntimeException("User not found");
        }

        // 3. 更新缓存
        user.setPoints(newPoints);
        cache.put(userId, user);
        return new PointUpdateResult(userId, currentPoints, newPoints);
    }

    /**
     * 正确的实现应该使用原子操作：
     * UPDATE users SET points = points + ? WHERE id = ?
     */

    /**
     * 转移积分
     * BUG: 没有事务，部分失败会导致数据不一致
     *
     * @param fromUserId 转出用户 ID
     * @param toUserId 转入用户 ID
     * @param points 积分数量
     * @return 转移结果
     */
    public PointTransferResult transferPoints(String fromUserId, String toUserId, int points) {
        // BUG: 这两个操作不是原子的
        // 如果第一个成功、第二个失败，积分就凭空消失了
        addPoints(fromUserId, -points);
        addPoints(toUserId, points);
        return new PointTransferResult(fromUserId, toUserId, points);
    }

    /**
     * 批量获取用户
     * @param userIds 用户 ID 列表
     * @return 用户列表
     */
    public List<User> getUsers(List<String> userIds) {
        List<User> users = new ArrayList<>();

        for (String userId : userIds) {
            User user = getUser(userId);
            if (user != null) {
                users.add(user);
            }
        }

        return users;
    }

    /**
     * 清除缓存
     */
    public void clearCache() {
        cache.clear();
    }

    /**
     * 清除特定用户缓存
     */
    public void clearCache(String userId) {
        cache.remove(userId);
    }
}