package com.cs336.tutor.ui.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs336.tutor.domain.engine.BPEExplanationsZh
import com.cs336.tutor.domain.engine.TutorEngine
import com.cs336.tutor.domain.model.CodeLineStub
import com.cs336.tutor.domain.model.JudgeResult
import com.cs336.tutor.domain.provider.LLMProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SplitScreenTutorUiState(
    val componentId: String = "",
    val componentName: String = "BPE Tokenizer",
    val codeLines: List<CodeLineStub> = emptyList(),
    val currentLineIndex: Int = 0,
    val currentLine: CodeLine? = null,
    val explanation: String = "",
    val userCode: String = "",
    val judgeResult: JudgeResult? = null,
    val isLoading: Boolean = false,
    val questionText: String = "",
    val answerText: String = "",
    val isAnswerLoading: Boolean = false
)

@HiltViewModel
class SplitScreenTutorViewModel @Inject constructor(
    private val tutorEngine: TutorEngine,
    private val llmProvider: LLMProvider,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplitScreenTutorUiState())
    val uiState: StateFlow<SplitScreenTutorUiState> = _uiState.asStateFlow()

    private var allCodeLines: List<CodeLineStub> = emptyList()
    private var isChinese: Boolean = false

    fun initialize(componentId: String) {
        if (_uiState.value.componentId == componentId && allCodeLines.isNotEmpty()) return
        _uiState.value = _uiState.value.copy(componentId = componentId, isLoading = true)

        // Check language preference
        val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        isChinese = prefs.getString("language", "en") == "zh"

        viewModelScope.launch {
            try {
                val component = tutorEngine.loadComponent(componentId)
                allCodeLines = component.codeLines
                // Apply Chinese explanations if language is set to Chinese
                if (isChinese) {
                    allCodeLines = allCodeLines.map { line ->
                        val zhExp = BPEExplanationsZh.explanations[line.lineNumber]
                        if (zhExp != null) line.copy(explanationZh = zhExp) else line
                    }
                }
                _uiState.value = _uiState.value.copy(
                    componentId = componentId,
                    componentName = component.name,
                    codeLines = allCodeLines,
                    isLoading = false
                )
                navigateToLine(0)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    explanation = "Error loading component: ${e.message}"
                )
            }
        }
    }

    fun navigateToLine(index: Int) {
        if (allCodeLines.isEmpty() || index < 0 || index >= allCodeLines.size) return
        val line = allCodeLines[index]
        val expl = if (isChinese && line.explanationZh.isNotEmpty()) line.explanationZh else line.explanation
        _uiState.value = _uiState.value.copy(
            currentLineIndex = index,
            currentLine = CodeLine(
                lineNumber = line.lineNumber,
                code = line.code,
                explanation = expl
            ),
            explanation = expl,
            userCode = line.code,
            judgeResult = null
        )
    }

    fun nextLine() {
        val nextIndex = _uiState.value.currentLineIndex + 1
        if (nextIndex < allCodeLines.size) navigateToLine(nextIndex)
    }

    fun previousLine() {
        val prevIndex = _uiState.value.currentLineIndex - 1
        if (prevIndex >= 0) navigateToLine(prevIndex)
    }

    fun onCodeChange(newCode: String) {
        _uiState.value = _uiState.value.copy(userCode = newCode)
    }

    fun onJudge() {
        val state = _uiState.value
        if (state.userCode.isBlank()) return
        _uiState.value = state.copy(isLoading = true, judgeResult = null)
        viewModelScope.launch {
            try {
                val expectedCode = state.currentLine?.code ?: ""
                val result = llmProvider.judge(
                    componentId = state.componentId,
                    userCode = state.userCode,
                    expectedCode = expectedCode
                )
                _uiState.value = _uiState.value.copy(judgeResult = result, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    judgeResult = JudgeResult(score = 0f, passed = false,
                        feedback = "Judge error: ${e.message}",
                        suggestions = listOf("Check your code syntax", "Try again")),
                    isLoading = false
                )
            }
        }
    }

    fun onQuestionChanged(question: String) {
        _uiState.value = _uiState.value.copy(questionText = question)
    }

    fun onAskQuestion(question: String) {
        val q = question.trim()
        if (q.isBlank()) return
        _uiState.value = _uiState.value.copy(isAnswerLoading = true, answerText = "")
        viewModelScope.launch {
            try {
                val context = buildString {
                    append("Component: ${_uiState.value.componentName}\n")
                    append("Current line (${_uiState.value.currentLine?.lineNumber}): ")
                    append("${_uiState.value.currentLine?.code}\n")
                    append("User code: ${_uiState.value.userCode}\n")
                }
                val flow = llmProvider.answer(question = q, context = context)
                flow.collect { chunk ->
                    _uiState.value = _uiState.value.copy(
                        answerText = _uiState.value.answerText + chunk.text,
                        isAnswerLoading = !chunk.isComplete
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(answerText = "Error: ${e.message}", isAnswerLoading = false)
            }
        }
    }
}
