package com.example.service;

import java.util.List;

/**
 * 数据库接口
 *
 * @author <a href="mailto:yanglulu@fcbox.com">005964</a>
 * @since 1.0 2026/04/28
 */
public interface Database {
    /**
     * 查询操作
     * @param sql SQL 语句
     * @param params 参数
     * @return 查询结果
     */
    List<Object> query(String sql, Object... params);

    /**
     * 更新操作
     * @param sql SQL 语句
     * @param params 参数
     * @return 受影响的行数
     */
    int update(String sql, Object... params);

    /**
     * 插入操作
     * @param sql SQL 语句
     * @param params 参数
     * @return 插入的对象
     */
    Object insert(String sql, Object... params);
}
