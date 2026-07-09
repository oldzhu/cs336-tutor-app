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
    val localModelPath: String = "/sdcard/models/qwen2.5-1.5b-instruct-q4_k_m.gguf",
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
        _uiState.value = _uiState.value.copy(
            isRemote = prefs.getString("llm_provider_type", "remote") == "remote",
            isChinese = prefs.getString("language", "en") == "zh",
            apiEndpoint = prefs.getString("api_endpoint", "https://api.deepseek.com/v1") ?: "https://api.deepseek.com/v1",
            apiKey = prefs.getString("api_key", "") ?: "",
            modelName = prefs.getString("model_name", "deepseek-v4-flash") ?: "deepseek-v4-flash",
            localModelPath = prefs.getString("local_model_path", 
                "/sdcard/models/qwen2.5-1.5b-instruct-q4_k_m.gguf") ?: "/sdcard/models/qwen2.5-1.5b-instruct-q4_k_m.gguf"
        )
    }

    fun onLanguageChanged(isChinese: Boolean) {
        _uiState.value = _uiState.value.copy(isChinese = isChinese, isSaved = false)
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

    fun onLocalModelPathChanged(path: String) {
        _uiState.value = _uiState.value.copy(localModelPath = path, isSaved = false)
    }

    fun saveLanguage() {
        val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        prefs.edit().putString("language", if (_uiState.value.isChinese) "zh" else "en").commit()
    }

    fun onSave() {
        val state = _uiState.value
        val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        prefs.edit()
            .putString("language", if (state.isChinese) "zh" else "en")
            .putString("llm_provider_type", if (state.isRemote) "remote" else "local")
            .putString("api_endpoint", state.apiEndpoint)
            .putString("api_key", state.apiKey)
            .putString("model_name", state.modelName)
            .putString("local_model_path", state.localModelPath)
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
