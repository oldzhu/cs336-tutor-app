package com.cs336.tutor.data.remote

import com.cs336.tutor.domain.model.*
import com.cs336.tutor.domain.provider.ExplanationChunk
import com.cs336.tutor.domain.provider.LLMProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class LocalLLMProviderTest {

    @Test
    fun `provider name is Local`() {
        val provider = LocalLLMProvider()
        assertEquals("Local", provider.name)
    }

    @Test
    fun `judge returns mock without model`() = runBlocking {
        val provider = LocalLLMProvider()
        val result = provider.judge("bpe", "code", "expected")
        assertTrue(result.passed)
        assertTrue(result.feedback.isNotEmpty())
    }

    @Test
    fun `answer returns explanation chunk`() = runBlocking {
        val provider = LocalLLMProvider()
        val result = provider.answer("What is BPE?", "context").first()
        assertTrue(result.text.isNotEmpty())
    }

    @Test
    fun `judgeAssignment returns mock without model`() = runBlocking {
        val provider = LocalLLMProvider()
        val result = provider.judgeAssignment(mapOf("bpe" to "code"), "test")
        assertTrue(result.feedback.isNotEmpty())
    }

    @Test
    fun `explain returns flow of chunks`() = runBlocking {
        val provider = LocalLLMProvider()
        val chunks = provider.explain("bpe", listOf("import re")).toList()
        assertTrue(chunks.isNotEmpty())
        assertTrue(chunks[0].text.isNotEmpty())
    }

    @Test
    fun `generateComponent returns tutor component`() = runBlocking {
        val provider = LocalLLMProvider()
        val spec = ComponentSpec("test", "Test", "desc", emptyList(), emptyList(), emptyList(), emptyList())
        val component = provider.generateComponent(spec)
        assertEquals("test", component.id)
    }
}
