# CS336 Tutor — Learn LLMs by Building One on Android

> [中文版](#cs336-导师应用--在-android-上从零构建语言模型)

**An Android app with an AI tutor that guides you through Stanford CS336: Language Modeling from Scratch — Assignment 1.**

🧑‍🏫 **AI explains** line by line → ✍️ **You code** alongside → 🤖 **AI judges** your code → ❓ **Ask questions** freely

---

## ✨ Features

| Feature | Description |
|---|---|
| 🧠 **Split-screen tutor** | AI explanation panel + interactive code editor, side by side |
| 🔄 **Switchable LLM** | Remote (OpenAI, Anthropic, OpenRouter) or local (Ollama, llama.cpp) |
| 🔌 **Chaquopy runtime** | Run Python code directly on your Android device — no backend needed |
| 🧩 **Extensible** | Start with classic decoder-only Transformer, dynamically add MoE, MLA, DeepSeek V4 |
| 🌏 **Bilingual** | Full UI and documentation in both Chinese and English |
| 📱 **Offline-capable** | Pre-bundled content + local LLM support |

## 🗺️ Roadmap

```
Phase 0: Foundation     → Skeleton app + docs        [⬜]
Phase 1: BPE Tutor MVP  → Split-screen BPE lesson    [⬜]
Phase 2: Full Transformer → RMSNorm, RoPE, Attn, MLP [⬜]
Phase 3: Advanced        → MoE, MLA, DeepSeek V4    [⬜]
Phase 4: Polish          → Play Store beta release   [⬜]
```

See [Development Plan](docs/plan/en/01-mvp-plan.md) for details.

## 🏗️ Build from Source

### Prerequisites

| Tool | Version | Notes |
|---|---|---|
| Android Studio | Hedgehog (2023.1.1+) | Or IntelliJ IDEA |
| JDK | 17+ | Bundled with Android Studio |
| Android SDK | 34+ | API 34 platform + build-tools |
| Gradle | 8.7+ | Gradle wrapper included |
| Git | Any | |

### Build Steps

```bash
# 1. Clone
git clone https://github.com/YOUR_USER/cs336-tutor-app.git
cd cs336-tutor-app

# 2. Open in Android Studio
# File → Open → select project root

# 3. Build APK
# Terminal:
./gradlew assembleDebug
# Or in Android Studio: Build → Build Bundle(s) / APK(s) → Build APK(s)

# 4. The APK will be at:
# app/build/outputs/apk/debug/app-debug.apk
```

### Configure LLM Provider

Edit `app/src/main/res/values/preferences.xml` or configure in-app:

```xml
<!-- Default: OpenAI -->
<string name="llm_provider">openai</string>
<string name="llm_endpoint">https://api.openai.com/v1</string>
<string name="llm_model">gpt-4o</string>
```

## 📚 Documentation

All docs are bilingual (Chinese + English, cross-linked):

| Document | English | 中文 |
|---|---|---|
| Project Spec | [View](docs/spec/en/01-project-overview.md) | [查看](docs/spec/zh/01-project-overview.md) |
| Development Plan | [View](docs/plan/en/01-mvp-plan.md) | [查看](docs/plan/zh/01-mvp-plan.md) |
| Dev Log | [View](docs/log/en/01-dev-log.md) | [查看](docs/log/zh/01-dev-log.md) |

## 📁 Project Structure

```
cs336-tutor-app/
├── app/                          # Android app module
│   └── src/main/
│       ├── java/com/cs336/tutor/ # Kotlin sources
│       │   ├── ui/               # Compose UI screens
│       │   ├── domain/           # Business logic
│       │   ├── data/             # Data layer (Room, API)
│       │   └── di/               # Dependency injection
│       ├── python/               # Python scripts (Chaquopy)
│       └── res/                  # Resources
├── docs/                         # Bilingual documentation
│   ├── spec/                     # Design specifications
│   ├── plan/                     # Development plans
│   ├── log/                      # Development logs
│   └── api/                      # API integration docs
├── build.gradle.kts              # Root build file
├── settings.gradle.kts           # Project settings
└── README.md                     # This file
```

## 🧪 Tech Stack

- **Language**: Kotlin 2.x
- **UI**: Jetpack Compose + Material 3
- **Architecture**: MVVM + Clean Architecture
- **Python runtime**: [Chaquopy](https://chaquo.com/chaquopy/) (embedded CPython)
- **Local storage**: Room Database
- **Networking**: OkHttp + Retrofit
- **DI**: Hilt

## 📄 License

MIT

---

> Built with ❤️ and OpenCode

---

# CS336 导师应用 — 在 Android 上从零构建语言模型

> [English](#cs336-tutor--learn-llms-by-building-one-on-android)

**一款 Android 应用，配有 AI 导师，引导你完成 Stanford CS336: 从零构建语言模型 — 作业 1。**

🧑‍🏫 **AI 逐行讲解** → ✍️ **你跟着编码** → 🤖 **AI 评判代码** → ❓ **自由提问**

---

## ✨ 功能

| 功能 | 描述 |
|---|---|
| 🧠 **分屏导师** | AI 讲解面板 + 交互式代码编辑器，左右并排 |
| 🔄 **可切换 LLM** | 远程（OpenAI、Anthropic、OpenRouter）或本地（Ollama、llama.cpp）|
| 🔌 **Chaquopy 运行时** | 直接在 Android 设备上运行 Python 代码 — 无需后端 |
| 🧩 **可扩展** | 从经典仅解码器 Transformer 开始，动态添加 MoE、MLA、DeepSeek V4 |
| 🌏 **双语** | 完整的用户界面和文档支持中英文 |
| 📱 **离线可用** | 预装内容 + 本地 LLM 支持 |

## 🗺️ 路线图

```
阶段 0：基础搭建    → 骨架应用 + 文档         [⬜]
阶段 1：BPE MVP    → 分屏 BPE 课程          [⬜]
阶段 2：完整 Transformer → RMSNorm, RoPE, Attn, MLP [⬜]
阶段 3：高级功能    → MoE, MLA, DeepSeek V4  [⬜]
阶段 4：打磨发布    → Play Store 测试版       [⬜]
```

详见[开发计划](docs/plan/zh/01-mvp-plan.md)。

## 🏗️ 从源码构建

### 前置条件

| 工具 | 版本 | 备注 |
|---|---|---|
| Android Studio | Hedgehog (2023.1.1+) | 或 IntelliJ IDEA |
| JDK | 17+ | Android Studio 自带 |
| Android SDK | 34+ | API 34 平台 + build-tools |
| Gradle | 8.7+ | 项目包含 Gradle Wrapper |
| Git | 任意 | |

### 构建步骤

```bash
# 1. 克隆
git clone https://github.com/YOUR_USER/cs336-tutor-app.git
cd cs336-tutor-app

# 2. 在 Android Studio 中打开
# File → Open → 选择项目根目录

# 3. 构建 APK
# 终端：
./gradlew assembleDebug
# 或在 Android Studio 中：Build → Build Bundle(s) / APK(s) → Build APK(s)

# 4. APK 位置：
# app/build/outputs/apk/debug/app-debug.apk
```

### 配置 LLM 提供商

编辑 `app/src/main/res/values/preferences.xml` 或在应用内配置：

```xml
<!-- 默认：OpenAI -->
<string name="llm_provider">openai</string>
<string name="llm_endpoint">https://api.openai.com/v1</string>
<string name="llm_model">gpt-4o</string>
```

## 📚 文档

所有文档均为双语（中英文互链）：

| 文档 | English | 中文 |
|---|---|---|
| 项目规格 | [查看](docs/spec/en/01-project-overview.md) | [查看](docs/spec/zh/01-project-overview.md) |
| 开发计划 | [查看](docs/plan/en/01-mvp-plan.md) | [查看](docs/plan/zh/01-mvp-plan.md) |
| 开发日志 | [查看](docs/log/en/01-dev-log.md) | [查看](docs/log/zh/01-dev-log.md) |

## 📁 项目结构

```
cs336-tutor-app/
├── app/                          # Android 应用模块
│   └── src/main/
│       ├── java/com/cs336/tutor/ # Kotlin 源码
│       │   ├── ui/               # Compose UI 屏幕
│       │   ├── domain/           # 业务逻辑
│       │   ├── data/             # 数据层（Room、API）
│       │   └── di/               # 依赖注入
│       ├── python/               # Python 脚本（Chaquopy）
│       └── res/                  # 资源文件
├── docs/                         # 双语文档
│   ├── spec/                     # 设计规格
│   ├── plan/                     # 开发计划
│   ├── log/                      # 开发日志
│   └── api/                      # API 集成文档
├── build.gradle.kts              # 根构建文件
├── settings.gradle.kts           # 项目设置
└── README.md                     # 本文件
```

## 🧪 技术栈

- **语言**：Kotlin 2.x
- **UI**：Jetpack Compose + Material 3
- **架构**：MVVM + Clean Architecture
- **Python 运行时**：[Chaquopy](https://chaquo.com/chaquopy/)（嵌入 CPython）
- **本地存储**：Room 数据库
- **网络**：OkHttp + Retrofit
- **DI**：Hilt

## 📄 许可

MIT

---

> 用 ❤️ 和 OpenCode 构建
