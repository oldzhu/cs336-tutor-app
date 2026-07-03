package com.cs336.tutor.domain.engine

import org.junit.Assert.*
import org.junit.Test

class FFNComponentTest {

    @Test fun `spec has correct id`() { assertEquals("ffn", FFNComponent.spec.id) }
    @Test fun `spec has correct name`() { assertEquals("Position-wise FFN (SwiGLU)", FFNComponent.spec.name) }
    @Test fun `spec has code lines`() { assertTrue(FFNComponent.spec.codeLines.isNotEmpty()) }
    @Test fun `uses SiLU or Swish activation`() {
        val code = FFNComponent.spec.codeLines.joinToString("\n") { it.code }
        assertTrue(code.contains("silu") || code.contains("SiLU") || code.contains("swish"))
    }
    @Test fun `has gate and up projections`() {
        val code = FFNComponent.spec.codeLines.joinToString("\n") { it.code }
        assertTrue(code.contains("gate") || code.contains("up") || code.contains("w1") || code.contains("w3"))
    }
    @Test fun `has exercises`() { assertTrue(FFNComponent.spec.exercises.isNotEmpty()) }
    @Test fun `has judge criteria`() { assertTrue(FFNComponent.spec.judgeCriteria.isNotEmpty()) }
}
