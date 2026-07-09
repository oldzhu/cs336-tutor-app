package com.cs336.tutor.data.remote

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

/**
 * Downloads GGUF models from ModelScope to app private storage.
 */
object ModelScopeDownloader {

    private const val TAG = "ModelDownload"
    private const val MODEL_SCOPE_BASE = "https://modelscope.cn"
    
    /** Simple model catalog — small models suitable for mobile */
    val AVAILABLE_MODELS = listOf(
        ModelEntry(
            id = "qwen2.5-1.5b",
            name = "Qwen2.5-1.5B-Instruct",
            size = "1.1 GB",
            description = "Best for mobile — Chinese + Python tutoring",
            url = "$MODEL_SCOPE_BASE/models/Qwen/Qwen2.5-1.5B-Instruct-GGUF/resolve/master/qwen2.5-1.5b-instruct-q4_k_m.gguf",
            filename = "qwen2.5-1.5b-instruct-q4_k_m.gguf"
        ),
        ModelEntry(
            id = "qwen2.5-0.5b",
            name = "Qwen2.5-0.5B-Instruct",
            size = "350 MB",
            description = "Lightweight — fast but less accurate",
            url = "$MODEL_SCOPE_BASE/models/Qwen/Qwen2.5-0.5B-Instruct-GGUF/resolve/master/qwen2.5-0.5b-instruct-q4_k_m.gguf",
            filename = "qwen2.5-0.5b-instruct-q4_k_m.gguf"
        ),
        ModelEntry(
            id = "smollm2-360m",
            name = "SmolLM2-360M-Instruct",
            size = "200 MB",
            description = "Ultra-light — basic Q&A only",
            url = "https://huggingface.co/HuggingFaceTB/SmolLM2-360M-Instruct-GGUF/resolve/main/smollm2-360m-instruct-q4_k_m.gguf",
            filename = "smollm2-360m-instruct-q4_k_m.gguf"
        )
    )

    data class ModelEntry(
        val id: String,
        val name: String,
        val size: String,
        val description: String,
        val url: String,
        val filename: String
    )

    data class DownloadProgress(
        val modelId: String,
        val bytesDownloaded: Long = 0,
        val totalBytes: Long = 0,
        val isDone: Boolean = false,
        val error: String? = null
    ) {
        val percent: Int get() = if (totalBytes > 0) ((bytesDownloaded * 100) / totalBytes).toInt() else 0
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.MINUTES)
        .build()

    fun download(context: Context, entry: ModelEntry): Flow<DownloadProgress> = flow {
        val file = File(context.filesDir, entry.filename)
        emit(DownloadProgress(entry.id, 0, 0))

        try {
            val request = Request.Builder().url(entry.url).build()
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                emit(DownloadProgress(entry.id, error = "HTTP ${response.code}"))
                return@flow
            }

            val body = response.body ?: run {
                emit(DownloadProgress(entry.id, error = "Empty response"))
                return@flow
            }

            val total = body.contentLength()
            emit(DownloadProgress(entry.id, 0, total))

            body.byteStream().use { input ->
                FileOutputStream(file).use { output ->
                    val buffer = ByteArray(8192)
                    var bytes = input.read(buffer)
                    var totalRead = 0L
                    while (bytes >= 0) {
                        output.write(buffer, 0, bytes)
                        totalRead += bytes
                        emit(DownloadProgress(entry.id, totalRead, total))
                        bytes = input.read(buffer)
                    }
                }
            }

            emit(DownloadProgress(entry.id, file.length(), file.length(), isDone = true))
            Log.i(TAG, "Downloaded ${entry.name} to ${file.absolutePath}")
        } catch (e: Exception) {
            file.delete()
            emit(DownloadProgress(entry.id, error = e.message ?: "Unknown error"))
            Log.e(TAG, "Download failed: ${e.message}")
        }
    }.flowOn(Dispatchers.IO)

    fun isDownloaded(context: Context, entryId: String): Boolean {
        val entry = AVAILABLE_MODELS.find { it.id == entryId } ?: return false
        return File(context.filesDir, entry.filename).exists()
    }

    fun getLocalPath(context: Context, entryId: String): String? {
        val entry = AVAILABLE_MODELS.find { it.id == entryId } ?: return null
        val file = File(context.filesDir, entry.filename)
        return if (file.exists()) file.absolutePath else null
    }
}
