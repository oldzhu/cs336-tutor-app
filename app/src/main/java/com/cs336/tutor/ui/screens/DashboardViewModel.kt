package com.cs336.tutor.ui.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val isLoading: Boolean = true
)

// Bilingual component name/description mapping
private val COMPONENT_NAMES_EN = mapOf(
    "bpe" to "BPE Tokenizer",
    "rmsnorm" to "RMSNorm",
    "rope" to "Rotary Position Embedding (RoPE)",
    "attention" to "Multi-Head Self-Attention",
    "ffn" to "Position-wise FFN (SwiGLU)",
    "transformer" to "Full Transformer Block"
)
private val COMPONENT_DESC_EN = mapOf(
    "bpe" to "Byte-Pair Encoding — subword tokenization from scratch",
    "rmsnorm" to "Root Mean Square Layer Normalization",
    "rope" to "Relative position encoding for transformers",
    "attention" to "Causal scaled dot-product attention",
    "ffn" to "Feed-forward network with SwiGLU activation",
    "transformer" to "Assemble all components into a decoder-only block"
)
private val COMPONENT_NAMES_ZH = mapOf(
    "bpe" to "BPE 分词器",
    "rmsnorm" to "RMSNorm",
    "rope" to "RoPE 旋转位置编码",
    "attention" to "多头自注意力",
    "ffn" to "位置前馈网络 (SwiGLU)",
    "transformer" to "完整 Transformer 块"
)
private val COMPONENT_DESC_ZH = mapOf(
    "bpe" to "Byte-Pair Encoding — 从零实现子词分词",
    "rmsnorm" to "均方根层归一化",
    "rope" to "Transformer 的相对位置编码",
    "attention" to "因果缩放点积注意力",
    "ffn" to "使用 SwiGLU 激活的前馈网络",
    "transformer" to "将所有组件组装成仅解码器 Transformer 块"
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
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
            val compNames = if (isZh) COMPONENT_NAMES_ZH else COMPONENT_NAMES_EN
            val compDescs = if (isZh) COMPONENT_DESC_ZH else COMPONENT_DESC_EN

            val components = listOf(
                TutorComponent(id = "bpe", name = compNames["bpe"]!!, description = compDescs["bpe"]!!, isLocked = false),
                TutorComponent(id = "rmsnorm", name = compNames["rmsnorm"]!!, description = compDescs["rmsnorm"]!!, isLocked = false),
                TutorComponent(id = "rope", name = compNames["rope"]!!, description = compDescs["rope"]!!, isLocked = false),
                TutorComponent(id = "attention", name = compNames["attention"]!!, description = compDescs["attention"]!!, isLocked = false),
                TutorComponent(id = "ffn", name = compNames["ffn"]!!, description = compDescs["ffn"]!!, isLocked = false),
                TutorComponent(id = "transformer", name = compNames["transformer"]!!, description = compDescs["transformer"]!!, isLocked = false)
            )
            _uiState.value = DashboardUiState(components = components, isLoading = false)
        }
    }
}
