# claude-code-pracices

## 1、bugfix-pipeline

代码Bug修复工作流

```
┌─────────┐  自动 ┌─────────┐  审核  ┌─────────┐ 自动  ┌─────────┐
│ Locator │ ────→ │ Analyzer│ ────→ │  Fixer  │ ────→ │Verifier │
└─────────┘       └─────────┘       └─────────┘       └─────────┘
```

提示词：

```
帮我修复这个 bug：xxx。

执行方式：
1. 先让 bug-locator 定位 → 自动传给 bug-analyzer
2. bug-analyzer 分析完后 → 先给我看根因分析，我确认后再继续
3. 我确认后 → 让 bug-fixer 修复 → 自动传给 bug-verifier
4. bug-verifier 验证完给我最终报告
```

