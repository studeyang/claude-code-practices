# Bug Fix Pipeline - Java 版本

这是一个用于练习 bug 修复的 Java 项目，包含三个服务模块，每个服务都包含特定的 bug 供学习和修复。

## 项目结构

```
java/
├── pom.xml                                          # Maven 配置文件
├── src/main/java/com/example/
│   ├── model/                                       # 数据模型
│   │   ├── User.java                               # 用户模型
│   │   ├── Cart.java                               # 购物车模型
│   │   ├── CartItem.java                           # 购物车项模型
│   │   ├── Order.java                              # 订单模型
│   │   └── OrderOptions.java                       # 订单选项
│   ├── service/                                     # 业务服务
│   │   ├── UserService.java                        # 用户服务（竞态条件 bug）
│   │   ├── CartService.java                        # 购物车服务（空指针 bug）
│   │   ├── OrderService.java                       # 订单服务（类型转换 bug）
│   │   ├── Database.java                           # 数据库接口
│   │   ├── PointUpdateResult.java                  # 积分更新结果
│   │   └── PointTransferResult.java                # 积分转移结果
│   └── util/                                        # 工具类
│       └── Utils.java                              # 工具函数
└── src/test/java/com/example/service/
    ├── MockDatabase.java                           # 模拟数据库
    └── ServiceTest.java                            # 服务测试
```

## 包含的 Bug

### 1. UserService - 竞态条件 Bug
**位置**: `UserService.addPoints()` 方法

**问题**: 并发调用时会丢失更新
```java
// BUG: 这里存在竞态条件
// 如果两个请求同时执行，它们会读到相同的 currentPoints
// 然后各自计算 newPoints，导致其中一个更新被覆盖
int currentPoints = user.getPoints() != null ? user.getPoints() : 0;
int newPoints = currentPoints + points;
```

**修复建议**: 使用数据库原子操作
```sql
UPDATE users SET points = points + ? WHERE id = ?
```

### 2. CartService - 空指针 Bug
**位置**: `CartService.calculateTotal()` 方法

**问题**: 没有处理空数组或 null 的情况
```java
// BUG: 如果 items 是 null，会报错
// NullPointerException
return items.stream()
        .mapToDouble(item -> item.getPrice() * item.getQuantity())
        .sum();
```

**修复建议**: 添加 null 检查
```java
if (items == null || items.isEmpty()) {
    return 0;
}
```

### 3. OrderService - 类型转换 Bug
**位置**: `OrderService.calculatePointsDiscount()` 方法

**问题**: 没有正确处理类型转换
```java
// BUG: points 可能是字符串
// 在 Java 中需要显式类型转换
if (points == null || points <= 0) {
    return 0;
}
```

**修复建议**: 使用工具类进行安全的类型转换

## 构建和运行

### 前置要求
- JDK 17+
- Maven 3.6+

### 编译项目
```bash
cd java
mvn clean compile
```

### 运行测试
```bash
mvn test
```

### 运行特定测试
```bash
mvn test -Dtest=ServiceTest#testCalculateTotalHandlesNullItems
```

### 打包
```bash
mvn clean package
```

## 测试说明

测试用例设计用于暴露 bug：

1. **testCalculateTotalHandlesNullItems** - 测试 null 输入时的行为
2. **testCalculateTotalHandlesEmptyArray** - 测试空数组时的行为
3. **testCalculateTotalComputesCorrectly** - 测试正常计算
4. **testPointsDiscountCalculation** - 测试积分折扣计算
5. **testAddPoints** - 测试积分添加（可能暴露竞态条件）

## 修复指南

### Bug 修复流程

1. **定位**: 运行测试，找到失败的测试
2. **分析**: 阅读代码，理解 bug 根本原因
3. **修复**: 实施最小化的修复
4. **验证**: 重新运行测试，确保修复有效

### 修复原则

- ✅ 做最小的必要更改
- ✅ 匹配现有代码风格
- ✅ 添加必要的 null/类型检查
- ✅ 使用现有的工具函数
- ❌ 不要重构不相关的代码
- ❌ 不要添加不必要的抽象
- ❌ 不要无理由更改函数签名

## 学习要点

通过这个项目，你可以学习：

1. **异步编程**: 使用 CompletableFuture 进行异步操作
2. **异常处理**: 正确处理 null 和边界情况
3. **并发控制**: 理解竞态条件和解决方案
4. **类型安全**: Java 的强类型系统如何帮助避免 bug
5. **单元测试**: 编写测试来暴露和预防 bug

## 扩展练习

1. 修复所有已知的 bug
2. 添加更多的测试用例
3. 实现数据库连接池
4. 添加事务支持
5. 实现缓存失效策略
6. 添加日志记录

## 许可证

MIT License
