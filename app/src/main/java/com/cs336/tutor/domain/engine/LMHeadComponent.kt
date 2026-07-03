package com.cs336.tutor.domain.engine

import com.cs336.tutor.domain.model.*

object LMHeadComponent {
    val spec = ComponentSpec(
        id = "lmhead", name = "LM Head",
        description = "Language Model Head: maps hidden states to vocabulary logits via a linear projection.",
        prerequisites = listOf("training"),
        codeLines = listOf(
            CodeLineStub(1, "import torch.nn as nn", "Import PyTorch neural network module for nn.Module and nn.Linear."),
            CodeLineStub(3, "class LMHead(nn.Module):",
                "The final layer that converts hidden states to token predictions. Maps (batch, seq, dim) → (batch, seq, vocab_size)."),
            CodeLineStub(4, "def __init__(self, dim, vocab_size):",
                "dim = model dimension. vocab_size = number of tokens in vocabulary."),
            CodeLineStub(5, "super().__init__()", "", isEditable = false),
            CodeLineStub(6, "self.lm_head = nn.Linear(dim, vocab_size, bias=False)",
                "KEY LINE: Linear projection from model dimension to vocabulary size. bias=False (modern practice). Weight tying: often shares weights with Embedding layer."),
            CodeLineStub(8, "def forward(self, x):",
                "Forward pass: project each positions hidden state to vocabulary logits."),
            CodeLineStub(9, "return self.lm_head(x)",
                "Output shape: (batch, seq, vocab_size). Each position has a score for every token — the highest score is the predicted next token.")
        ),
        exercises = listOf(Exercise("lmhead_ex_1", "Implement LMHead as nn.Linear(dim, vocab_size)",
            "Module mapping (batch, seq, dim) → (batch, seq, vocab_size)",
            listOf(JudgeCriterion("nn.Linear with correct dims", 0.5f),
                JudgeCriterion("bias=False", 0.2f), JudgeCriterion("forward returns correct shape", 0.3f)))),
        judgeCriteria = listOf(JudgeCriterion("Correct Linear projection", 0.4f),
            JudgeCriterion("bias=False", 0.3f), JudgeCriterion("Output shape correct", 0.3f))
    )
}
