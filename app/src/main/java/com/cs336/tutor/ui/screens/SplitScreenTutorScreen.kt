package com.cs336.tutor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.cs336.tutor.domain.model.CodeLineStub
import com.cs336.tutor.domain.model.JudgeResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitScreenTutorScreen(
    componentId: String,
    onBack: () -> Unit,
    viewModel: SplitScreenTutorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(componentId) {
        viewModel.initialize(componentId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.componentName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading && uiState.codeLines.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Left panel: AI explains code
                AIExplanationPanel(
                    modifier = Modifier.weight(1f),
                    codeLines = uiState.codeLines,
                    currentLineIndex = uiState.currentLineIndex,
                    currentLine = uiState.currentLine,
                    explanation = uiState.explanation,
                    onPrevious = viewModel::previousLine,
                    onNext = viewModel::nextLine
                )

                // Divider
                VerticalDivider()

                // Right panel: User writes code
                CodeEditorPanel(
                    modifier = Modifier.weight(1f),
                    code = uiState.userCode,
                    onCodeChange = viewModel::onCodeChange,
                    onJudge = viewModel::onJudge,
                    judgeResult = uiState.judgeResult,
                    isLoading = uiState.isLoading,
                    questionText = uiState.questionText,
                    onQuestionChanged = viewModel::onQuestionChanged,
                    answerText = uiState.answerText,
                    isAnswerLoading = uiState.isAnswerLoading,
                    onAskQuestion = viewModel::onAskQuestion
                )
            }
        }
    }
}

@Composable
fun AIExplanationPanel(
    modifier: Modifier = Modifier,
    codeLines: List<CodeLineStub>,
    currentLineIndex: Int,
    currentLine: CodeLine?,
    explanation: String,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    val scrollState = rememberScrollState()
    val lineCount = codeLines.size

    Column(modifier = modifier.fillMaxHeight()) {
        // Panel header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primaryContainer,
            tonalElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "📖 AI Explanation",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Line ${currentLine?.lineNumber ?: "?"} of $lineCount",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        // Navigation row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onPrevious,
                enabled = currentLineIndex > 0
            ) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous line")
            }
            Text(
                text = "${currentLineIndex + 1} / $lineCount",
                style = MaterialTheme.typography.labelMedium
            )
            IconButton(
                onClick = onNext,
                enabled = currentLineIndex < lineCount - 1
            ) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Next line")
            }
        }

        HorizontalDivider()

        // Scrollable content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(12.dp)
        ) {
            // Target code display
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "💻 Target Code",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF1E1E1E),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = currentLine?.code ?: "// Select a line",
                            modifier = Modifier.padding(12.dp),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            color = Color(0xFFD4D4D4),
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Explanation
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "🧠 Explanation",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = explanation.ifBlank { "Select a line to see the explanation." },
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )
                }
            }

            // Hints
            val currentStub = codeLines.getOrNull(currentLineIndex)
            if (currentStub != null && currentStub.hints.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "💡 Hints",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        currentStub.hints.forEach { hint ->
                            Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                Text("• ", fontFamily = FontFamily.Monospace)
                                Text(
                                    text = hint,
                                    style = MaterialTheme.typography.bodySmall,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun CodeEditorPanel(
    modifier: Modifier = Modifier,
    code: String,
    onCodeChange: (String) -> Unit,
    onJudge: () -> Unit,
    judgeResult: JudgeResult?,
    isLoading: Boolean,
    questionText: String,
    onQuestionChanged: (String) -> Unit,
    answerText: String,
    isAnswerLoading: Boolean,
    onAskQuestion: (String) -> Unit
) {
    Column(modifier = modifier.fillMaxHeight()) {
        // Panel header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.secondaryContainer,
            tonalElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "✏️ Your Code",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Write your implementation below",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        // Code editor area
        Column(
            modifier = Modifier
                .weight(0.45f)
                .padding(8.dp)
        ) {
            OutlinedTextField(
                value = code,
                onValueChange = onCodeChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                ),
                placeholder = {
                    Text(
                        "Write your code here...",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF1E1E1E),
                    unfocusedContainerColor = Color(0xFF1E1E1E),
                    focusedTextColor = Color(0xFFD4D4D4),
                    unfocusedTextColor = Color(0xFFD4D4D4),
                    cursorColor = Color(0xFF569CD6)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Judge button
            Button(
                onClick = onJudge,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && code.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Judge")
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        // Judge results
        Column(
            modifier = Modifier
                .weight(0.3f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = "📊 Results",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            if (judgeResult != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (judgeResult.passed)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (judgeResult.passed) Icons.Default.CheckCircle
                                else Icons.Default.Error,
                                contentDescription = null,
                                tint = if (judgeResult.passed)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Score: ${(judgeResult.score * 100).toInt()}%",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = judgeResult.feedback,
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 18.sp
                        )
                        if (judgeResult.suggestions.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Suggestions:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                            judgeResult.suggestions.forEach { suggestion ->
                                Text(
                                    text = "• $suggestion",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "Press \"Judge\" to evaluate your code.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        // Q&A section
        Column(
            modifier = Modifier
                .weight(0.25f)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "🤔 Ask a Question",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = questionText,
                    onValueChange = onQuestionChanged,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Ask about this code...", style = MaterialTheme.typography.bodySmall) },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { onAskQuestion(questionText) },
                    enabled = questionText.isNotBlank() && !isAnswerLoading
                ) {
                    if (isAnswerLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Send, contentDescription = "Ask")
                    }
                }
            }

            if (answerText.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = answerText,
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

data class CodeLine(
    val lineNumber: Int,
    val code: String,
    val explanation: String
)
