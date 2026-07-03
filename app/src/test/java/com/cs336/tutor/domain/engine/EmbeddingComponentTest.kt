package com.cs336.tutor.domain.engine

import org.junit.Assert.*
import org.junit.Test

class EmbeddingComponentTest {
    @Test fun `spec has correct id`() { assertEquals("embedding", EmbeddingComponent.spec.id) }
    @Test fun `spec name`() { assertEquals("Embedding Layer", EmbeddingComponent.spec.name) }
    @Test fun `has code lines`() { assertTrue(EmbeddingComponent.spec.codeLines.isNotEmpty()) }
    @Test fun `uses nn.Embedding`() {
        val code = EmbeddingComponent.spec.codeLines.joinToString("\n") { it.code }
        assertTrue(code.contains("nn.Embedding") || code.contains("Embedding"))
    }
    @Test fun `has vocab_size and dim params`() {
        val code = EmbeddingComponent.spec.codeLines.joinToString("\n") { it.code }
        assertTrue(code.contains("vocab_size") && code.contains("dim"))
    }
    @Test fun `has exercises`() { assertTrue(EmbeddingComponent.spec.exercises.isNotEmpty()) }
    @Test fun `has judge criteria`() { assertTrue(EmbeddingComponent.spec.judgeCriteria.isNotEmpty()) }
}
