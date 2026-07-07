package com.cs336.tutor.ui.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs336.tutor.data.remote.DeepSeekLLMProvider
import com.cs336.tutor.domain.engine.TutorEngine
import com.cs336.tutor.domain.model.JudgeResult
import com.cs336.tutor.domain.model.TutorComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val components: List<TutorComponent> = emptyList(),
    val isLoading: Boolean = true,
    val judgeResult: JudgeResult? = null,
    val isJudging: Boolean = false
)

private val NAMES_ZH = mapOf(
    "bpe" to "BPE 分词器", "embedding" to "Embedding 嵌入层", "rmsnorm" to "RMSNorm",
    "rope" to "RoPE 旋转位置编码", "attention" to "多头自注意力", "ffn" to "SwiGLU 前馈网络",
    "transformer" to "Transformer 块", "lmhead" to "LM Head 输出头", "optimizer" to "Adam 优化器",
    "training" to "训练循环",
    "fullreview" to "完整代码审查"
)
private val DESC_ZH = mapOf(
    "bpe" to "Byte-Pair Encoding", "embedding" to "Token 嵌入层", "rmsnorm" to "RMS 归一化",
    "rope" to "旋转位置编码", "attention" to "因果多头注意力", "ffn" to "SwiGLU 前馈网络",
    "transformer" to "完整 Transformer 块", "lmhead" to "LM Head 输出头", "optimizer" to "Adam 优化器",
    "training" to "训练循环",
    "fullreview" to "完整代码审查"
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val tutorEngine: TutorEngine,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init { loadComponents() }
    fun refresh() { loadComponents() }

    private fun loadComponents() {
        viewModelScope.launch {
            val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
            val isZh = prefs.getString("language", "en") == "zh"
            tutorEngine.components.collect { comps ->
                val localized = if (isZh) comps.map { c ->
                    c.copy(name = NAMES_ZH[c.id] ?: c.name, description = DESC_ZH[c.id] ?: c.description)
                } else comps
                _uiState.value = DashboardUiState(components = localized, isLoading = false)
            }
        }
    }

    fun onJudgeAssignment() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isJudging = true)
            try {
                val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
                val apiKey = (prefs.getString("api_key", "") ?: "").filter { !it.isWhitespace() }
                val result = if (apiKey.isNotEmpty() && apiKey.startsWith("sk-")) {
                    val provider = DeepSeekLLMProvider(context)
                    val comps = _uiState.value.components
                    val map = comps.associate { it.id to it.description }
                    provider.judgeAssignment(map, "Evaluate Assignment 1")
                } else {
                    JudgeResult(0.85f, true, "Mock: " + _uiState.value.components.size + " components look good!")
                }
                _uiState.value = _uiState.value.copy(judgeResult = result, isJudging = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    judgeResult = JudgeResult(0f, false, e.message ?: "Error"), isJudging = false)
            }
        }
    }

    fun onJudgeResultDismissed() {
        _uiState.value = _uiState.value.copy(judgeResult = null)
    }
}
