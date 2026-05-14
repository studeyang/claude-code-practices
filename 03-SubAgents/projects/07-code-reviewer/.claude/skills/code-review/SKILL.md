---
name: code-review
description: Review code for quality, security, and best practices. Checks for bugs, performance issues, and style violations. Use when the user asks for code review, wants feedback on their code, mentions reviewing changes, or asks about code quality.
allowed-tools: Read, Grep, Glob, Bash
---

# Code Review

从代码质量、安全性和最佳实践角度审查代码变更。

## 审查维度

### 安全性（最高优先级）
- SQL注入漏洞
- XSS漏洞
- 硬编码的密钥/凭据
- 认证/授权问题
- 输入验证缺失
- 不安全的密码学实践

### 性能
- N+1查询模式
- 内存泄漏
- 异步代码中的阻塞操作
- 缺失的缓存机会

### 可维护性
- 代码复杂度
- 缺失的错误处理
- 命名规范不佳
- 复杂逻辑缺乏文档

### 最佳实践
- SOLID原则违背
- 反模式
- 代码重复
- 缺失类型安全

## 调用时机

1. **识别变更**：`git diff` 或读取指定文件
2. **分析代码**：从多个维度检查
3. **报告问题**：按严重程度分类

## 输出格式

```markdown
## 代码审查报告

### 严重问题
- [文件:行号] 问题描述
  - 为什么重要
  - 建议修复方案

### 警告
- [文件:行号] 问题描述
  - 建议

### 建议
- [文件:行号] 改进机会

### 总结
- 问题总数：X
- 严重：X | 警告：X | 建议：X
- 整体风险评估：高/中/低
```

## 指南

- 严格只读，绝不修改文件
- 优先考虑安全性问题
- 具体说明位置（文件:行号）
- 提供可操作的修复建议
- 聚焦于变更部分