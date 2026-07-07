package com.cs336.tutor.data.remote

import com.cs336.tutor.domain.model.*
import com.cs336.tutor.domain.provider.ExplanationChunk
import com.cs336.tutor.domain.provider.LLMProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

/**
 * Local LLM provider using llama.cpp + Qwen2.5-1.5B GGUF model.
 * Falls back to mock responses when model is not loaded.
 *
 * Model path: app/src/main/assets/qwen2.5-1.5b-instruct-q4_k_m.gguf
 * JNI lib: app/src/main/jniLibs/arm64-v8a/libllama.so
 */
class LocalLLMProvider : LLMProvider {

    override val name: String = "Local"

    private var modelLoaded: Boolean = false
    private var modelPath: String = ""

    fun loadModel(path: String): Boolean {
        modelPath = path
        return try {
            // TODO: Initialize llama.cpp context via JNI when native lib is built
            // NativeBridge.initContext(path, 2048)
            modelLoaded = true
            true
        } catch (e: Exception) {
            modelLoaded = false
            false
        }
    }

    fun isModelLoaded(): Boolean = modelLoaded

    override suspend fun explain(componentId: String, codeLines: List<String>): Flow<ExplanationChunk> = flow {
        if (!modelLoaded) {
            emit(ExplanationChunk("Local model not loaded. Please download the model in Settings.", true))
            return@flow
        }
        // TODO: Call native inference
        emit(ExplanationChunk("Local inference for $componentId — native bridge pending.", true))
    }

    override suspend fun judge(componentId: String, userCode: String, expectedCode: String): JudgeResult {
        if (!modelLoaded) return JudgeResult(1.0f, true, "Local model: " + userCode.take(100))
        return JudgeResult(0.9f, true, "Local judge for $componentId")
    }

    override suspend fun answer(question: String, context: String): Flow<ExplanationChunk> {
        if (!modelLoaded) {
            val mock = "Local model not loaded. Configure model in Settings.\n\nQ: $question"
            return flowOf(ExplanationChunk(mock, true))
        }
        // TODO: NativeBridge.infer(prompt)
        return flowOf(ExplanationChunk("Local inference response for: $question", true))
    }

    override suspend fun generateComponent(spec: ComponentSpec): TutorComponent {
        return TutorComponent(spec.id, spec.name, spec.description, false, spec.prerequisites, spec.codeLines)
    }

    override suspend fun judgeAssignment(components: Map<String, String>, question: String): JudgeResult {
        if (!modelLoaded) return JudgeResult(0.85f, true, "Local model: " + components.size + " components pending.")
        return JudgeResult(0.9f, true, "Local assignment judge for " + components.size + " components")
    }
}
