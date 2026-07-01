package com.cs336.tutor.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.cs336.tutor.domain.model.JudgeResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitScreenTutorScreen(
    componentId: String,
    onBack: () -> Unit,
    viewModel: SplitScreenTutorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

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
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Left panel: AI explains code
            AIExplanationPanel(
                modifier = Modifier.weight(1f),
                currentLine = uiState.currentLine,
                explanation = uiState.explanation
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
                onAskQuestion = viewModel::onAskQuestion
            )
        }
    }
}

@Composable
fun AIExplanationPanel(
    modifier: Modifier = Modifier,
    currentLine: CodeLine?,
    explanation: String
) {
    // TODO: Implement line-by-line AI explanation panel
}

@Composable
fun CodeEditorPanel(
    modifier: Modifier = Modifier,
    code: String,
    onCodeChange: (String) -> Unit,
    onJudge: () -> Unit,
    judgeResult: JudgeResult?,
    onAskQuestion: (String) -> Unit
) {
    // TODO: Implement code editor with syntax highlighting
}

data class CodeLine(
    val lineNumber: Int,
    val code: String,
    val explanation: String
)
