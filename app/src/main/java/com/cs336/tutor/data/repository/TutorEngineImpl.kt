package com.cs336.tutor.data.repository

import com.cs336.tutor.domain.engine.TutorEngine
import com.cs336.tutor.domain.model.ComponentSpec
import com.cs336.tutor.domain.model.TutorComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TutorEngineImpl @Inject constructor() : TutorEngine {

    private val _components = MutableStateFlow<List<TutorComponent>>(emptyList())
    override val components: StateFlow<List<TutorComponent>> = _components.asStateFlow()

    private val _currentComponent = MutableStateFlow<TutorComponent?>(null)
    override val currentComponent: StateFlow<TutorComponent?> = _currentComponent.asStateFlow()

    override suspend fun loadComponent(id: String): TutorComponent {
        return _components.value.firstOrNull { it.id == id }
            ?: throw IllegalArgumentException("Component not found: $id")
    }

    override suspend fun registerComponent(spec: ComponentSpec): TutorComponent {
        val component = TutorComponent(
            id = spec.id,
            name = spec.name,
            description = spec.description,
            isLocked = false,
            prerequisites = spec.prerequisites,
            codeLines = spec.codeLines
        )
        _components.value = _components.value + component
        return component
    }

    override suspend fun unregisterComponent(id: String) {
        _components.value = _components.value.filter { it.id != id }
    }

    override suspend fun completeLine(componentId: String, lineNumber: Int) {
        // TODO: Update progress in Room DB
    }

    override suspend fun getProgress(componentId: String): Float {
        // TODO: Read from Room DB
        return 0f
    }
}
