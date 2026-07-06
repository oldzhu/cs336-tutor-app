package com.cs336.tutor.domain.engine

import org.junit.Assert.*
import org.junit.Test

class EmbeddingComponentTest {
    @Test fun testId() { assertEquals("embedding", EmbeddingComponent.spec.id) }
    @Test fun testName() { assertEquals("Embedding Layer", EmbeddingComponent.spec.name) }
    @Test fun testLines() { assertTrue(EmbeddingComponent.spec.codeLines.isNotEmpty()) }
    @Test fun testUsesEmbedding() {
        val code = EmbeddingComponent.spec.codeLines.joinToString("\n") { it.code }
        assertTrue(code.contains("nn.Embedding") || code.contains("Embedding"))
    }
    @Test fun testHasParams() {
        val code = EmbeddingComponent.spec.codeLines.joinToString("\n") { it.code }
        assertTrue(code.contains("vocab_size") && code.contains("dim"))
    }
    @Test fun testExercises() { assertTrue(EmbeddingComponent.spec.exercises.isNotEmpty()) }
    @Test fun testCriteria() { assertTrue(EmbeddingComponent.spec.judgeCriteria.isNotEmpty()) }
}
