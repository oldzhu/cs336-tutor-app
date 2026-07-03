package com.cs336.tutor.ui.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val isRemote: Boolean = true,
    val isChinese: Boolean = false,
    val apiEndpoint: String = "https://api.deepseek.com/v1",
    val apiKey: String = "",
    val modelName: String = "deepseek-v4-flash",
    val localEndpoint: String = "http://localhost:11434",
    val isSaving: Boolean = false,
    val isSaved: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val savedLang = prefs.getString("language", "en") ?: "en"
        _uiState.value = SettingsUiState(
            isRemote = prefs.getBoolean("is_remote", true),
            isChinese = savedLang == "zh",
            apiEndpoint = prefs.getString("api_endpoint", "https://api.deepseek.com/v1") ?: "https://api.deepseek.com/v1",
            apiKey = prefs.getString("api_key", "") ?: "",
            modelName = prefs.getString("model", "deepseek-v4-flash") ?: "deepseek-v4-flash",
            localEndpoint = prefs.getString("local_endpoint", "http://localhost:11434") ?: "http://localhost:11434"
        )
    }

    fun onLanguageChanged(isChinese: Boolean) {
        _uiState.value = _uiState.value.copy(isChinese = isChinese, isSaved = false)
    }

    fun saveLanguage() {
        val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        prefs.edit().putString("language", if (_uiState.value.isChinese) "zh" else "en").commit()
    }

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
        val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val s = _uiState.value
        prefs.edit()
            .putString("language", if (s.isChinese) "zh" else "en")
            .putBoolean("is_remote", s.isRemote)
            .putString("api_endpoint", s.apiEndpoint)
            .putString("api_key", s.apiKey)
            .putString("model", s.modelName)
            .putString("local_endpoint", s.localEndpoint)
            .commit()
        _uiState.value = _uiState.value.copy(isSaving = true)
        viewModelScope.launch {
            delay(500)
            _uiState.value = _uiState.value.copy(isSaving = false, isSaved = true)
            delay(2000)
            _uiState.value = _uiState.value.copy(isSaved = false)
        }
    }
}
