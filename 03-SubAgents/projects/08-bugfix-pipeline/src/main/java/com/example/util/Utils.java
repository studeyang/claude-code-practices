package com.example.util;

/**
 * 工具函数
 */
public class Utils {

    /**
     * 安全地将值转换为数字
     * @param value 值
     * @param defaultValue 默认值
     * @return 转换后的数字
     */
    public static double toNumber(Object value, double defaultValue) {
        if (value == null) {
            return defaultValue;
        }

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 安全地将值转换为整数
     * @param value 值
     * @param defaultValue 默认值
     * @return 转换后的整数
     */
    public static int toInt(Object value, int defaultValue) {
        if (value == null) {
            return defaultValue;
        }

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 安全地进行货币计算（避免浮点数精度问题）
     * @param amount 金额（以元为单位）
     * @return 金额（以分为单位）
     */
    public static long toCents(double amount) {
        return Math.round(toNumber(amount, 0) * 100);
    }

    /**
     * 将分转换为元
     * @param cents 分
     * @return 元
     */
    public static double toYuan(long cents) {
        return toNumber(cents, 0) / 100.0;
    }

    /**
     * 安全的加法（处理浮点数精度）
     * @param a 数字 a
     * @param b 数字 b
     * @return 和
     */
    public static double safeAdd(double a, double b) {
        return toYuan(toCents(a) + toCents(b));
    }

    /**
     * 安全的减法
     * @param a 数字 a
     * @param b 数字 b
     * @return 差
     */
    public static double safeSubtract(double a, double b) {
        return toYuan(toCents(a) - toCents(b));
    }

    /**
     * 安全的乘法
     * @param a 数字 a
     * @param b 数字 b
     * @return 积
     */
    public static double safeMultiply(double a, double b) {
        return toYuan(Math.round(toCents(a) * toNumber(b, 0)));
    }

    /**
     * 延迟函数
     * @param ms 毫秒数
     * @throws InterruptedException 中断异常
     */
    public static void delay(long ms) throws InterruptedException {
        Thread.sleep(ms);
    }

    /**
     * 重试函数
     * @param operation 操作
     * @param maxAttempts 最大尝试次数
     * @param delayMs 延迟毫秒数
     * @param backoff 退避因子
     * @param <T> 返回类型
     * @return 操作结果
     * @throws Exception 异常
     */
    public static <T> T retry(RetryableOperation<T> operation, int maxAttempts, long delayMs, double backoff) throws Exception {
        Exception lastError = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return operation.execute();
            } catch (Exception error) {
                lastError = error;

                if (attempt < maxAttempts) {
                    Thread.sleep((long) (delayMs * Math.pow(backoff, attempt - 1)));
                }
            }
        }

        throw lastError;
    }

    /**
     * 重试操作接口
     */
    @FunctionalInterface
    public interface RetryableOperation<T> {
        T execute() throws Exception;
    }
}
