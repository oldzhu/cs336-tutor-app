package com.cs336.tutor.domain.engine

import com.cs336.tutor.domain.model.CodeLineStub
import com.cs336.tutor.domain.model.ComponentSpec
import com.cs336.tutor.domain.model.Exercise
import com.cs336.tutor.domain.model.JudgeCriterion

/**
 * RMSNorm (Root Mean Square Layer Normalization) component specification.
 * A simplified layer norm that re-scales the input by the root mean square,
 * used in modern LLMs like LLaMA, Mistral, and DeepSeek.
 */
object RMSNormComponent {
    val spec = ComponentSpec(
        id = "rmsnorm",
        name = "RMSNorm",
        description = "Root Mean Square Layer Normalization. A simplified alternative to LayerNorm " +
                "that normalizes by RMS instead of mean + variance. Used in LLaMA, Mistral, and DeepSeek.",
        prerequisites = listOf("bpe"),
        codeLines = listOf(
            // ── Imports ──
            CodeLineStub(
                lineNumber = 1,
                code = "import torch.nn as nn",
                explanation = "Import PyTorch's neural network module for nn.Module base class and nn.Parameter."
            ),
            CodeLineStub(
                lineNumber = 2,
                code = "import torch",
                explanation = "Import PyTorch for tensor operations like pow, rsqrt, ones.",
                isEditable = false
            ),

            // ── Class definition ──
            CodeLineStub(
                lineNumber = 4,
                code = "class RMSNorm(nn.Module):",
                explanation = "Define RMS Normalization as a PyTorch module. Inherits from nn.Module " +
                        "so it can be used as a layer in a neural network.",
                hints = listOf("nn.Module gives us .parameters(), .to(device), .train()/.eval() for free")
            ),

            // ── __init__ ──
            CodeLineStub(
                lineNumber = 5,
                code = "def __init__(self, dim: int, eps: float = 1e-6):",
                explanation = "Constructor. dim = hidden dimension size (e.g., 4096 for 7B models). " +
                        "eps = small value to prevent division by zero in the normalization.",
                hints = listOf("eps is typically 1e-6 or 1e-5")
            ),
            CodeLineStub(
                lineNumber = 6,
                code = "super().__init__()",
                explanation = "Call the parent nn.Module constructor to register the module correctly.",
                isEditable = false
            ),
            CodeLineStub(
                lineNumber = 7,
                code = "self.eps = eps",
                explanation = "Store epsilon as an instance variable for use in _norm() and forward()."
            ),
            CodeLineStub(
                lineNumber = 8,
                code = "self.w = nn.Parameter(torch.ones(dim))",
                explanation = "KEY LINE: Learnable scale parameter. Initialized to all ones so " +
                        "RMSNorm starts as an identity transform. Unlike LayerNorm, RMSNorm has " +
                        "NO bias parameter — only a single weight vector.",
                hints = listOf("nn.Parameter tells PyTorch this tensor should be updated during training",
                    "Why no bias? RMSNorm normalizes to zero-mean implicitly")
            ),

            // ── _norm helper ──
            CodeLineStub(
                lineNumber = 10,
                code = "def _norm(self, x):",
                explanation = "Private helper that computes the actual RMS normalization. " +
                        "Separated from forward() so the type conversion logic stays clean.",
                hints = listOf("Using a private method is a design choice — you could inline it in forward()")
            ),
            CodeLineStub(
                lineNumber = 11,
                code = "return x * torch.rsqrt(x.pow(2).mean(-1, keepdim=True) + self.eps)",
                explanation = "THE CORE FORMULA: RMSNorm(x) = x / sqrt(mean(x²) + ε). " +
                        "Step by step: (1) x.pow(2) — square all elements, " +
                        "(2) .mean(-1, keepdim=True) — take mean along last dim, " +
                        "(3) + self.eps — add epsilon for numerical stability, " +
                        "(4) torch.rsqrt(...) — 1/sqrt(...), " +
                        "(5) x * ... — multiply input by inverse RMS.",
                hints = listOf("keepdim=True preserves the dimension for broadcasting",
                    "rsqrt is more efficient than 1/sqrt()")
            ),

            // ── forward ──
            CodeLineStub(
                lineNumber = 13,
                code = "def forward(self, x):",
                explanation = "The forward pass. Converts to float for numerical stability, " +
                        "applies RMS normalization, converts back to original dtype, then scales by w."
            ),
            CodeLineStub(
                lineNumber = 14,
                code = "output = self._norm(x.float()).type_as(x)",
                explanation = "Cast input to float32 (for stable computation), normalize, " +
                        "then cast back to the original dtype. This handles mixed-precision " +
                        "training where x might be float16 or bfloat16.",
                hints = listOf("Why float()? RMS can produce NaN in fp16 without this cast")
            ),
            CodeLineStub(
                lineNumber = 15,
                code = "return output * self.w",
                explanation = "Multiply by learnable weight w. This is the only learnable " +
                        "parameter in RMSNorm — it allows the network to learn which features " +
                        "to emphasize or suppress after normalization."
            )
        ),
        exercises = listOf(
            Exercise(
                id = "rmsnorm_ex_1",
                description = "Implement RMSNorm.__init__(): create a learnable weight parameter and store eps",
                expectedOutput = "Module with self.w shape=(dim,) initialized to ones, self.eps=1e-6",
                judgeCriteria = listOf(
                    JudgeCriterion("w is nn.Parameter with correct shape", 0.4f),
                    JudgeCriterion("eps is stored correctly", 0.2f),
                    JudgeCriterion("super().__init__() is called", 0.2f),
                    JudgeCriterion("w is initialized to ones", 0.2f)
                )
            ),
            Exercise(
                id = "rmsnorm_ex_2",
                description = "Implement RMSNorm._norm(): compute x / sqrt(mean(x²) + eps)",
                expectedOutput = "Normalized tensor with same shape as input",
                judgeCriteria = listOf(
                    JudgeCriterion("Correct RMS formula (x² → mean → +eps → rsqrt → x *)", 0.5f),
                    JudgeCriterion("mean computed along last dimension", 0.2f),
                    JudgeCriterion("keepdim=True for broadcasting", 0.15f),
                    JudgeCriterion("rsqrt used (not 1/sqrt)", 0.15f)
                )
            ),
            Exercise(
                id = "rmsnorm_ex_3",
                description = "Implement RMSNorm.forward(): normalize + type cast + scale",
                expectedOutput = "Correct RMS-normalized output matching reference implementation",
                judgeCriteria = listOf(
                    JudgeCriterion("Casts to float for numerical stability", 0.3f),
                    JudgeCriterion("Returns to original dtype with .type_as()", 0.2f),
                    JudgeCriterion("Multiplies by learnable weight w", 0.3f),
                    JudgeCriterion("Output shape matches input shape", 0.2f)
                )
            )
        ),
        judgeCriteria = listOf(
            JudgeCriterion("RMS normalization formula is correct", 0.4f),
            JudgeCriterion("Handles numerical stability (eps, float cast)", 0.3f),
            JudgeCriterion("Learnable weight parameter is properly defined", 0.3f)
        )
    )
}
