# Project Specification: CS336 Tutor Android App

> **English** · [中文版](../zh/01-project-overview.md)
> Status: Draft v0.1

## 1. Project Vision

An Android application that uses a Large Language Model (LLM) as an interactive tutor to guide users through learning and practicing **Stanford CS336: Language Modeling from Scratch — Assignment 1 (Basics)**.

The app provides a split-screen interface where:
- **Left panel**: AI explains and demonstrates code line-by-line for each component of a decoder-only transformer pipeline
- **Right panel**: User follows along writing code, with AI-powered judging, debugging, and Q&A

## 2. Core Principles

1. **Learn by doing** — not just reading, but writing every line
2. **From scratch** — no black boxes; build every component from the ground up
3. **Extensible** — start with classic decoder-only transformer, dynamically add MoE, MLA, DeepSeek V4, etc.
4. **Switchable AI** — support both remote (OpenAI, Anthropic, OpenRouter) and local (Ollama, llama.cpp) LLM backends
5. **Bilingual** — full documentation in Chinese and English

## 3. Target Users

- Students taking CS336 or similar courses
- Self-learners wanting to understand LLMs from the ground up
- Developers transitioning into AI/ML engineering

## 4. Technical Architecture

```
┌──────────────────────────────────────────────────────────┐
│                    CS336 Tutor App                        │
├─────────────────────────────┬────────────────────────────┤
│  Presentation Layer         │  Domain Layer              │
│  ┌───────────────────────┐  │  ┌──────────────────────┐  │
│  │ Splash → Onboarding   │  │  │ TutorEngine           │  │
│  │ Dashboard             │  │  │ ├─ Component Registry │  │
│  │ SplitScreenTutor      │  │  │ ├─ LLM Abstraction   │  │
│  │  ├─ AIExplanationPanel│  │  │ ├─ Code Judge        │  │
│  │  ├─ CodeEditorPanel   │  │  │ ├─ Progress Tracker  │  │
│  │  ├─ Q&AOverlay        │  │  │ └─ Exercise Engine   │  │
│  │  └─ StatusBar         │  │  └──────────────────────┘  │
│  └───────────────────────┘  │                            │
│  Data Layer                 │  Infrastructure Layer      │
│  ┌───────────────────────┐  │  ┌──────────────────────┐  │
│  │ Room DB (SQLite)      │  │  │ Chaquopy (Python)    │  │
│  │  ├─ Progress          │  │  │ OkHttp + Retrofit    │  │
│  │  ├─ Code Snapshots    │  │  │ LLM Provider SDKs    │  │
│  │  └─ Q&A History       │  │  └──────────────────────┘  │
│  └───────────────────────┘  │                            │
└─────────────────────────────┴────────────────────────────┘
```

## 5. LLM Provider Abstraction

All LLM interactions go through a unified `LLMProvider` interface:

```kotlin
interface LLMProvider {
    suspend fun explain(component: String, context: String): Flow<String>
    suspend fun judge(code: String, expected: String): JudgeResult
    suspend fun answer(question: String, context: String): Flow<String>
    suspend fun generateComponent(spec: ComponentSpec): ComponentTutor
}
```

Supported providers:
- **Remote**: OpenAI, Anthropic, OpenRouter, custom OpenAI-compatible endpoints
- **Local**: Ollama, llama.cpp (via local HTTP endpoint)

## 6. Tutor Component Architecture

Each component (BPE Tokenizer, Attention, MLP, etc.) follows this contract:

```kotlin
data class TutorComponent(
    val id: String,
    val name: String,
    val description: String,
    val prerequisites: List<String>,
    val codeLines: List<CodeLine>,
    val exercises: List<Exercise>,
    val judgeCriteria: List<JudgeCriterion>
)

data class CodeLine(
    val lineNumber: Int,
    val code: String,
    val explanation: String,     // AI-generated or pre-defined
    val isEditable: Boolean = true,
    val hints: List<String> = emptyList()
)
```

## 7. Python Code Execution (Chaquopy)

Python code entered by the user is executed via **Chaquopy** — an embedded CPython runtime inside the Android APK.

- **Scope**: CPU-only operations (tokenization, forward pass, loss computation)
- **Limitations**: No GPU/ CUDA; training demos use tiny models
- **Libraries**: Built-in stdlib + numpy (no PyTorch at runtime; architecture explanation only)

## 8. Dynamic Extensibility

Users can request the LLM to generate a new component tutor at runtime:

1. User: "Add MoE tutor module"
2. LLM generates a `ComponentSpec` JSON (code, explanations, exercises, judge criteria)
3. App validates and registers the new component in the Component Registry
4. New component appears in the dashboard — no app update or recompile needed

## 9. Tech Stack Summary

| Component | Technology |
|---|---|
| Language | Kotlin 2.x |
| UI Framework | Jetpack Compose + Material 3 |
| Architecture | MVVM + Clean Architecture |
| Python Runtime | Chaquopy SDK |
| Local Storage | Room Database |
| Networking | OkHttp + Retrofit |
| DI | Hilt / Koin |
| Build System | Gradle + Kotlin DSL |

## 10. Development Phases

See [Development Plan](../plan/en/01-mvp-plan.md) for detailed milestones.

---

> 🔗 **Related Docs**
> - [Development Plan (English)](../plan/en/01-mvp-plan.md)
> - [Development Plan (中文)](../plan/zh/01-mvp-plan.md)
> - [README](../../../README.md)
