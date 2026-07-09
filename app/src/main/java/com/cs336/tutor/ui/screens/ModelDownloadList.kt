package com.cs336.tutor.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cs336.tutor.data.remote.ModelScopeDownloader

@Composable
fun ModelDownloadList(context: Context, onModelSelected: (String) -> Unit) {
    Text(
        "Available Models",
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 16.dp)
    )
    Spacer(Modifier.height(8.dp))

    ModelScopeDownloader.AVAILABLE_MODELS.forEach { model ->
        val downloaded = ModelScopeDownloader.isDownloaded(context, model.id)
        var showDownload by remember { mutableStateOf(false) }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        ) {
            Column(Modifier.weight(1f)) {
                Text(model.name, fontWeight = FontWeight.Bold)
                Text("${model.size} — ${model.description}", style = MaterialTheme.typography.bodySmall)
                if (downloaded) Text("✅ Ready", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall)
            }
            Spacer(Modifier.width(8.dp))
            if (downloaded) {
                Button(onClick = {
                    val path = ModelScopeDownloader.getLocalPath(context, model.id)
                    if (path != null) onModelSelected(path)
                }, modifier = Modifier.height(36.dp)) {
                    Text("Use")
                }
            } else {
                Button(onClick = { showDownload = true }, modifier = Modifier.height(36.dp)) {
                    Text("Download")
                }
            }
        }

        if (showDownload) {
            var progress by remember { mutableStateOf(ModelScopeDownloader.DownloadProgress(model.id)) }
            LaunchedEffect(Unit) {
                ModelScopeDownloader.download(context, model).collect { progress = it }
            }
            AlertDialog(
                onDismissRequest = { showDownload = false },
                title = { Text("Downloading ${model.name}") },
                text = {
                    Column {
                        if (progress.error != null) {
                            Text("Error: ${progress.error}", color = MaterialTheme.colorScheme.error)
                        } else if (progress.percent >= 100) {
                            Text("✅ Download complete!", color = MaterialTheme.colorScheme.primary)
                        } else {
                            Text("${progress.percent}% (${progress.bytesDownloaded / 1024 / 1024} MB)")
                            LinearProgressIndicator(
                                progress = { progress.percent / 100f },
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showDownload = false }) { Text("OK") }
                }
            )
        }
    }
}
