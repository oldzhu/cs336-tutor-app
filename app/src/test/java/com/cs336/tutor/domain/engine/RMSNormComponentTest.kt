package com.cs336.tutor.domain.engine

import org.junit.Assert.*
import org.junit.Test

class RMSNormComponentTest {

    @Test
    fun `spec has correct id`() {
        assertEquals("rmsnorm", RMSNormComponent.spec.id)
    }

    @Test
    fun `spec has correct name`() {
        assertEquals("RMSNorm", RMSNormComponent.spec.name)
    }

    @Test
    fun `spec has code lines`() {
        assertTrue(RMSNormComponent.spec.codeLines.isNotEmpty())
    }

    @Test
    fun `class definition is in code lines`() {
        val allCode = RMSNormComponent.spec.codeLines.joinToString("\n") { it.code }
        assertTrue(allCode.contains("class RMSNorm"))
        assertTrue(allCode.contains("nn.Module"))
    }

    @Test
    fun `weight parameter is nn.Parameter of ones`() {
        val allCode = RMSNormComponent.spec.codeLines.joinToString("\n") { it.code }
        assertTrue(allCode.contains("nn.Parameter"))
        assertTrue(allCode.contains("torch.ones"))
    }

    @Test
    fun `epsilon is defined in init`() {
        val allCode = RMSNormComponent.spec.codeLines.joinToString("\n") { it.code }
        assertTrue(allCode.contains("eps"))
    }

    @Test
    fun `forward method exists`() {
        val allCode = RMSNormComponent.spec.codeLines.joinToString("\n") { it.code }
        assertTrue(allCode.contains("forward"))
    }

    @Test
    fun `rms computation uses pow and rsqrt`() {
        val allCode = RMSNormComponent.spec.codeLines.joinToString("\n") { it.code }
        assertTrue(allCode.contains("pow") || allCode.contains("square") || allCode.contains("**"))
        assertTrue(allCode.contains("rsqrt") || allCode.contains("sqrt"))
    }

    @Test
    fun `spec has exercises`() {
        assertTrue(RMSNormComponent.spec.exercises.isNotEmpty())
    }

    @Test
    fun `spec has judge criteria`() {
        assertTrue(RMSNormComponent.spec.judgeCriteria.isNotEmpty())
    }
}
