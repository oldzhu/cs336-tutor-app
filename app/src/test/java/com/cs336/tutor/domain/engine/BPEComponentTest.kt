package com.cs336.tutor.domain.engine

import com.cs336.tutor.domain.model.CodeLineStub
import com.cs336.tutor.domain.model.ComponentSpec
import org.junit.Assert.*
import org.junit.Test

class BPEComponentTest {

    @Test
    fun `spec has correct id and name`() {
        val spec = BPEComponent.spec
        assertEquals("bpe", spec.id)
        assertEquals("BPE Tokenizer", spec.name)
        assertTrue(spec.description.contains("Byte-Pair Encoding"))
    }

    @Test
    fun `spec contains all required code sections`() {
        val spec = BPEComponent.spec
        val codeLines = spec.codeLines

        // Should have BPE training code
        val trainLines = codeLines.filter { it.code.contains("train_bpe") }
        assertTrue("Should contain train_bpe function", trainLines.isNotEmpty())

        // Should have encoding code
        val encodeLines = codeLines.filter { it.code.contains("encode") }
        assertTrue("Should contain encode method", encodeLines.isNotEmpty())

        // Should have decoding code
        val decodeLines = codeLines.filter { it.code.contains("decode") }
        assertTrue("Should contain decode method", decodeLines.isNotEmpty())
    }

    @Test
    fun `code lines have line numbers starting from 1`() {
        val spec = BPEComponent.spec
        val lines = spec.codeLines
        assertTrue("Should have code lines", lines.isNotEmpty())
        assertEquals(1, lines.first().lineNumber)
    }

    @Test
    fun `exercises have judge criteria`() {
        val spec = BPEComponent.spec
        assertTrue("Should have exercises", spec.exercises.isNotEmpty())
        spec.exercises.forEach { exercise ->
            assertTrue(
                "Exercise '${exercise.id}' should have judge criteria",
                exercise.judgeCriteria.isNotEmpty()
            )
        }
    }

    @Test
    fun `code lines are not editable by default for explanation`() {
        val spec = BPEComponent.spec
        // Most lines should be editable (user writes code)
        val editableCount = spec.codeLines.count { it.isEditable }
        assertTrue("Most lines should be editable", editableCount > spec.codeLines.size / 2)
    }
}
