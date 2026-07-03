package com.cs336.tutor.domain.engine

import org.junit.Assert.*
import org.junit.Test

class TrainingLoopComponentTest {
    @Test fun `spec has correct id`() { assertEquals("training", TrainingLoopComponent.spec.id) }
    @Test fun `spec name`() { assertEquals("Training Loop", TrainingLoopComponent.spec.name) }
    @Test fun `has code lines`() { assertTrue(TrainingLoopComponent.spec.codeLines.isNotEmpty()) }
    @Test fun `has loss computation`() {
        val code = TrainingLoopComponent.spec.codeLines.joinToString("\n") { it.code }
        assertTrue(code.contains("cross_entropy") || code.contains("loss") || code.contains("CrossEntropy"))
    }
    @Test fun `has optimizer`() {
        val code = TrainingLoopComponent.spec.codeLines.joinToString("\n") { it.code }
        assertTrue(code.contains("optim") || code.contains("Adam") || code.contains("SGD"))
    }
    @Test fun `has backward pass`() {
        val code = TrainingLoopComponent.spec.codeLines.joinToString("\n") { it.code }
        assertTrue(code.contains("backward") || code.contains("zero_grad"))
    }
    @Test fun `has exercises`() { assertTrue(TrainingLoopComponent.spec.exercises.isNotEmpty()) }
    @Test fun `has judge criteria`() { assertTrue(TrainingLoopComponent.spec.judgeCriteria.isNotEmpty()) }
}
