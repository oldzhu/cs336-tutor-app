package com.cs336.tutor.domain.engine

import com.cs336.tutor.domain.model.*

object OptimizerComponent {
    val spec = ComponentSpec(
        id = "optimizer", name = "Adam Optimizer",
        description = "Adam (Adaptive Moment Estimation): combines momentum and RMSProp for efficient gradient-based optimization.",
        prerequisites = listOf("lmhead"),
        codeLines = listOf(
            CodeLineStub(1, "import torch", "", isEditable = false),
            CodeLineStub(3, "optimizer = torch.optim.AdamW(model.parameters(), lr=3e-4, betas=(0.9, 0.95), weight_decay=0.1)",
                "KEY LINE: AdamW — Adam with decoupled weight decay. lr=3e-4 (standard for 7B models). betas control momentum decay rates. weight_decay=0.1 for regularization.",
                hints = listOf("AdamW is preferred over Adam — weight decay is decoupled from gradient updates")),
            CodeLineStub(5, "optimizer = torch.optim.Adam(model.parameters(), lr=1e-3)",
                "Classic Adam (without weight decay). Simpler but less effective for large models.",
                isEditable = false),
            CodeLineStub(7, "# Adam update rule:", "", isEditable = false),
            CodeLineStub(8, "# m_t = beta1 * m_{t-1} + (1-beta1) * g_t  (momentum)",
                "First moment (mean of gradients). Smooths noisy gradients — like a rolling average.", isEditable = false),
            CodeLineStub(9, "# v_t = beta2 * v_{t-1} + (1-beta2) * g_t²  (RMS)",
                "Second moment (uncentered variance). Adapts learning rate per-parameter.", isEditable = false),
            CodeLineStub(10, "# theta_t = theta_{t-1} - lr * m_t / (sqrt(v_t) + eps)  (update)",
                "Parameter update: divide momentum by sqrt of adaptive learning rate. Each parameter gets its own effective learning rate.", isEditable = false)
        ),
        exercises = listOf(Exercise("opt_ex_1", "Create AdamW optimizer with correct parameters",
            "Optimizer configured for language model training",
            listOf(JudgeCriterion("Uses AdamW (not Adam)", 0.3f), JudgeCriterion("lr between 1e-4 and 1e-3", 0.2f),
                JudgeCriterion("weight_decay > 0", 0.25f), JudgeCriterion("model.parameters() passed", 0.25f)))),
        judgeCriteria = listOf(JudgeCriterion("AdamW correctly configured", 0.4f),
            JudgeCriterion("Appropriate learning rate", 0.3f), JudgeCriterion("Weight decay enabled", 0.3f))
    )
}
