package com.cs336.tutor.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs336.tutor.domain.engine.TutorEngine
import com.cs336.tutor.domain.model.TutorComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val components: List<TutorComponent> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val tutorEngine: TutorEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init { loadComponents() }

    fun refresh() { loadComponents() }

    private fun loadComponents() {
        viewModelScope.launch {
            tutorEngine.components.collect { comps ->
                _uiState.value = DashboardUiState(components = comps, isLoading = false)
            }
        }
    }
}
