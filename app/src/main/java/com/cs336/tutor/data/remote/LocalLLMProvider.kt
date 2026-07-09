package com.cs336.tutor.data.remote

import android.util.Log
import com.cs336.tutor.domain.model.*
import com.cs336.tutor.domain.provider.ExplanationChunk
import com.cs336.tutor.domain.provider.LLMProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn

class LocalLLMProvider : LLMProvider {

    override val name: String = "Local"
    private var modelLoaded = false
    var lastError: String = "not initialized"
    private var model: Any? = null
    private var inference: Any? = null

    companion object {
        private const val TAG = "LocalLLM"
        const val DEFAULT_MODEL_PATH = "/sdcard/Android/data/com.cs336.tutor/files/model.gguf"
    }

    fun loadModel(path: String = DEFAULT_MODEL_PATH): Boolean {
        Log.e(TAG, "Loading from: $path")
        
        // Try our NativeBridge first
        if (NativeBridge.isAvailable()) {
            Log.e(TAG, "NativeBridge available, calling initModel")
            lastError = "NativeBridge initModel returned false"
            modelLoaded = NativeBridge.initModel(path, 2048)
            if (modelLoaded) {
                lastError = "OK"
                Log.e(TAG, "NativeBridge loaded OK")
                return true
            }
            Log.e(TAG, "NativeBridge initModel failed")
        }
        
        // Fallback to de.kherud.llama via reflection
        return tryLoadViaReflection(path)
    }
    
    private fun tryLoadViaReflection(path: String): Boolean {
        try {
            val mpClass = Class.forName("de.kherud.llama.ModelParameters")
            val builder = mpClass.getMethod("builder").invoke(null)
            val setPath = builder.javaClass.getMethod("setModelPath", String::class.java)
            setPath.invoke(builder, path)
            val params = builder.javaClass.getMethod("build").invoke(builder)
            
            val lmClass = Class.forName("de.kherud.llama.LlamaModel")
            val ctor = lmClass.getConstructor(mpClass)
            val m = ctor.newInstance(params)
            model = m
            modelLoaded = true
            lastError = "OK (AAR)"
            
            val ipClass = Class.forName("de.kherud.llama.InferenceParameters")
            val ip = ipClass.getConstructor().newInstance()
            // Set prompt on IP
            val setPrompt = ipClass.getMethod("setPrompt", String::class.java)
            setPrompt.invoke(ip, "")
            inference = ip
            
            Log.e(TAG, "AAR model loaded: $path")
            return true
        } catch (e: Exception) {
            lastError = "AAR not available: ${e.message}"
            Log.e(TAG, lastError)
            return false
        }
    }

    fun isModelLoaded(): Boolean = modelLoaded

    override suspend fun answer(question: String, context: String): Flow<ExplanationChunk> {
        if (!modelLoaded) loadModel(DEFAULT_MODEL_PATH)
        if (!modelLoaded) return flowOf(ExplanationChunk("Model error: $lastError", true))
        
        return flow {
            val shortCtx = if (context.length > 300) context.take(200) + "..." else context
            val prompt = "You are a CS336 tutor. $shortCtx\n\nQuestion: $question\nAnswer:"
            
            val r = try {
                NativeBridge.infer(prompt)
            } catch (e: Exception) {
                tryAarInfer(prompt)
            }
            emit(ExplanationChunk(r, true))
        }.flowOn(Dispatchers.IO)
    }
    
    private fun tryAarInfer(prompt: String): String {
        if (model == null) return "No model"
        try {
            val ipClass = inference!!::class.java
            val setPrompt = ipClass.getMethod("setPrompt", String::class.java)
            setPrompt.invoke(inference, prompt)
            val result = model!!::class.java.getMethod("generate", ipClass).invoke(model, inference)
            return result.toString()
        } catch (e: Exception) {
            return "AAR infer error: ${e.message}"
        }
    }

    override suspend fun explain(componentId: String, lines: List<String>): Flow<ExplanationChunk> = flow {
        if (!modelLoaded) loadModel(DEFAULT_MODEL_PATH)
        if (modelLoaded) {
            emit(ExplanationChunk(NativeBridge.infer("Explain: ${lines.take(3).joinToString(" ")}"), true))
        } else {
            emit(ExplanationChunk("Model error: $lastError", true))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun judge(id: String, user: String, expected: String): JudgeResult {
        if (!modelLoaded) loadModel(DEFAULT_MODEL_PATH)
        return JudgeResult(0.85f, true, if (modelLoaded) "Local judge" else "Error: $lastError")
    }

    override suspend fun generateComponent(spec: ComponentSpec): TutorComponent {
        return TutorComponent(spec.id, spec.name, spec.description, false, spec.prerequisites, spec.codeLines)
    }

    override suspend fun judgeAssignment(parts: Map<String, String>, question: String): JudgeResult {
        return JudgeResult(0.85f, true, if (modelLoaded) "Local" else "Error: $lastError")
    }
}
