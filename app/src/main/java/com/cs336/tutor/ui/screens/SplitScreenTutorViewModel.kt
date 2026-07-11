package com.cs336.tutor.ui.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs336.tutor.domain.engine.ComponentExplanationsZh
import com.cs336.tutor.domain.engine.TutorEngine
import com.cs336.tutor.data.local.dao.ChatMessageDao
import com.cs336.tutor.data.local.entity.ChatMessageEntity
import com.cs336.tutor.domain.model.ChatMessage
import kotlinx.coroutines.launch
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
    val isAnswerLoading: Boolean = false,
    val chatMessages: List<ChatMessage> = emptyList(),
    val chatHistoryText: String = "",
    val chatCleared: Boolean = false
)

@HiltViewModel
class SplitScreenTutorViewModel @Inject constructor(
    private val tutorEngine: TutorEngine,
    private val llmProvider: LLMProvider,
    private val chatMessageDao: ChatMessageDao,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplitScreenTutorUiState())
    val uiState: StateFlow<SplitScreenTutorUiState> = _uiState.asStateFlow()

    private var allCodeLines: List<CodeLineStub> = emptyList()
    private var isChinese: Boolean = false
    private val userCodeMap = mutableMapOf<Int, String>()

    fun initialize(componentId: String) {
        val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val currentLang = prefs.getString("language", "en") ?: "en"
        val langChanged = (currentLang == "zh") != isChinese
        if (_uiState.value.componentId == componentId && allCodeLines.isNotEmpty() && !langChanged) return
        _uiState.value = _uiState.value.copy(componentId = componentId, isLoading = true, chatHistoryText = "")
        isChinese = currentLang == "zh"
        viewModelScope.launch {
            try {
                val component = tutorEngine.loadComponent(componentId)
                allCodeLines = if (componentId == "fullreview") {
                    assembleFullCode()
                } else {
                    component.codeLines
                }
                if (isChinese) {
                    val zhExps = ComponentExplanationsZh.getExplanations(componentId)
                    val zhHints = ComponentExplanationsZh.getHints(componentId)
                    allCodeLines = allCodeLines.map { line ->
                        val zhExp = zhExps[line.lineNumber]
                        val zhHint = zhHints[line.lineNumber]
                        val newLine = if (zhExp != null) line.copy(explanationZh = zhExp) else line
                        if (zhHint != null) newLine.copy(hints = zhHint) else newLine
                    }
                }
                _uiState.value = _uiState.value.copy(componentId = componentId, componentName = component.name, codeLines = allCodeLines, isLoading = false)
                navigateToLine(0)
            } catch (e: Exception) { _uiState.value = _uiState.value.copy(isLoading = false, explanation = "Error: ${e.message}") }
        }
    }

    fun navigateToLine(index: Int) {
        if (allCodeLines.isEmpty() || index < 0 || index >= allCodeLines.size) return
        val line = allCodeLines[index]
        val expl = if (isChinese && line.explanationZh.isNotEmpty()) line.explanationZh else line.explanation
        _uiState.value = _uiState.value.copy(currentLineIndex = index, currentLine = CodeLine(lineNumber = line.lineNumber, code = line.code, explanation = expl), explanation = expl, userCode = line.code, judgeResult = null)
    }

    fun nextLine() { val nextIndex = _uiState.value.currentLineIndex + 1; if (nextIndex < allCodeLines.size) navigateToLine(nextIndex) }
    fun previousLine() { val prevIndex = _uiState.value.currentLineIndex - 1; if (prevIndex >= 0) navigateToLine(prevIndex) }
    fun onCodeChange(newCode: String) { _uiState.value = _uiState.value.copy(userCode = newCode); userCodeMap[_uiState.value.currentLineIndex] = newCode }
    fun onQuestionChanged(question: String) { _uiState.value = _uiState.value.copy(questionText = question) }

    fun onAskQuestion(question: String) {
        val q = question.trim()
        if (q.isBlank()) return
        val userMsg = ChatMessage("user", q)
        val msgs = _uiState.value.chatMessages + userMsg
        _uiState.value = _uiState.value.copy(isAnswerLoading = true, answerText = "", chatCleared = false, chatMessages = msgs)
        viewModelScope.launch {
            chatMessageDao.insert(ChatMessageEntity(componentId = _uiState.value.componentId, role = "user", content = q))
        }
        viewModelScope.launch {
            try {
                // Build full context: all code + current line + chat history
                val fullCode = allCodeLines.joinToString("\n") { "${it.lineNumber}: ${it.code}" }
                val currentLine = _uiState.value.currentLine?.let { "Line ${it.lineNumber}: ${it.code}" } ?: ""
                val history = _uiState.value.chatMessages.takeLast(10).joinToString("\n") { "${it.role}: ${it.content.take(200)}" }
                val ctx = "Component: ${_uiState.value.componentName}\nFull code:\n$fullCode\n\nCurrent: $currentLine\n\nChat history:\n$history"
                val flow = llmProvider.answer(question = q, context = ctx)
                var answer = ""
                flow.collect { chunk ->
                    answer += chunk.text
                    _uiState.value = _uiState.value.copy(answerText = answer, isAnswerLoading = !chunk.isComplete)
                    if (chunk.isComplete) {
                        val assistantMsg = ChatMessage("assistant", answer)
                        _uiState.value = _uiState.value.copy(chatMessages = _uiState.value.chatMessages + assistantMsg)
                        viewModelScope.launch {
                            chatMessageDao.insert(ChatMessageEntity(componentId = _uiState.value.componentId, role = "assistant", content = answer))
                        }
                    }
                }
            } catch (e: Exception) {
                val errMsg = ChatMessage("assistant", "Error: ${e.message}")
                _uiState.value = _uiState.value.copy(answerText = "Error: ${e.message}", isAnswerLoading = false, chatMessages = _uiState.value.chatMessages + errMsg)
            }
        }
    }

    fun onJudgeComponent() {
        if (userCodeMap.isEmpty()) return
        _uiState.value = _uiState.value.copy(isLoading = true, judgeResult = null)
        viewModelScope.launch {
            try {
                val fullCode = allCodeLines.mapIndexed { idx, line -> userCodeMap[idx] ?: line.code }.joinToString("\n")
                val result = llmProvider.judge(_uiState.value.componentId, fullCode, allCodeLines.joinToString("\n") { it.code })
                _uiState.value = _uiState.value.copy(judgeResult = result, isLoading = false)
            } catch (e: Exception) { _uiState.value = _uiState.value.copy(judgeResult = JudgeResult(0f, false, "Error: ${e.message}"), isLoading = false) }
        }
    }

    private suspend fun assembleFullCode(): List<com.cs336.tutor.domain.model.CodeLineStub> {
        val ids = listOf("bpe", "embedding", "rmsnorm", "rope", "attention", "ffn", "transformer", "lmhead", "optimizer", "training")
        val result = mutableListOf<com.cs336.tutor.domain.model.CodeLineStub>()
        var n = 1
        for (id in ids) {
            result.add(com.cs336.tutor.domain.model.CodeLineStub(n++, "# === $id ===", "", isEditable = false))
            try {
                val c = tutorEngine.loadComponent(id)
                for (l in c.codeLines) {
                    if (!l.code.startsWith("# === ")) result.add(l.copy(lineNumber = n++))
                }
            } catch (_: Exception) {}
        }
        return result
    }

    fun onJudge() {
        val state = _uiState.value
        if (state.userCode.isBlank()) return
        _uiState.value = state.copy(isLoading = true, judgeResult = null)
        viewModelScope.launch {
            try {
                val result = llmProvider.judge(componentId = state.componentId, userCode = state.userCode, expectedCode = state.currentLine?.code ?: "")
                _uiState.value = _uiState.value.copy(judgeResult = result, isLoading = false)
            } catch (e: Exception) { _uiState.value = _uiState.value.copy(judgeResult = JudgeResult(0f, false, "Judge error: ${e.message}"), isLoading = false) }
        }
    }
    fun loadChatHistory() {
        if (_uiState.value.chatCleared) return // Skip if user explicitly cleared
        viewModelScope.launch {
            val entities = chatMessageDao.getMessages(_uiState.value.componentId)
            val msgs = entities.map { ChatMessage(it.role, it.content, it.timestamp) }
            _uiState.value = _uiState.value.copy(chatMessages = msgs)
        }
    }

    fun clearChatHistory() {
        _uiState.value = _uiState.value.copy(chatMessages = emptyList(), chatCleared = true)
        viewModelScope.launch {
            chatMessageDao.clearComponent(_uiState.value.componentId)
        }
    }
}
