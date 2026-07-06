package com.cs336.tutor.domain.engine

import org.junit.Assert.*
import org.junit.Test

class RMSNormComponentTest {
    @Test fun testId() { assertEquals("rmsnorm", RMSNormComponent.spec.id) }
    @Test fun testName() { assertEquals("RMSNorm", RMSNormComponent.spec.name) }
    @Test fun testLines() { assertTrue(RMSNormComponent.spec.codeLines.isNotEmpty()) }
    @Test fun testClassDef() {
        val code = RMSNormComponent.spec.codeLines.joinToString("\n") { it.code }
        assertTrue(code.contains("class RMSNorm") && code.contains("nn.Module"))
    }
    @Test fun testWeightParam() {
        val code = RMSNormComponent.spec.codeLines.joinToString("\n") { it.code }
        assertTrue(code.contains("nn.Parameter") && code.contains("torch.ones"))
    }
    @Test fun testEps() {
        val code = RMSNormComponent.spec.codeLines.joinToString("\n") { it.code }
        assertTrue(code.contains("eps"))
    }
    @Test fun testForward() {
        val code = RMSNormComponent.spec.codeLines.joinToString("\n") { it.code }
        assertTrue(code.contains("forward"))
    }
    @Test fun testRmsComp() {
        val code = RMSNormComponent.spec.codeLines.joinToString("\n") { it.code }
        assertTrue(code.contains("pow") || code.contains("rsqrt") || code.contains("sqrt"))
    }
    @Test fun testExercises() { assertTrue(RMSNormComponent.spec.exercises.isNotEmpty()) }
    @Test fun testCriteria() { assertTrue(RMSNormComponent.spec.judgeCriteria.isNotEmpty()) }
}
