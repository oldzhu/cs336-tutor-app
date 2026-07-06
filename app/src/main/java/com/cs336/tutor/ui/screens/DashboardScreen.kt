package com.cs336.tutor.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.cs336.tutor.R
import androidx.hilt.navigation.compose.hiltViewModel
import com.cs336.tutor.ui.components.ComponentCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToTutor: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) { viewModel.refresh() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
        ) {
            Text(stringResource(R.string.dashboard_title), style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(stringResource(R.string.dashboard_subtitle), style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(uiState.components) { component ->
                    ComponentCard(component = component, onClick = { onNavigateToTutor(component.id) })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.onJudgeAssignment() },
                enabled = !uiState.isJudging,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isJudging) CircularProgressIndicator(Modifier.size(16.dp))
                else Text(stringResource(R.string.judge_assignment_button))
            }

            if (uiState.judgeResult != null) {
                val r = uiState.judgeResult!!
                Spacer(Modifier.height(8.dp))
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Score: ${(r.score * 100).toInt()}%", style = MaterialTheme.typography.titleMedium)
                        Text(r.feedback, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
