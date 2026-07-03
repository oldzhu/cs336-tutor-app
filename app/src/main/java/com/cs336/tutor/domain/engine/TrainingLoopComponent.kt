package com.cs336.tutor.domain.engine

import com.cs336.tutor.domain.model.*

object TrainingLoopComponent {
    val spec = ComponentSpec(
        id = "training", name = "Training Loop",
        description = "Complete training loop: forward pass, loss computation, backward pass, and optimizer step.",
        prerequisites = listOf("transformer"),
        codeLines = listOf(
            CodeLineStub(1, "import torch", "Import PyTorch — provides tensor operations and automatic differentiation."),
            CodeLineStub(2, "import torch.nn.functional as F", "Cross-entropy loss function.", isEditable = false),
            CodeLineStub(4, "def train_step(model, batch, optimizer):",
                "One complete training step: forward → loss → backward → update. batch = (input_ids, target_ids)."),
            CodeLineStub(5, "model.train()",
                "Set model to training mode. This enables dropout, batch norm, etc."),
            CodeLineStub(6, "optimizer.zero_grad()",
                "CRITICAL: Zero all gradients before backward pass. If you skip this, gradients accumulate from previous steps!",
                hints = listOf("Forgetting zero_grad() is a common bug — loss doesn't decrease")),
            CodeLineStub(8, "logits = model(batch)",
                "Forward pass through the entire model. logits shape: (batch, seq_len, vocab_size). Each position predicts the next token."),
            CodeLineStub(10, "loss = F.cross_entropy(",
                "KEY LINE: Standard language modeling loss. Compares predicted token distribution against actual next tokens."),
            CodeLineStub(11, "logits.view(-1, logits.size(-1)),",
                "Flatten logits: (batch*seq_len, vocab_size). We predict each token independently."),
            CodeLineStub(12, "batch.view(-1)",
                "Flatten targets: (batch*seq_len,). Must match logits' first dimension."),
            CodeLineStub(13, ")",
                "", isEditable = false),
            CodeLineStub(15, "loss.backward()",
                "Backpropagation: computes gradients for all parameters via automatic differentiation."),
            CodeLineStub(16, "optimizer.step()",
                "Apply gradients: update all parameters using the optimizer's rule (Adam, SGD, etc.)."),
            CodeLineStub(17, "return loss.item()",
                "Return scalar loss value for logging/monitoring.")
        ),
        exercises = listOf(
            Exercise("train_ex_1", "Implement train_step with zero_grad → forward → loss → backward → step",
                "Loss decreases over multiple steps on sample data",
                listOf(JudgeCriterion("zero_grad() called before backward", 0.2f),
                    JudgeCriterion("Correct cross_entropy loss computation", 0.3f),
                    JudgeCriterion("backward() then optimizer.step()", 0.3f),
                    JudgeCriterion("Returns loss value", 0.2f)))
        ),
        judgeCriteria = listOf(
            JudgeCriterion("Correct training step order", 0.3f),
            JudgeCriterion("Cross-entropy loss computes correctly", 0.3f),
            JudgeCriterion("Gradient flow verified (loss decreases)", 0.4f)
        )
    )
}
