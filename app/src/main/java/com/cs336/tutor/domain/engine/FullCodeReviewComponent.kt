package com.cs336.tutor.domain.engine

import com.cs336.tutor.domain.model.*

object FullCodeReviewComponent {
    val spec = ComponentSpec(
        id = "fullreview",
        name = "Full Code Review",
        description = "Assemble all component code into one file and review",
        prerequisites = listOf("bpe", "embedding", "rmsnorm", "rope", "attention", "ffn", "transformer", "lmhead", "optimizer", "training"),
        codeLines = listOf(
            CodeLineStub(1, "# CS336 Assignment 1 — Complete Transformer Pipeline", "Full assignment: all 10 components assembled.", isEditable = false),
            CodeLineStub(2, "# Tokenize (BPE) → Embed → N×TransformerBlocks → LM Head → Loss", "Complete decoder-only transformer pipeline.", isEditable = false),
            CodeLineStub(3, "# Use Judge Assignment button for AI evaluation", "The AI reviews correctness, style, and completeness.", isEditable = false)
        ),
        exercises = listOf(
            Exercise("Review your assembled code", "Check consistency", "", emptyList()),
            Exercise("Verify dimensions", "Trace shape at each stage", "", emptyList()),
            Exercise("Optimize", "Identify bottlenecks", "", emptyList())
        ),
        judgeCriteria = listOf(
            JudgeCriterion("All 10 components present", 0.4f),
            JudgeCriterion("Tensor dims consistent", 0.3f),
            JudgeCriterion("PyTorch best practices", 0.3f)
        )
    )
}
