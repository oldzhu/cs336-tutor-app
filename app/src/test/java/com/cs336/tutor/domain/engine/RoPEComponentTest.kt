package com.cs336.tutor.domain.engine

import org.junit.Assert.*
import org.junit.Test

class RoPEComponentTest {

    @Test fun `spec has correct id`() { assertEquals("rope", RoPEComponent.spec.id) }
    @Test fun `spec has correct name`() { assertEquals("RoPE", RoPEComponent.spec.name) }
    @Test fun `spec has code lines`() { assertTrue(RoPEComponent.spec.codeLines.isNotEmpty()) }
    @Test fun `has class or function definition`() {
        val code = RoPEComponent.spec.codeLines.joinToString("\n") { it.code }
        assertTrue(code.contains("class") || code.contains("def"))
    }
    @Test fun `rotary computation uses sin and cos`() {
        val code = RoPEComponent.spec.codeLines.joinToString("\n") { it.code }
        assertTrue(code.contains("sin") && code.contains("cos"))
    }
    @Test fun `has exercises`() { assertTrue(RoPEComponent.spec.exercises.isNotEmpty()) }
    @Test fun `has judge criteria`() { assertTrue(RoPEComponent.spec.judgeCriteria.isNotEmpty()) }
}
