package com.cs336.tutor.ui.screens

import androidx.lifecycle.ViewModel
import com.cs336.tutor.domain.model.JudgeResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class SplitScreenTutorUiState(
    val componentId: String = "",
    val componentName: String = "",
    val currentLine: CodeLine? = null,
    val explanation: String = "",
    val userCode: String = "",
    val judgeResult: JudgeResult? = null
)

@HiltViewModel
class SplitScreenTutorViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SplitScreenTutorUiState())
    val uiState: StateFlow<SplitScreenTutorUiState> = _uiState.asStateFlow()

    fun onCodeChange(newCode: String) {
        _uiState.value = _uiState.value.copy(userCode = newCode)
    }

    fun onJudge() {
        // TODO: Send code to LLM for judging
    }

    fun onAskQuestion(question: String) {
        // TODO: Send question to LLM
    }
}
