package com.example.service;

/**
 * 积分更新结果
 */
public class PointUpdateResult {
    private final String userId;
    private final int oldPoints;
    private final int newPoints;

    public PointUpdateResult(String userId, int oldPoints, int newPoints) {
        this.userId = userId;
        this.oldPoints = oldPoints;
        this.newPoints = newPoints;
    }

    public String getUserId() {
        return userId;
    }

    public int getOldPoints() {
        return oldPoints;
    }

    public int getNewPoints() {
        return newPoints;
    }

    @Override
    public String toString() {
        return "PointUpdateResult{" +
                "userId='" + userId + '\'' +
                ", oldPoints=" + oldPoints +
                ", newPoints=" + newPoints +
                '}';
    }
}
