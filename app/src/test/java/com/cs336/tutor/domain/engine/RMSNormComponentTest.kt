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
    fun `code line 1 is function definition`() {
        val line = RMSNormComponent.spec.codeLines[0]
        assertTrue(line.code.contains("def"))
        assertTrue(line.code.contains("RMSNorm"))
        assertTrue(line.code.contains("nn.Module"))
    }

    @Test
    fun `code line 2 defines weight parameter`() {
        val line = RMSNormComponent.spec.codeLines[1]
        assertTrue(line.code.contains("w") || line.code.contains("weight"))
    }

    @Test
    fun `code line 3 defines epsilon constant`() {
        val line3 = RMSNormComponent.spec.codeLines[2]
        val allCode = RMSNormComponent.spec.codeLines.joinToString("\n") { it.code }
        assertTrue(allCode.contains("eps") || allCode.contains("epsilon"))
    }

    @Test
    fun `forward method contains rms computation`() {
        val allCode = RMSNormComponent.spec.codeLines.joinToString("\n") { it.code }
        assertTrue(allCode.contains("forward"))
        assertTrue(allCode.contains("pow") || allCode.contains("square") || allCode.contains("**"))
    }

    @Test
    fun `spec has at least one exercise`() {
        assertTrue(RMSNormComponent.spec.exercises.isNotEmpty())
    }

    @Test
    fun `spec has judge criteria`() {
        assertTrue(RMSNormComponent.spec.judgeCriteria.isNotEmpty())
    }
}
