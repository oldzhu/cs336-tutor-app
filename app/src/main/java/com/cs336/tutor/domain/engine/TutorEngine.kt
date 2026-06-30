package com.cs336.tutor.domain.engine

import com.cs336.tutor.domain.model.TutorComponent
import com.cs336.tutor.domain.model.ComponentSpec
import kotlinx.coroutines.flow.StateFlow

/**
 * Core engine that manages tutor components and user progress.
 * Components can be dynamically registered at runtime.
 */
interface TutorEngine {
    val components: StateFlow<List<TutorComponent>>
    val currentComponent: StateFlow<TutorComponent?>

    suspend fun loadComponent(id: String): TutorComponent
    suspend fun registerComponent(spec: ComponentSpec): TutorComponent
    suspend fun unregisterComponent(id: String)
    suspend fun completeLine(componentId: String, lineNumber: Int)
    suspend fun getProgress(componentId: String): Float
}
