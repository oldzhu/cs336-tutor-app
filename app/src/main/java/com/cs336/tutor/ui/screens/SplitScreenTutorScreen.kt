package com.cs336.tutor.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalContext
import com.cs336.tutor.R
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.cs336.tutor.domain.engine.ComponentOverviews
import com.cs336.tutor.domain.model.CodeLineStub
import com.cs336.tutor.domain.model.ChatMessage
import com.cs336.tutor.domain.model.JudgeResult
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitScreenTutorScreen(
    componentId: String,
    onBack: () -> Unit,
    viewModel: SplitScreenTutorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

        val lang = (LocalContext.current.getSharedPreferences("app_settings", 0).getString("language", "en") ?: "en") == "zh"
    LaunchedEffect(componentId, lang) {
        viewModel.initialize(componentId)
        viewModel.loadChatHistory()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.componentName) },
                actions = { androidx.compose.material3.TextButton(onClick = { viewModel.clearChatHistory() }) { androidx.compose.material3.Text("Clear") } },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading || uiState.codeLines.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Always side-by-side split — works for both portrait and landscape
            // Each panel is independently scrollable via touch scroll
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                AIExplanationPanel(
                    modifier = Modifier.weight(1f),
                    componentId = uiState.componentId,
                    codeLines = uiState.codeLines,
                    currentLineIndex = uiState.currentLineIndex,
                    currentLine = uiState.currentLine,
                    explanation = uiState.explanation,
                    onPrevious = viewModel::previousLine,
                    onNext = viewModel::nextLine,
                    onNavigateToLine = viewModel::navigateToLine
                )
                VerticalDivider()
                CodeEditorPanel(
                    modifier = Modifier.weight(1f),
                    code = uiState.userCode,
                    onCodeChange = viewModel::onCodeChange,
                    onJudge = viewModel::onJudge,
                    onJudgeComponent = viewModel::onJudgeComponent,
                    judgeResult = uiState.judgeResult,
                    isLoading = uiState.isLoading,
                    questionText = uiState.questionText,
                    onQuestionChanged = viewModel::onQuestionChanged,
                    answerText = uiState.answerText,
                    isAnswerLoading = uiState.isAnswerLoading,
                    onAskQuestion = viewModel::onAskQuestion,
                    chatMessages = uiState.chatMessages
                )
            }
        }
    }
}

