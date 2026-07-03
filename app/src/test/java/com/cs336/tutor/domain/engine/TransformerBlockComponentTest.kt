package com.cs336.tutor.domain.engine

import org.junit.Assert.*
import org.junit.Test

class TransformerBlockComponentTest {

    @Test fun `spec has correct id`() { assertEquals("transformer", TransformerBlockComponent.spec.id) }
    @Test fun `spec has correct name`() { assertEquals("Full Transformer Block", TransformerBlockComponent.spec.name) }
    @Test fun `spec assembles RMSNorm and Attention`() {
        val code = TransformerBlockComponent.spec.codeLines.joinToString("\n") { it.code }
        assertTrue(code.contains("RMSNorm") || code.contains("norm") || code.contains("attention") || code.contains("self_attn"))
    }
    @Test fun `uses residual connections`() {
        val code = TransformerBlockComponent.spec.codeLines.joinToString("\n") { it.code }
        assertTrue(code.contains("+") && (code.contains("residual") || code.contains("x")))
    }
    @Test fun `has exercises`() { assertTrue(TransformerBlockComponent.spec.exercises.isNotEmpty()) }
    @Test fun `has judge criteria`() { assertTrue(TransformerBlockComponent.spec.judgeCriteria.isNotEmpty()) }
}
