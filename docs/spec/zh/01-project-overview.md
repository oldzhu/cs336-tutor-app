# 项目规格：CS336 导师安卓应用

> **中文版** · [English](../en/01-project-overview.md)
> 状态：草案 v0.1

## 1. 项目愿景

一款安卓应用程序，利用大型语言模型（LLM）作为交互式导师，引导用户学习和实践 **Stanford CS336：从零构建语言模型——作业 1（基础）**。

应用提供分屏界面：
- **左侧面板**：AI 逐行讲解和演示仅解码器 Transformer 管道的各个组件
- **右侧面板**：用户跟随编写代码，配有 AI 判断、调试和问答功能

## 2. 核心原则

1. **做中学**——不仅仅是阅读，而是亲手编写每一行代码
2. **从零开始**——没有黑盒，从地基开始构建每个组件
3. **可扩展**——从经典仅解码器 Transformer 开始，动态添加 MoE、MLA、DeepSeek V4 等
4. **可切换 AI**——支持远程（OpenAI、Anthropic、OpenRouter）和本地（Ollama、llama.cpp）LLM 后端
5. **双语**——完整的中英文文档

## 3. 目标用户

- 修读 CS336 或类似课程的学生
- 希望从底层理解 LLM 的自学者
- 转型 AI/ML 工程的开发者

## 4. 技术架构

```
┌──────────────────────────────────────────────────────────┐
│                    CS336 导师应用                          │
├─────────────────────────────┬────────────────────────────┤
│  表现层                      │  领域层                    │
│  ┌───────────────────────┐  │  ┌──────────────────────┐  │
│  │ 启动页 → 引导页       │  │  │ TutorEngine           │  │
│  │ 仪表盘                │  │  │ ├─ 组件注册表        │  │
│  │ 分屏学习              │  │  │ ├─ LLM 抽象层       │  │
│  │  ├─ AI讲解面板        │  │  │ ├─ 代码评判器       │  │
│  │  ├─ 代码编辑器面板    │  │  │ ├─ 进度追踪器       │  │
│  │  ├─ 问答浮层          │  │  │ └─ 练习引擎         │  │
│  │  └─ 状态栏            │  │  └──────────────────────┘  │
│  └───────────────────────┘  │                            │
│  数据层                      │  基础设施层                │
│  ┌───────────────────────┐  │  ┌──────────────────────┐  │
│  │ Room 数据库 (SQLite)  │  │  │ Chaquopy (Python)    │  │
│  │  ├─ 进度              │  │  │ OkHttp + Retrofit    │  │
│  │  ├─ 代码快照          │  │  │ LLM提供商 SDK       │  │
│  │  └─ 问答历史          │  │  └──────────────────────┘  │
│  └───────────────────────┘  │                            │
└─────────────────────────────┴────────────────────────────┘
```

## 5. LLM 提供商抽象

所有 LLM 交互通过统一的 `LLMProvider` 接口进行：

```kotlin
interface LLMProvider {
    suspend fun explain(component: String, context: String): Flow<String>
    suspend fun judge(code: String, expected: String): JudgeResult
    suspend fun answer(question: String, context: String): Flow<String>
    suspend fun generateComponent(spec: ComponentSpec): ComponentTutor
}
```

支持的提供商：
- **远程**：OpenAI、Anthropic、OpenRouter、自定义 OpenAI 兼容端点
- **本地**：Ollama、llama.cpp（通过本地 HTTP 端点）

## 6. 导师组件架构

每个组件（BPE Tokenizer、Attention、MLP 等）遵循此契约：

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
    val explanation: String,
    val isEditable: Boolean = true,
    val hints: List<String> = emptyList()
)
```

## 7. Python 代码执行 (Chaquopy)

用户输入的 Python 代码通过 **Chaquopy**（嵌入 APK 的 CPython 运行时）执行。

- **范围**：仅 CPU 操作（分词、前向传播、损失计算）
- **限制**：无 GPU/CUDA；训练演示使用极小模型
- **库**：内置 stdlib + numpy（运行时无 PyTorch；仅架构讲解时提及）

## 8. 动态可扩展性

用户可要求 LLM 在运行时生成新的组件导师：

1. 用户："添加 MoE 导师模块"
2. LLM 生成 `ComponentSpec` JSON（代码、讲解、练习、评判标准）
3. 应用验证并在组件注册表中注册新组件
4. 新组件出现在仪表盘中——无需应用更新或重新编译

## 9. 技术栈总结

| 组件 | 技术 |
|---|---|
| 语言 | Kotlin 2.x |
| UI 框架 | Jetpack Compose + Material 3 |
| 架构 | MVVM + 整洁架构 |
| Python 运行时 | Chaquopy SDK |
| 本地存储 | Room 数据库 |
| 网络 | OkHttp + Retrofit |
| 依赖注入 | Hilt / Koin |
| 构建系统 | Gradle + Kotlin DSL |

## 10. 开发阶段

详细里程碑请见[开发计划](./01-mvp-plan.md)。

---

> 🔗 **相关文档**
> - [开发计划 (中文)](../plan/zh/01-mvp-plan.md)
> - [开发计划 (English)](../plan/en/01-mvp-plan.md)
> - [README](../../../README.md)
