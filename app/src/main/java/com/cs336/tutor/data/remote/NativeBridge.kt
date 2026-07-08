package com.cs336.tutor.data.remote

/**
 * JNI bridge to native llama.cpp inference engine.
 * Methods map to C++ functions in llama_jni.cpp.
 */
object NativeBridge {
    
    private var loaded = false
    
    init {
        try {
            System.loadLibrary("llama")
            loaded = true
        } catch (e: UnsatisfiedLinkError) {
            loaded = false
            android.util.Log.w("NativeBridge", "libllama.so not loaded: ${e.message}")
        }
    }
    
    fun isAvailable(): Boolean = loaded
    
    /** Initialize the model from GGUF file */
    external fun initModel(modelPath: String, contextSize: Int = 2048): Boolean
    
    /** Run inference on a prompt, returns generated text */
    external fun infer(prompt: String): String
    
    /** Release native resources */
    external fun freeModel()
}
