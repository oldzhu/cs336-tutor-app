package com.cs336.tutor.domain.engine

import com.cs336.tutor.domain.model.*

object FFNComponent {
    val spec = ComponentSpec(
        id = "ffn", name = "Position-wise FFN (SwiGLU)",
        description = "Feed-forward network with SwiGLU activation: output = (x·W_gate ⊙ SiLU(x·W_up)) · W_down.",
        prerequisites = listOf("attention"),
        codeLines = listOf(
            CodeLineStub(1, "import torch.nn as nn", "Import PyTorch neural network module."),
            CodeLineStub(2, "import torch.nn.functional as F", "SiLU activation is in F.silu().", isEditable = false),
            CodeLineStub(4, "class FeedForward(nn.Module):",
                "SwiGLU feed-forward network: expands to hidden_dim, applies gated activation, projects back."),
            CodeLineStub(5, "def __init__(self, dim, hidden_dim=None):",
                "dim = model dimension (e.g. 4096). hidden_dim typically = 8/3 * dim ≈ 14336 for 7B models."),
            CodeLineStub(6, "super().__init__()", "Initialize nn.Module — registers all sub-modules and parameters."),
            CodeLineStub(7, "hidden_dim = hidden_dim or 4 * dim",
                "Default: 4x expansion. LLaMA-style uses 8/3 * dim, but 4x is the classic Transformer default."),
            CodeLineStub(8, "self.w1 = nn.Linear(dim, hidden_dim, bias=False)", "Gate projection: produces gating values."),
            CodeLineStub(9, "self.w3 = nn.Linear(dim, hidden_dim, bias=False)", "Up projection: produces values to be gated."),
            CodeLineStub(10, "self.w2 = nn.Linear(hidden_dim, dim, bias=False)", "Down projection: projects back to model dim."),
            CodeLineStub(12, "def forward(self, x):",
                "SwiGLU forward: gate = SiLU(x·w1), up = x·w3, output = (gate ⊙ up) · w2."),
            CodeLineStub(13, "return self.w2(F.silu(self.w1(x)) * self.w3(x))",
                "KEY LINE: The full SwiGLU formula in one line! SiLU(w1·x) acts as a learned gate on w3·x, then w2 projects back. The element-wise multiply (*) makes this a gated activation — unlike ReLU which is a simple threshold.")
        ),
        exercises = listOf(
            Exercise("ffn_ex_1", "Implement FFN.__init__ with gate/up/down projections",
                "Three nn.Linear layers with correct dimensions", listOf(
                    JudgeCriterion("w1 (gate) and w3 (up) project dim → hidden_dim", 0.4f),
                    JudgeCriterion("w2 (down) projects hidden_dim → dim", 0.3f),
                    JudgeCriterion("bias=False on all projections", 0.3f))),
            Exercise("ffn_ex_2", "Implement forward with SwiGLU formula",
                "Correct SwiGLU output matching reference", listOf(
                    JudgeCriterion("SiLU applied to gate projection", 0.4f),
                    JudgeCriterion("Element-wise multiply of gate and up", 0.3f),
                    JudgeCriterion("Down projection applied to result", 0.3f)))
        ),
        judgeCriteria = listOf(
            JudgeCriterion("Correct SwiGLU formula: silu(gate) * up → down", 0.5f),
            JudgeCriterion("Correct dimensions (dim → hidden_dim → dim)", 0.3f),
            JudgeCriterion("bias=False on all Linear layers", 0.2f)
        )
    )
}
