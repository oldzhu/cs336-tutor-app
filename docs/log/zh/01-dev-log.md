# 开发日志

> **中文版** · [English](../en/01-dev-log.md)
> 记录所有决策、行动和进展

## 2026-06-30 — 项目启动

### 决策
- **Android 上的 Python**：Chaquopy（APK 内嵌入 CPython）—— 详见[规格 §7](../../spec/zh/01-project-overview.md#7-python-代码执行-chaquopy)
- **项目位置**：Windows 文件系统（`C:\Users\orien\cs336-tutor-app`）—— WSL 网络因 Windows 代理（127.0.0.1:10808）不稳定
- **OpenCode**：已通过 npm 在 Windows 上安装（v1.17.11），将在网络可用时用于 Android 项目脚手架搭建
- **双语文档**：每份英文文档链接到对应的中文文档，反之亦然

### 已完成的操作
- 创建 Git 仓库及初始文档结构
- 起草项目规格（中/英文）
- 起草包含 5 个阶段的开发计划
- 在 `docs/` 下创建双语文档框架
- 创建完整 Android 项目脚手架（Compose + Hilt + Room + Chaquopy）
- 生成 Gradle wrapper（8.11.1）— 修复 KAPT 锁问题
- 安装 Android SDK（platform 35、build-tools 35、platform-tools）
- **首次 APK 构建：成功** — `app-debug.apk`（43MB）
- 添加 BPE Tokenizer 参考 Python 实现
- 添加启动图标资源
- 设置 GitHub 远程仓库（`origin → git@github.com:oldzhu/cs336-tutor-app.git`）
- 生成 SSH 密钥用于 GitHub 认证

### 依赖网络（待办）
- [x] Android SDK 安装
- [ ] GitHub 推送（手动 — SSH 代理太慢）
- [ ] WSL 环境正在设置（apt update 通过代理较慢）

### 下一步
1. 完成离线项目脚手架（Gradle 文件、源代码模板）
2. 首次 Git 提交
3. 等待用户信号继续网络相关任务
