package com.cs336.tutor.domain.engine

import org.junit.Assert.*
import org.junit.Test

class AttentionComponentTest {

    @Test fun `spec has correct id`() { assertEquals("attention", AttentionComponent.spec.id) }
    @Test fun `spec has correct name`() { assertEquals("Multi-Head Self-Attention", AttentionComponent.spec.name) }
    @Test fun `spec has code lines`() { assertTrue(AttentionComponent.spec.codeLines.isNotEmpty()) }
    @Test fun `uses Q K V projections`() {
        val code = AttentionComponent.spec.codeLines.joinToString("\n") { it.code }
        assertTrue(code.contains("Linear") || code.contains("q_proj") || code.contains("Wq"))
        assertTrue(code.contains("k") && code.contains("v") && code.contains("q"))
    }
    @Test fun `attention formula uses softmax`() {
        val code = AttentionComponent.spec.codeLines.joinToString("\n") { it.code }
        assertTrue(code.contains("softmax"))
    }
    @Test fun `uses causal mask`() {
        val code = AttentionComponent.spec.codeLines.joinToString("\n") { it.code }
        assertTrue(code.contains("mask") || code.contains("causal") || code.contains("tril"))
    }
    @Test fun `has exercises`() { assertTrue(AttentionComponent.spec.exercises.isNotEmpty()) }
    @Test fun `has judge criteria`() { assertTrue(AttentionComponent.spec.judgeCriteria.isNotEmpty()) }
}
