package com.cs336.tutor.domain.engine

import com.cs336.tutor.domain.model.*

object EmbeddingComponent {
    val spec = ComponentSpec(
        id = "embedding", name = "Embedding Layer",
        description = "Token embedding layer: maps token IDs to dense vectors of dimension dim.",
        prerequisites = listOf("bpe"),
        codeLines = listOf(
            CodeLineStub(1, "import torch.nn as nn", "Import PyTorch neural network module — provides nn.Module and nn.Embedding."),
            CodeLineStub(3, "class Embedding(nn.Module):",
                "Token embedding layer. Maps integer token IDs (0 to vocab_size-1) to dense vectors of size dim. These vectors are learned during training."),
            CodeLineStub(4, "def __init__(self, vocab_size, dim):",
                "vocab_size = number of unique tokens. dim = embedding dimension (e.g., 4096 for 7B models)."),
            CodeLineStub(5, "super().__init__()", "Initialize nn.Module — registers all sub-modules and parameters."),
            CodeLineStub(6, "self.embed = nn.Embedding(vocab_size, dim)",
                "KEY LINE: PyTorch's built-in embedding lookup. Stores a (vocab_size, dim) weight matrix. During forward, selects rows by token ID."),
            CodeLineStub(8, "def forward(self, x):",
                "Forward pass. x: token indices, shape (batch, seq_len). Returns embeddings, shape (batch, seq_len, dim)."),
            CodeLineStub(9, "return self.embed(x)",
                "Lookup each token ID in the embedding table. Gradients flow back through the selected rows during training.")
        ),
        exercises = listOf(
            Exercise("emb_ex_1", "Implement Embedding layer with nn.Embedding",
                "Module that maps (batch, seq) indices to (batch, seq, dim) vectors",
                listOf(JudgeCriterion("nn.Embedding with correct dimensions", 0.5f),
                    JudgeCriterion("forward returns correct shape", 0.3f),
                    JudgeCriterion("super().__init__() called", 0.2f)))
        ),
        judgeCriteria = listOf(
            JudgeCriterion("nn.Embedding used correctly", 0.4f),
            JudgeCriterion("Correct input/output shapes", 0.3f),
            JudgeCriterion("clean, minimal implementation", 0.3f)
        )
    )
}
