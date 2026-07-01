package com.cs336.tutor.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val isRemote: Boolean = true,
    val apiEndpoint: String = "https://api.deepseek.com/v1",
    val apiKey: String = "",
    val modelName: String = "deepseek-v4-flash",
    val localEndpoint: String = "http://localhost:11434",
    val isSaving: Boolean = false,
    val isSaved: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun onProviderChanged(isRemote: Boolean) {
        _uiState.value = _uiState.value.copy(isRemote = isRemote, isSaved = false)
    }

    fun onApiEndpointChanged(endpoint: String) {
        _uiState.value = _uiState.value.copy(apiEndpoint = endpoint, isSaved = false)
    }

    fun onApiKeyChanged(key: String) {
        _uiState.value = _uiState.value.copy(apiKey = key, isSaved = false)
    }

    fun onModelNameChanged(model: String) {
        _uiState.value = _uiState.value.copy(modelName = model, isSaved = false)
    }

    fun onLocalEndpointChanged(endpoint: String) {
        _uiState.value = _uiState.value.copy(localEndpoint = endpoint, isSaved = false)
    }

    fun onSave() {
        _uiState.value = _uiState.value.copy(isSaving = true)
        viewModelScope.launch {
            // TODO: Persist to DataStore Preferences
            // For now, just simulate save
            delay(500)
            _uiState.value = _uiState.value.copy(isSaving = false, isSaved = true)
            // Hide "saved" after 2 seconds
            delay(2000)
            _uiState.value = _uiState.value.copy(isSaved = false)
        }
    }
}
