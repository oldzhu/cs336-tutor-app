# 开发计划：CS336 导师应用

> **中文版** · [English](../en/01-mvp-plan.md)
> 参考: [项目规格](../../spec/zh/01-project-overview.md)

## 阶段 0：基础搭建（第 1 周）

**目标**：可运行的 Android 项目骨架 + 双语文档

- [x] 创建 Git 仓库 + 文档结构
- [ ] 设计规格文档（spec/zh, spec/en）
- [ ] 开发计划（本文档）
- [ ] 脚手架 Android Compose 项目
- [ ] `Hello World` Compose 页面在模拟器渲染
- [ ] 配置 Gradle 集成 Chaquopy SDK
- [ ] 配置 Room 数据库架构
- [ ] 创建 LLM 提供商接口 + Mock 实现
- [ ] 验证 APK 构建成功
- [ ] 首个 Git 标签: `v0.1.0-foundation`

## 阶段 1：MVP — BPE Tokenizer 导师（第 2-3 周）

**目标**：一个组件的可工作分屏导师

### 核心 UI
- [ ] **SplitScreenTutor** 活动 — 双面板布局
- [ ] **AIExplanationPanel** — 可滚动，逐行显示代码 + 讲解
- [ ] **CodeEditorPanel** — 可编辑代码区域，语法高亮
- [ ] **Q&A 浮层** — 底部弹窗，用于向 LLM 提问
- [ ] **StatusBar** — 进度指示器，LLM 提供商切换，暂停/继续

### BPE Tokenizer 组件
- [ ] BPE 规格加载到组件注册表
- [ ] 逐行代码：`train_bpe()`、`encode()`、`decode()`
- [ ] LLM 为每行生成讲解
- [ ] **评判功能**：用户代码对比预期代码，返回差异 + 评分
- [ ] 通过 Chaquopy 执行 Python 代码进行测试
- [ ] 离线回退：预装讲解内容（基本流程无需 LLM）

### LLM 集成
- [ ] 远程提供商：OpenAI（主要）、Anthropic（备用）
- [ ] 本地提供商：Ollama（可配置端点）
- [ ] 设置中的提供商选择 UI
- [ ] API 密钥管理（通过 EncryptedSharedPreferences 加密存储）

### 验证
- [ ] 用户可以端到端完成 BPE Tokenizer 课程
- [ ] AI 评判能捕获典型错误（词表大小错误、缺少合并）
- [ ] 问答功能回答当前行的问题
- [ ] 进度在应用重启后仍然保留
- [ ] APK 大小 < 80MB
- [ ] 标签: `v0.2.0-mvp-bpe`

## 阶段 2：完整 Transformer 管道（第 4-6 周）

- [ ] **RMSNorm** 组件导师
- [ ] **RoPE（旋转位置编码）** 组件导师
- [ ] **多头自注意力** 组件导师
- [ ] **位置前馈网络（SwiGLU）** 组件导师
- [ ] **完整 Transformer 块** — 拼接各组件
- [ ] **输出层**（LM head + softmax + 交叉熵损失）
- [ ] **训练循环** 导师 — 前向、反向、优化器步骤
- [ ] 可视化：注意力图、损失曲线
- [ ] 组件依赖图 — 基于前置条件锁定/解锁
- [ ] 标签: `v0.3.0-full-transformer`

## 阶段 3：高级功能（第 7-8 周）

- [ ] **动态组件生成** — LLM 在运行时创建新的导师模块
- [ ] **MoE（混合专家）** 组件
- [ ] **MLA（多头潜注意力）** 组件
- [ ] **DeepSeek V4 风格架构** 组件
- [ ] 用户代码导出（将完成的代码保存到文件）
- [ ] 进度分享 / 排行榜
- [ ] 标签: `v0.4.0-extensible`

## 阶段 4：打磨与发布（第 9-10 周）

- [ ] 性能优化（冷启动 < 3 秒）
- [ ] 新用户引导教程
- [ ] 离线模式（完整本地 LLM + 缓存内容）
- [ ] 应用图标、主题、无障碍支持
- [ ] Play Store 测试版发布
- [ ] 标签: `v1.0.0-beta`

## 技术决策及理由

| 决策 | 选择 | 理由 |
|---|---|---|
| Python 运行时 | Chaquopy | APK 内完整 CPython，无需后端 |
| 代码编辑器 | 通过 WebView 的 CodeMirror | 成熟的语法高亮，逐行交互 |
| DI 框架 | Hilt | 官方 Android 推荐，Compose 友好 |
| 架构 | MVVM + 整洁架构 | 可测试、可维护、Google 推荐 |
| LLM 流式传输 | Kotlin Flow | 响应式 UI 更新，支持取消 |
| 本地 LLM | Ollama | 最简设置，OpenAI 兼容 API，跨平台 |

## 风险登记表

| 风险 | 缓解措施 |
|---|---|
| Chaquopy 与 numpy 兼容性 | 使用 Chaquopy 内置 numpy 构建；锁定版本 |
| APK 大小超过 100MB | 模块化：按需下载组件包 |
| LLM API 费用 | 支持本地模型；缓存讲解内容 |
| WSL 中的 Android 模拟器 | 使用 Windows 端模拟器，从 WSL 通过 ADB 连接 |

---

> 🔗 **相关文档**
> - [项目规格 (中文)](../../spec/zh/01-project-overview.md)
> - [项目规格 (English)](../../spec/en/01-project-overview.md)
> - [README](../../../README.md)
