package com.cs336.tutor.domain.provider

import com.cs336.tutor.domain.model.ComponentSpec
import com.cs336.tutor.domain.model.JudgeResult
import com.cs336.tutor.domain.model.TutorComponent
import kotlinx.coroutines.flow.Flow

/**
 * Unified interface for all LLM providers.
 * Implementations: Remote (DeepSeek) and Local (llama.cpp via java-llama.cpp AAR)
 */
interface LLMProvider {
    /** Get the provider name/identifier */
    val name: String

    /** Generate line-by-line explanation for a component */
    suspend fun explain(componentId: String, codeLines: List<String>): Flow<ExplanationChunk>

    /** Judge user's code against expected implementation */
    suspend fun judge(
        componentId: String,
        userCode: String,
        expectedCode: String
    ): JudgeResult

    /** Answer a user's question about current context */
    suspend fun answer(question: String, context: String): Flow<ExplanationChunk>

    /** Dynamically generate a new component tutor spec */
    suspend fun generateComponent(spec: ComponentSpec): TutorComponent

    /** Judge entire assignment — all components together */
    suspend fun judgeAssignment(
        components: Map<String, String>,
        question: String
    ): JudgeResult
}

data class ExplanationChunk(
    val text: String,
    val isComplete: Boolean = false
)
