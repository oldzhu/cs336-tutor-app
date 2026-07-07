package com.cs336.tutor.ui.screens

import com.cs336.tutor.domain.engine.TutorEngine
import com.cs336.tutor.domain.model.*
import com.cs336.tutor.domain.provider.ExplanationChunk
import com.cs336.tutor.domain.provider.LLMProvider
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class ChatHistoryTest {

    data class ChatMessage(val role: String, val content: String, val timestamp: Long = System.currentTimeMillis())

    @Test
    fun `chat message has role and content`() {
        val msg = ChatMessage("user", "hello")
        assertEquals("user", msg.role)
        assertEquals("hello", msg.content)
    }

    @Test
    fun `chat messages list starts empty`() {
        val messages: List<ChatMessage> = emptyList()
        assertTrue(messages.isEmpty())
    }

    @Test
    fun `adding message to list preserves history`() {
        val messages = mutableListOf<ChatMessage>()
        messages.add(ChatMessage("user", "Q1: What is BPE?"))
        messages.add(ChatMessage("assistant", "A1: BPE pairs..."))
        assertEquals(2, messages.size)
        assertEquals("user", messages[0].role)
        assertEquals("assistant", messages[1].role)
    }

    @Test
    fun `asked question adds user and assistant messages`() = runBlocking {
        var history = listOf<ChatMessage>()
        val provider = object : LLMProvider {
            override val name = "test"
            override suspend fun explain(cId: String, lines: List<String>) = flowOf(ExplanationChunk(""))
            override suspend fun judge(cId: String, u: String, e: String) = JudgeResult(1f, true, "")
            override suspend fun answer(q: String, c: String) = flowOf(ExplanationChunk("Answer: $q", true))
            override suspend fun generateComponent(s: ComponentSpec) = TutorComponent("","","",false)
            override suspend fun judgeAssignment(c: Map<String, String>, q: String) = JudgeResult(1f, true, "")
        }

        val answer = mutableListOf<ExplanationChunk>()
        provider.answer("What is BPE?", "context").collect { answer.add(it) }

        history = history + ChatMessage("user", "What is BPE?")
        history = history + ChatMessage("assistant", answer.joinToString("") { it.text })

        assertEquals(2, history.size)
        assertEquals("user", history[0].role)
        assertEquals("assistant", history[1].role)
        assertTrue(history[1].content.contains("Answer"))
    }

    @Test
    fun `history accumulates across multiple questions`() {
        val history = mutableListOf<ChatMessage>()
        history.add(ChatMessage("user", "Q1"))
        history.add(ChatMessage("assistant", "A1"))
        history.add(ChatMessage("user", "Q2"))
        history.add(ChatMessage("assistant", "A2"))

        assertEquals(4, history.size)
        assertEquals("Q1", history[0].content)
        assertEquals("A1", history[1].content)
        assertEquals("Q2", history[2].content)
        assertEquals("A2", history[3].content)
    }
}
