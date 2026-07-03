package com.cs336.tutor.domain.engine

import com.cs336.tutor.domain.model.*

object TransformerBlockComponent {
    val spec = ComponentSpec(
        id = "transformer", name = "Full Transformer Block",
        description = "Assembles RMSNorm, Attention, and FFN into a complete decoder-only Transformer block with residual connections.",
        prerequisites = listOf("ffn"),
        codeLines = listOf(
            CodeLineStub(1, "import torch.nn as nn", "", isEditable = false),
            CodeLineStub(2, "from rmsnorm import RMSNorm", "Import our RMSNorm implementation.", isEditable = false),
            CodeLineStub(3, "from attention import Attention", "Import our Attention implementation.", isEditable = false),
            CodeLineStub(4, "from ffn import FeedForward", "Import our FFN implementation.", isEditable = false),
            CodeLineStub(6, "class TransformerBlock(nn.Module):",
                "The complete building block of a decoder-only Transformer. Each block transforms hidden states through attention + FFN."),
            CodeLineStub(7, "def __init__(self, dim, n_heads):",
                "Initialize all sub-components: 2 RMSNorm layers, 1 Attention, 1 FFN."),
            CodeLineStub(8, "super().__init__()", "", isEditable = false),
            CodeLineStub(9, "self.attention = Attention(dim, n_heads)", "Multi-head self-attention sub-layer."),
            CodeLineStub(10, "self.feed_forward = FeedForward(dim)", "SwiGLU feed-forward sub-layer."),
            CodeLineStub(11, "self.attention_norm = RMSNorm(dim)",
                "PRE-attention normalization (Pre-LN architecture). Normalizes before attention, not after. Used in modern LLaMA-style models."),
            CodeLineStub(12, "self.ffn_norm = RMSNorm(dim)",
                "PRE-FFN normalization. Same pattern: normalize before the sub-layer.", isEditable = false),
            CodeLineStub(14, "def forward(self, x, freqs_cis, mask=None):",
                "Complete forward pass through one Transformer block: norm → attn → +residual → norm → ffn → +residual."),
            CodeLineStub(16, "h = x + self.attention(self.attention_norm(x), freqs_cis, mask)",
                "KEY LINE: Pre-LN attention with residual. (1) RMSNorm the input, (2) run attention with RoPE, (3) add residual: h = x + attn(norm(x)). The residual connection is CRITICAL — it allows gradients to flow directly through the network without vanishing."),
            CodeLineStub(18, "out = h + self.feed_forward(self.ffn_norm(h))",
                "Same pattern for FFN: normalize → FFN → add residual. out = h + ffn(norm(h))."),
            CodeLineStub(19, "return out",
                "Output has same shape as input (batch, seq_len, dim). This enables stacking multiple blocks.")
        ),
        exercises = listOf(
            Exercise("tb_ex_1", "Implement TransformerBlock.__init__ with all sub-components",
                "Module with attention, ffn, attention_norm, ffn_norm attributes", listOf(
                    JudgeCriterion("Correct sub-module initialization", 0.4f),
                    JudgeCriterion("Pre-LN architecture (norm before sub-layer)", 0.3f),
                    JudgeCriterion("All components properly constructed", 0.3f))),
            Exercise("tb_ex_2", "Implement forward with Pre-LN and residual connections",
                "Output matches stacking sub-components manually", listOf(
                    JudgeCriterion("RMSNorm applied before attention (Pre-LN)", 0.3f),
                    JudgeCriterion("Residual connection: x + attn(norm(x))", 0.3f),
                    JudgeCriterion("RMSNorm applied before FFN", 0.2f),
                    JudgeCriterion("Second residual: h + ffn(norm(h))", 0.2f)))
        ),
        judgeCriteria = listOf(
            JudgeCriterion("Pre-LN architecture (norm before each sub-layer)", 0.3f),
            JudgeCriterion("Residual connections (x + sublayer(norm(x)))", 0.3f),
            JudgeCriterion("Output shape matches input shape", 0.2f),
            JudgeCriterion("All sub-components correctly wired", 0.2f)
        )
    )
}
