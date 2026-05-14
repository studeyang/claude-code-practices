package com.example.service;

/**
 * 积分转移结果
 */
public class PointTransferResult {
    private final String fromUserId;
    private final String toUserId;
    private final int points;

    public PointTransferResult(String fromUserId, String toUserId, int points) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.points = points;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public int getPoints() {
        return points;
    }

    @Override
    public String toString() {
        return "PointTransferResult{" +
                "fromUserId='" + fromUserId + '\'' +
                ", toUserId='" + toUserId + '\'' +
                ", points=" + points +
                '}';
    }
}
