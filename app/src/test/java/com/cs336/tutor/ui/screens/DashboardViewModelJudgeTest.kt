package com.cs336.tutor.ui.screens

import com.cs336.tutor.domain.engine.TutorEngine
import com.cs336.tutor.domain.model.ComponentSpec
import com.cs336.tutor.domain.model.JudgeResult
import com.cs336.tutor.domain.model.TutorComponent
import com.cs336.tutor.domain.provider.ExplanationChunk
import com.cs336.tutor.domain.provider.LLMProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class DashboardViewModelJudgeTest {

    @Test
    fun `JudgeResult data class works`() {
        val r = JudgeResult(0.85f, true, "Great job!", suggestions = listOf("tip1"))
        assertEquals(0.85f, r.score)
        assertTrue(r.passed)
        assertEquals("Great job!", r.feedback)
        assertEquals(1, r.suggestions.size)
    }

    @Test
    fun `DashboardUiState has judge fields`() {
        val r = JudgeResult(1f, true, "ok")
        val state = DashboardUiState(
            components = emptyList(),
            isLoading = false,
            judgeResult = r,
            isJudging = false
        )
        assertNotNull(state.judgeResult)
        assertFalse(state.isJudging)
    }

    @Test
    fun `companion object accepts LLMProvider`() {
        val provider = object : LLMProvider {
            override val name = "test"
            override suspend fun explain(cId: String, lines: List<String>) = flowOf(ExplanationChunk("test"))
            override suspend fun judge(cId: String, u: String, e: String) = JudgeResult(1f, true, "ok")
            override suspend fun answer(q: String, c: String) = flowOf(ExplanationChunk("ok"))
            override suspend fun generateComponent(s: ComponentSpec) = TutorComponent("", "", "", false)
            override suspend fun judgeAssignment(components: Map<String, String>, question: String) =
                JudgeResult(0.9f, true, "Assignment looks good!")
        }
        DashboardViewModel.llmProvider = provider
        assertNotNull(DashboardViewModel.llmProvider)
        assertEquals("test", DashboardViewModel.llmProvider?.name)
    }
}
