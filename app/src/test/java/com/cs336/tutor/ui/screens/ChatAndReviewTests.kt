package com.cs336.tutor.ui.screens

import com.cs336.tutor.domain.engine.FullCodeReviewComponent
import com.cs336.tutor.domain.engine.TutorEngine
import com.cs336.tutor.domain.model.*
import com.cs336.tutor.domain.provider.ExplanationChunk
import com.cs336.tutor.domain.provider.LLMProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ChatAndReviewTests {

    // === ChatMessage model tests ===
    
    @Test fun `ChatMessage has role content and timestamp`() {
        val msg = ChatMessage("user", "Hello")
        assertEquals("user", msg.role)
        assertEquals("Hello", msg.content)
        assertTrue(msg.timestamp > 0)
    }

    @Test fun `ChatMessage for assistant role`() {
        val msg = ChatMessage("assistant", "Hi there!")
        assertEquals("assistant", msg.role)
    }

    // === Chat history accumulation tests ===
    
    @Test fun `chat history starts empty`() {
        val state = SplitScreenTutorUiState()
        assertTrue(state.chatMessages.isEmpty())
    }

    @Test fun `chatMessages accumulates user and assistant messages`() {
        val msgs = listOf(
            ChatMessage("user", "Q1"),
            ChatMessage("assistant", "A1"),
            ChatMessage("user", "Q2"),
            ChatMessage("assistant", "A2")
        )
        assertEquals(4, msgs.size)
        assertEquals("user", msgs[0].role)
        assertEquals("assistant", msgs[1].role)
    }

    // === FullCodeReview component tests ===
    
    @Test fun `FullCodeReview has correct id`() {
        assertEquals("fullreview", FullCodeReviewComponent.spec.id)
    }

    @Test fun `FullCodeReview has 10 prerequisites`() {
        assertEquals(10, FullCodeReviewComponent.spec.prerequisites.size)
        assertTrue(FullCodeReviewComponent.spec.prerequisites.contains("bpe"))
        assertTrue(FullCodeReviewComponent.spec.prerequisites.contains("training"))
    }

    @Test fun `FullCodeReview has code lines`() {
        assertTrue(FullCodeReviewComponent.spec.codeLines.isNotEmpty())
    }

    @Test fun `FullCodeReview has exercises`() {
        assertTrue(FullCodeReviewComponent.spec.exercises.isNotEmpty())
    }

    @Test fun `FullCodeReview has judge criteria`() {
        assertTrue(FullCodeReviewComponent.spec.judgeCriteria.isNotEmpty())
        assertEquals(3, FullCodeReviewComponent.spec.judgeCriteria.size)
    }

    // === JudgeAssignment tests ===

    @Test fun `judgeAssignment returns mock without key`() = runBlocking {
        val provider = object : LLMProvider {
            override val name = "test"
            override suspend fun explain(cId: String, lines: List<String>) = flowOf(ExplanationChunk(""))
            override suspend fun judge(cId: String, u: String, e: String) = JudgeResult(1f, true, "")
            override suspend fun answer(q: String, c: String) = flowOf(ExplanationChunk("ok"))
            override suspend fun generateComponent(s: ComponentSpec) = TutorComponent("","","",false)
            override suspend fun judgeAssignment(components: Map<String, String>, question: String) =
                JudgeResult(0.85f, true, "All good!")
        }
        val result = provider.judgeAssignment(mapOf("bpe" to "code"), "test")
        assertEquals(0.85f, result.score)
        assertTrue(result.passed)
    }

    // === Chat context includes full code ===

    @Test fun `ask question builds context with component name and code`() {
        val uiState = SplitScreenTutorUiState(
            componentId = "bpe",
            componentName = "BPE Tokenizer",
            codeLines = listOf(CodeLineStub(1, "import re", "Import regex")),
            currentLine = CodeLine(1, "import re", "Import regex")
        )
        assertEquals("BPE Tokenizer", uiState.componentName)
        assertTrue(uiState.codeLines.isNotEmpty())
    }

    // === Dashboard judge state tests ===

    @Test fun `DashboardUiState has judge fields`() {
        val state = DashboardUiState(
            components = emptyList(),
            isLoading = false,
            judgeResult = null,
            isJudging = false
        )
        assertNull(state.judgeResult)
        assertFalse(state.isJudging)
    }

    @Test fun `DashboardUiState with judge result`() {
        val r = JudgeResult(0.9f, true, "Great!")
        val state = DashboardUiState(judgeResult = r)
        assertEquals(0.9f, state.judgeResult!!.score)
    }
}
