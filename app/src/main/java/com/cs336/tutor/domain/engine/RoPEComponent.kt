package com.cs336.tutor.domain.engine

import com.cs336.tutor.domain.model.*

/**
 * Rotary Position Embedding (RoPE) — encodes position information
 * directly into attention scores via rotation matrices.
 */
object RoPEComponent {
    val spec = ComponentSpec(
        id = "rope", name = "RoPE",
        description = "Rotary Position Embedding. Encodes position by rotating query/key vectors. Used in LLaMA, Mistral, DeepSeek.",
        prerequisites = listOf("rmsnorm"),
        codeLines = listOf(
            CodeLineStub(1, "import torch", "Import PyTorch.", isEditable = false),
            CodeLineStub(2, "import math", "Import math for log and trigonometric functions.", isEditable = false),
            CodeLineStub(4, "def precompute_freqs_cis(dim, end, theta=10000.0):",
                "Precompute complex exponentials for all positions. This runs once at init — not every forward pass. Returns cos+sin values as complex numbers (cis)."),
            CodeLineStub(5, "freqs = 1.0 / (theta ** (torch.arange(0, dim, 2)[: (dim // 2)].float() / dim))",
                "KEY LINE: Compute frequencies on a log scale. Lower dimensions get higher frequencies (short-range position info). Higher dimensions get lower frequencies (long-range)."),
            CodeLineStub(6, "t = torch.arange(end)", "Create position indices [0, 1, ..., end-1]."),
            CodeLineStub(7, "freqs = torch.outer(t, freqs)", "Outer product: every position × every frequency → shape (seq_len, dim//2)."),
            CodeLineStub(8, "freqs_cis = torch.polar(torch.ones_like(freqs), freqs)",
                "Convert to complex numbers: polar(1, angle) = e^(i·angle) = cos(angle) + i·sin(angle)."),
            CodeLineStub(9, "return freqs_cis", "Return precomputed complex exponentials for all positions.", isEditable = false),
            CodeLineStub(11, "def apply_rotary_emb(xq, xk, freqs_cis):",
                "Apply rotary embeddings to query and key tensors. xq/xk have shape (batch, seq_len, n_heads, head_dim)."),
            CodeLineStub(12, "xq_ = torch.view_as_complex(xq.float().reshape(*xq.shape[:-1], -1, 2))",
                "Reshape to pairs and view as complex numbers. Maps (real, imag) pairs → complex numbers for efficient rotation."),
            CodeLineStub(14, "xk_ = torch.view_as_complex(xk.float().reshape(*xk.shape[:-1], -1, 2))",
                "Same for keys — convert to complex representation.", isEditable = false),
            CodeLineStub(16, "xq_out = torch.view_as_real(xq_ * freqs_cis).flatten(3)",
                "Rotate queries: multiply complex Q by complex cis → rotates by position angle. Then convert back to real numbers."),
            CodeLineStub(18, "xk_out = torch.view_as_real(xk_ * freqs_cis).flatten(3)",
                "Rotate keys: same operation. This encodes relative position into the dot product Q·K."),
            CodeLineStub(20, "return xq_out.type_as(xq), xk_out.type_as(xk)", "Cast back to original dtype.")
        ),
        exercises = listOf(
            Exercise("rope_ex_1", "Implement precompute_freqs_cis(): compute frequency-based complex exponentials",
                "freqs_cis of shape (max_seq_len, dim//2) as complex tensor",
                listOf(JudgeCriterion("Correct frequency formula (1/theta^(2i/d))", 0.4f),
                    JudgeCriterion("Uses torch.polar to create complex numbers", 0.3f),
                    JudgeCriterion("Outer product of positions and frequencies", 0.3f))),
            Exercise("rope_ex_2", "Implement apply_rotary_emb(): rotate Q and K by position",
                "Correctly rotated query and key tensors",
                listOf(JudgeCriterion("Reshapes to pairs and views as complex", 0.3f),
                    JudgeCriterion("Multiplies by freqs_cis (complex rotation)", 0.4f),
                    JudgeCriterion("Returns to real and casts to original dtype", 0.3f)))
        ),
        judgeCriteria = listOf(
            JudgeCriterion("Frequency precomputation is correct", 0.4f),
            JudgeCriterion("Rotation is applied correctly to Q and K", 0.4f),
            JudgeCriterion("Type handling (float cast, return dtype)", 0.2f)
        )
    )
}
