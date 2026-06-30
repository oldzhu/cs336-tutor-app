package com.cs336.tutor.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs336.tutor.domain.model.TutorComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val components: List<TutorComponent> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadComponents()
    }

    private fun loadComponents() {
        viewModelScope.launch {
            // TODO: Load from ComponentRegistry
            val mockComponents = listOf(
                TutorComponent(
                    id = "bpe",
                    name = "BPE Tokenizer",
                    description = "Byte-Pair Encoding — subword tokenization from scratch",
                    isLocked = false
                ),
                TutorComponent(
                    id = "rmsnorm",
                    name = "RMSNorm",
                    description = "Root Mean Square Layer Normalization",
                    isLocked = true
                ),
                TutorComponent(
                    id = "rope",
                    name = "Rotary Position Embedding (RoPE)",
                    description = "Relative position encoding for transformers",
                    isLocked = true
                ),
                TutorComponent(
                    id = "attention",
                    name = "Multi-Head Self-Attention",
                    description = "Causal scaled dot-product attention",
                    isLocked = true
                ),
                TutorComponent(
                    id = "ffn",
                    name = "Position-wise FFN (SwiGLU)",
                    description = "Feed-forward network with SwiGLU activation",
                    isLocked = true
                ),
                TutorComponent(
                    id = "transformer",
                    name = "Full Transformer Block",
                    description = "Assemble all components into a decoder-only block",
                    isLocked = true
                )
            )
            _uiState.value = DashboardUiState(
                components = mockComponents,
                isLoading = false
            )
        }
    }
}