@Composable
fun AIExplanationPanel(
    modifier: Modifier = Modifier,
    componentId: String,
    codeLines: List<CodeLineStub>,
    currentLineIndex: Int,
    currentLine: CodeLine?,
    explanation: String,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onNavigateToLine: (Int) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val lineCount = codeLines.size
    val ctx = LocalContext.current
    var showAllLines by remember { mutableStateOf(false) }
    var showLineJump by remember { mutableStateOf(false) }
    var jumpLineNumber by remember { mutableStateOf("") }
    var showOverview by remember { mutableStateOf(false) }
    
    // Auto-repeat: type → pause → clear → retype loop
    var repeatTrigger by remember { mutableStateOf(0) }

    // Typing animation: retype code character by character at human speed
    var displayedCode by remember { mutableStateOf("") }
    val targetCode = currentLine?.code ?: ""
    
    LaunchedEffect(targetCode, repeatTrigger) {
        displayedCode = ""
        if (targetCode.isNotEmpty()) {
            for (i in targetCode.indices) {
                delay(80) // human typing speed
                displayedCode = targetCode.substring(0, i + 1)
            }
        }
    }
    
    LaunchedEffect(repeatTrigger, targetCode) {
        if (targetCode.isNotEmpty()) {
            delay(targetCode.length * 80L + 2000) // typing time + 2s pause
            displayedCode = ""  // clear the code
            delay(500)          // brief blank pause
            repeatTrigger++     // trigger retype
        }
    }

    Column(modifier = modifier.fillMaxHeight()) {
        // Panel header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primaryContainer,
            tonalElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.ai_tutor_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { showOverview = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Info, contentDescription = "Overview", modifier = Modifier.size(18.dp))
                    }
                    IconButton(
                        onClick = { showAllLines = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.List, contentDescription = "Show all lines", modifier = Modifier.size(18.dp))
                    }
                }
                Text(
                    text = "Line ${currentLineIndex + 1} of $lineCount",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { showLineJump = true }
                )
            }
        }

        // Line jump dialog
        if (showLineJump) {
            AlertDialog(
                onDismissRequest = { showLineJump = false },
                title = { Text("Go to line") },
                text = {
                    OutlinedTextField(
                        value = jumpLineNumber,
                        onValueChange = { if (it.all { c -> c.isDigit() }) jumpLineNumber = it },
                        label = { Text("Line number (1-$lineCount)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        val num = jumpLineNumber.toIntOrNull()
                        if (num != null && num in 1..lineCount) {
                            onNavigateToLine(num - 1)
                            showLineJump = false
                            jumpLineNumber = ""
                        }
                    }) { Text("Go") }
                },
                dismissButton = {
                    TextButton(onClick = { showLineJump = false; jumpLineNumber = "" }) { Text("Cancel") }
                }
            )
        }

        // Overview dialog
        if (showOverview) {
            val uriHandler = LocalUriHandler.current
            val overview = ComponentOverviews.getOverview(componentId)
            val isZh = (ctx.getSharedPreferences("app_settings", 0).getString("language", "en") ?: "en") == "zh"
            if (overview != null) {
                val content = if (isZh) overview.zh else overview.en
                AlertDialog(
                    onDismissRequest = { showOverview = false },
                    title = { Text(content.title, fontWeight = FontWeight.Bold) },
                    text = {
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            Text("📐 Formula / 公式", style = MaterialTheme.typography.titleSmall)
                            Spacer(Modifier.height(4.dp))
                            Text(content.formula, style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace)
                            Spacer(Modifier.height(12.dp)); Text("Purpose", style = MaterialTheme.typography.titleSmall); Spacer(Modifier.height(4.dp)); Text(content.purpose, style = MaterialTheme.typography.bodySmall); Spacer(Modifier.height(12.dp)); Text("Usage", style = MaterialTheme.typography.titleSmall); Spacer(Modifier.height(4.dp)); Text(content.usage, style = MaterialTheme.typography.bodySmall); Spacer(Modifier.height(12.dp)); Text("Without", style = MaterialTheme.typography.titleSmall); Spacer(Modifier.height(4.dp)); Text(content.without, style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.height(12.dp))
                            Text("⚙️ Algorithm / 算法", style = MaterialTheme.typography.titleSmall)
                            Spacer(Modifier.height(4.dp))
                            Text(content.algorithm, style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.height(12.dp))
                            Text("💡 Why / 为什么", style = MaterialTheme.typography.titleSmall)
                            Spacer(Modifier.height(4.dp))
                            Text(content.why, style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.height(12.dp))
                            Text("📚 References / 参考资料", style = MaterialTheme.typography.titleSmall)
                            content.references.forEach { ref ->
                                Spacer(Modifier.height(2.dp))
                                Text("• ${ref.label}", style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary)
                                Text(ref.url, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.clickable { uriHandler.openUri(ref.url) })
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showOverview = false }) { Text("OK") }
                    }
                )
            }
        }

        // Navigation row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPrevious, enabled = currentLineIndex > 0, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.ChevronLeft, contentDescription = null, modifier = Modifier.size(20.dp))
            }
            Text(text = "${currentLineIndex + 1}/$lineCount", style = MaterialTheme.typography.labelSmall)
            IconButton(onClick = onNext, enabled = currentLineIndex < lineCount - 1, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(20.dp))
            }
        }

        HorizontalDivider()

        // Scrollable content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(8.dp)
        ) {
            // Animated target code
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = stringResource(R.string.code_section),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = Color(0xFF1E1E1E),
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = displayedCode.ifEmpty { "▊" },
                            modifier = Modifier.padding(10.dp),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = Color(0xFFD4D4D4),
                            lineHeight = 18.sp
                        )
                    }
                    if (displayedCode.length < targetCode.length) {
                        Text(
                            text = "▊ typing...",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Explanation
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = stringResource(R.string.explanation_section),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = explanation.ifBlank { stringResource(R.string.navigate_hint) },
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 18.sp
                    )
                }
            }

            // Hints
            val stub = codeLines.getOrNull(currentLineIndex)
            if (stub != null && stub.hints.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(stringResource(R.string.hints_section), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        stub.hints.forEach { hint ->
                            Text("• $hint", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 2.dp))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
    
    // All-lines dialog
    if (showAllLines) {
        Dialog(onDismissRequest = { showAllLines = false }) {
            Surface(
                shape = MaterialTheme.shapes.large,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.85f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(R.string.all_lines_title), style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                        IconButton(onClick = { showAllLines = false }) {
                            Icon(Icons.Default.Close, "Close")
                        }
                    }
                    HorizontalDivider()
                    Spacer(Modifier.height(8.dp))
                    LazyColumn {
                        itemsIndexed(codeLines) { index, line ->
                            val isCurrent = index == currentLineIndex
                            Surface(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                color = if (isCurrent) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surface,
                                shape = MaterialTheme.shapes.small,
                                onClick = { showAllLines = false }
                            ) {
                                Row(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = "${line.lineNumber}",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.width(28.dp)
                                    )
                                    Text(
                                        text = line.code,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = if (isCurrent) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CodeEditorPanel(
    modifier: Modifier = Modifier,
    code: String,
    onCodeChange: (String) -> Unit,
    onJudge: () -> Unit,
    onJudgeComponent: () -> Unit = {},
    judgeResult: JudgeResult?,
    isLoading: Boolean,
    questionText: String,
    onQuestionChanged: (String) -> Unit,
    answerText: String,
    isAnswerLoading: Boolean,
    onAskQuestion: (String) -> Unit,
    chatMessages: List<ChatMessage> = emptyList(),
    onClearChat: () -> Unit = {}
) {
    var localQuestion by remember { mutableStateOf("") }
    val chatVm0: SplitScreenTutorViewModel = hiltViewModel()
    var _chatCleared by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    
    Column(
        modifier = modifier
            .fillMaxHeight()
            .verticalScroll(scrollState)
    ) {
        // Code editor header
        Surface(color = MaterialTheme.colorScheme.tertiaryContainer, tonalElevation = 2.dp) {
            Text(stringResource(R.string.your_code_title), modifier = Modifier.padding(8.dp), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }
        
        // Editor area
        OutlinedTextField(
            value = code,
            onValueChange = onCodeChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp)
                .padding(8.dp),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontFamily = FontFamily.Monospace, 
                fontSize = 13.sp, 
                lineHeight = 20.sp, 
                color = Color(0xFFD4D4D4)
            ),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFF1E1E1E), 
                focusedContainerColor = Color(0xFF1E1E1E)
            )
        )
        
        // Judge Component button (evaluates ALL lines)
        OutlinedButton(
            onClick = onJudgeComponent,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
        ) {
            Text("🧪 Judge Component", style = MaterialTheme.typography.labelMedium)
        }
        Spacer(Modifier.height(4.dp))
        // Judge single-line button
        Button(
            onClick = onJudge,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
        ) {
            if (isLoading) CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
            else Text(stringResource(R.string.judge_button))
        }
        
        // Judge result
        AnimatedVisibility(visible = judgeResult != null) {
            val result = judgeResult
            if (result != null) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (result.passed) Color(0xFF1B5E20) else Color(0xFFB71C1C)
                    )
                ) {
                    Column(Modifier.padding(8.dp)) {
                        Text(
                            "Score: ${(result.score * 100).toInt()}%  ${if (result.passed) "✅ Pass" else "❌ Needs work"}",
                            color = Color.White, style = MaterialTheme.typography.labelMedium
                        )
                        Text(result.feedback, color = Color.White, style = MaterialTheme.typography.bodySmall)
                        result.suggestions.forEach { Text("→ $it", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall) }
                    }
                }
            }
        }
        
        HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
        
        // Chat history
        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.chat_history_label), style = MaterialTheme.typography.titleSmall)
            if (!_chatCleared && chatMessages.isNotEmpty()) {
                TextButton(onClick = {
                        // Clear both DB and in-memory (local) state
                        chatVm0.clearChatHistory()
                        _chatCleared = true
                    }) {
                    Text(stringResource(R.string.clear_chat), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
        if (!_chatCleared && chatMessages.isNotEmpty()) {
            Column(modifier = Modifier.padding(8.dp).heightIn(max = 300.dp).verticalScroll(rememberScrollState())) {
                chatMessages.forEach { msg ->
                    val isUser = msg.role == "user"
                    Text(
                        (if (isUser) "You: " else "AI: ") + msg.content,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
            HorizontalDivider(Modifier.padding(horizontal = 8.dp))
        }

        // Q&A section
        Column(modifier = Modifier.padding(8.dp)) {
            Text(stringResource(R.string.q_and_a_title), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = localQuestion,
                    onValueChange = { localQuestion = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(stringResource(R.string.q_and_a_placeholder)) },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodySmall
                )
                IconButton(
                    onClick = { onAskQuestion(localQuestion); localQuestion = "" },
                    enabled = localQuestion.isNotBlank()
                ) {
                    Icon(Icons.Default.Send, "Ask", tint = if (localQuestion.isNotBlank()) 
                        MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (isAnswerLoading) LinearProgressIndicator(Modifier.fillMaxWidth())

        }
    }
}

data class CodeLine(
    val lineNumber: Int,
    val code: String,
    val explanation: String
)
