package com.cs336.tutor.domain.engine

import com.cs336.tutor.domain.model.*

object AttentionComponent {
    val spec = ComponentSpec(
        id = "attention", name = "Multi-Head Self-Attention",
        description = "Causal scaled dot-product attention with multi-head projections.",
        prerequisites = listOf("rope"),
        codeLines = listOf(
            CodeLineStub(1, "import torch.nn as nn", "", isEditable = false),
            CodeLineStub(2, "import torch.nn.functional as F", "Import functional API for softmax.", isEditable = false),
            CodeLineStub(4, "class Attention(nn.Module):", "Multi-head causal self-attention module."),
            CodeLineStub(5, "def __init__(self, dim, n_heads):",
                "dim = model dimension, n_heads = number of attention heads. head_dim = dim // n_heads."),
            CodeLineStub(6, "super().__init__()", "", isEditable = false),
            CodeLineStub(7, "self.n_heads = n_heads", "Store number of heads.", isEditable = false),
            CodeLineStub(8, "self.head_dim = dim // n_heads", "Each head operates on dim//n_heads dimensional subspace."),
            CodeLineStub(9, "self.wq = nn.Linear(dim, dim, bias=False)",
                "Query projection. bias=False because we use RoPE (no absolute position — bias would add the same to every position)."),
            CodeLineStub(10, "self.wk = nn.Linear(dim, dim, bias=False)", "Key projection. Same shape as query.", isEditable = false),
            CodeLineStub(11, "self.wv = nn.Linear(dim, dim, bias=False)", "Value projection.", isEditable = false),
            CodeLineStub(12, "self.wo = nn.Linear(dim, dim, bias=False)", "Output projection combines all head outputs."),
            CodeLineStub(14, "def forward(self, x, freqs_cis, mask=None):",
                "Forward pass. x: (batch, seq_len, dim). freqs_cis: precomputed RoPE frequencies."),
            CodeLineStub(15, "b, s, d = x.shape", "Unpack batch, sequence length, and model dimension."),
            CodeLineStub(17, "xq = self.wq(x).view(b, s, self.n_heads, self.head_dim)",
                "Project to queries and reshape: (batch, seq, dim) → (batch, seq, n_heads, head_dim)."),
            CodeLineStub(18, "xk = self.wk(x).view(b, s, self.n_heads, self.head_dim)", "Same for keys.", isEditable = false),
            CodeLineStub(19, "xv = self.wv(x).view(b, s, self.n_heads, self.head_dim)", "Same for values.", isEditable = false),
            CodeLineStub(21, "xq, xk = apply_rotary_emb(xq, xk, freqs_cis)",
                "Apply RoPE to queries and keys — encodes position into attention scores."),
            CodeLineStub(23, "scores = torch.matmul(xq, xk.transpose(2, 3)) / math.sqrt(self.head_dim)",
                "THE CORE: Scaled dot-product. Q·K^T divided by sqrt(d_k) prevents softmax saturation at large dimensions."),
            CodeLineStub(25, "if mask is not None: scores = scores + mask",
                "Apply causal mask (upper triangle = -inf) so position i can only attend to positions <= i."),
            CodeLineStub(26, "scores = F.softmax(scores, dim=-1)",
                "Normalize attention weights to sum to 1. Each row is a probability distribution over previous positions."),
            CodeLineStub(28, "output = torch.matmul(scores, xv)",
                "Weighted sum of values: each position combines values from all positions it can attend to."),
            CodeLineStub(29, "output = output.transpose(1, 2).contiguous().view(b, s, d)",
                "Reshape back: (batch, n_heads, seq, head_dim) → (batch, seq, dim)."),
            CodeLineStub(30, "return self.wo(output)", "Output projection blends information across all heads.")
        ),
        exercises = listOf(
            Exercise("attn_ex_1", "Implement Q/K/V projections with correct shapes",
                "Tensors shaped (batch, seq, n_heads, head_dim)", listOf(
                    JudgeCriterion("Q, K, V projections use nn.Linear(dim, dim, bias=False)", 0.4f),
                    JudgeCriterion("Correctly reshaped to (b, s, n_heads, head_dim)", 0.4f),
                    JudgeCriterion("apply_rotary_emb called on Q and K", 0.2f))),
            Exercise("attn_ex_2", "Implement scaled dot-product attention with causal mask",
                "Attention output correctly masked and normalized", listOf(
                    JudgeCriterion("Q·K^T / sqrt(head_dim) scaling", 0.3f),
                    JudgeCriterion("Causal mask applied (can't attend to future)", 0.3f),
                    JudgeCriterion("Softmax along dim=-1", 0.2f),
                    JudgeCriterion("Output projection and reshape correct", 0.2f)))
        ),
        judgeCriteria = listOf(
            JudgeCriterion("Correct attention formula (QK^T/√d_k)", 0.3f),
            JudgeCriterion("Multi-head splitting/merging correct", 0.3f),
            JudgeCriterion("Causal masking enforced", 0.2f),
            JudgeCriterion("RoPE integration correct", 0.2f)
        )
    )
}
