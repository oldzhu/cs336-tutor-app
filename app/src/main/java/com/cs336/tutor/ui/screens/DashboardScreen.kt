package com.cs336.tutor.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.CheckCircle
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
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = { IconButton(onClick = onNavigateToSettings) { Icon(Icons.Default.Settings, null) } }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.onJudgeAssignment() },
                icon = { if (uiState.isJudging) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp) else Icon(Icons.Default.CheckCircle, null) },
                text = { Text(if (uiState.isJudging) "Judging..." else if (uiState.judgeResult != null) "See Score" else "Judge") }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text(stringResource(R.string.dashboard_title), style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.dashboard_subtitle), style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(16.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(uiState.components) { comp ->
                    ComponentCard(component = comp, onClick = { onNavigateToTutor(comp.id) })
                }
            }
        }
    }

    if (uiState.judgeResult != null) {
        val r = uiState.judgeResult!!
        ModalBottomSheet(
            onDismissRequest = { viewModel.onJudgeResultDismissed() },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth()
            ) {
                Text("Score: " + (r.score * 100).toInt() + "%", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(12.dp))
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(r.feedback, style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
