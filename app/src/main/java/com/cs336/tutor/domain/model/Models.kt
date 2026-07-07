package com.cs336.tutor.domain.model

data class TutorComponent(
    val id: String,
    val name: String,
    val description: String,
    val isLocked: Boolean = true,
    val prerequisites: List<String> = emptyList(),
    val codeLines: List<CodeLineStub> = emptyList()
)


data class ChatMessage(
    val role: String,      // "user" or "assistant"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class CodeLineStub(
    val lineNumber: Int,
    val code: String,
    val explanation: String = "",
    val explanationZh: String = "",
    val isEditable: Boolean = true,
    val hints: List<String> = emptyList()
)

data class Exercise(
    val id: String,
    val description: String,
    val expectedOutput: String,
    val judgeCriteria: List<JudgeCriterion>
)

data class JudgeCriterion(
    val description: String,
    val weight: Float = 1.0f
)

data class JudgeResult(
    val score: Float,
    val passed: Boolean,
    val feedback: String,
    val diff: String = "",
    val suggestions: List<String> = emptyList()
)

data class ComponentSpec(
    val id: String,
    val name: String,
    val description: String,
    val prerequisites: List<String>,
    val codeLines: List<CodeLineStub>,
    val exercises: List<Exercise>,
    val judgeCriteria: List<JudgeCriterion>
)
