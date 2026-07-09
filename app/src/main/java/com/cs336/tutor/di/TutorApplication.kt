package com.cs336.tutor.di

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import java.io.File

@HiltAndroidApp
class TutorApplication : Application() {
    
    companion object {
        var modelPath: String = ""
    }
    
    override fun onCreate() {
        super.onCreate()
        
        val prefs = getSharedPreferences("app_settings", MODE_PRIVATE)
        val providerType = prefs.getString("llm_provider_type", "remote") ?: "remote"
        
        if (providerType == "local") {
            modelPath = prefs.getString("local_model_path",
                "/sdcard/Android/data/com.cs336.tutor/files/model.gguf") ?: "/sdcard/Android/data/com.cs336.tutor/files/model.gguf"
            
            // Try to copy to internal storage for guaranteed access
            val internalDir = filesDir
            val internalFile = File(internalDir, "model.gguf")
            
            try {
                val srcFile = File(modelPath)
                if (srcFile.exists() && srcFile.canRead() && srcFile.length() > 100_000_000) {
                    Log.e("LLM_INIT", "Model found on sdcard: ${srcFile.length()} bytes")
                    if (!internalFile.exists() || internalFile.length() != srcFile.length()) {
                        Log.e("LLM_INIT", "Copying model to internal storage...")
                        srcFile.copyTo(internalFile, overwrite = true)
                        Log.e("LLM_INIT", "Copy done: ${internalFile.length()} bytes")
                    }
                    modelPath = internalFile.absolutePath
                    Log.e("LLM_INIT", "Using internal path: $modelPath")
                } else {
                    Log.e("LLM_INIT", "sdcard model not readable, exists=${srcFile.exists()}, canRead=${srcFile.canRead()}, size=${srcFile.length()}")
                }
            } catch (e: Exception) {
                Log.e("LLM_INIT", "Copy failed: ${e.message}")
            }
        }
    }
}
