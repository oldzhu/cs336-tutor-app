package com.cs336.tutor.ui.screens

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.cs336.tutor.R
import androidx.hilt.navigation.compose.hiltViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var apiKeyVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // === Language ===
            Text(text = "Language / 语言", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                FilterChip(
                    selected = !uiState.isChinese,
                    onClick = { viewModel.onLanguageChanged(false) },
                    label = { Text(stringResource(R.string.language_en)) }
                )
                FilterChip(
                    selected = uiState.isChinese,
                    onClick = { viewModel.onLanguageChanged(true) },
                    label = { Text(stringResource(R.string.language_zh)) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // === LLM Provider Selection ===
            Text(text = "LLM Provider", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                FilterChip(
                    selected = uiState.isRemote,
                    onClick = { viewModel.onProviderChanged(true) },
                    label = { Text(stringResource(R.string.remote_provider)) }
                )
                FilterChip(
                    selected = !uiState.isRemote,
                    onClick = { viewModel.onProviderChanged(false) },
                    label = { Text(stringResource(R.string.local_provider)) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isRemote) {
                Text(stringResource(R.string.api_endpoint_label), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.apiEndpoint,
                    onValueChange = viewModel::onApiEndpointChanged,
                    label = { Text(stringResource(R.string.api_endpoint_label)) },
                    placeholder = { Text("https://api.deepseek.com/v1") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Next)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.apiKey,
                    onValueChange = viewModel::onApiKeyChanged,
                    label = { Text(stringResource(R.string.api_key_label)) },
                    placeholder = { Text("sk-...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (apiKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { apiKeyVisible = !apiKeyVisible }) {
                            Icon(if (apiKeyVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, if (apiKeyVisible) "Hide" else "Show")
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.modelName,
                    onValueChange = viewModel::onModelNameChanged,
                    label = { Text(stringResource(R.string.model_label)) },
                    placeholder = { Text("deepseek-v4-flash") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
            } else {
                Text(stringResource(R.string.local_endpoint_label), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.localEndpoint,
                    onValueChange = viewModel::onLocalEndpointChanged,
                    label = { Text(stringResource(R.string.local_endpoint_label)) },
                    placeholder = { Text("http://192.168.1.100:11434") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Done),
                    supportingText = { Text(stringResource(R.string.local_endpoint_hint)) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isSaved) {
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Text(stringResource(R.string.settings_saved), modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Button(
                onClick = {
                    viewModel.saveLanguage()
                    viewModel.onSave()
                    // Unwrap ContextWrapper to get the Activity
                    var act = context
                    while (act is android.content.ContextWrapper && act !is android.app.Activity) {
                        act = act.baseContext
                    }
                    (act as? android.app.Activity)?.recreate()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving
            ) {
                if (uiState.isSaving) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                Text(if (uiState.isSaving) "Saving..." else "Save Settings")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "After saving, the app will use the configured provider for all AI features.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
