package com.cs336.tutor.domain.engine

/**
 * Bilingual component overviews with formulas, algorithms, and references.
 * Displayed when user taps the "Overview" button in the tutor screen.
 */
object ComponentOverviews {

    data class Overview(
        val en: OverviewContent,
        val zh: OverviewContent
    )

    data class OverviewContent(
        val title: String,
        val formula: String,
        val algorithm: String,
        val purpose: String = "",
        val usage: String = "",
        val without: String = "",
        val why: String,
        val references: List<Reference>
    )

    data class Reference(val label: String, val url: String)

    val bpe = Overview(
        en = OverviewContent(
            title = "BPE Tokenization",
            formula = """
                |Algorithm: Byte-Pair Encoding (Sennrich et al., 2016)
                |
                |1. Start: each byte (0-255) is a token
                |2. Count: freq(a,b) for all adjacent pairs
                |3. Merge: replace most frequent pair → new token
                |4. Repeat steps 2-3 until vocab_size reached
                |
                |Encoding: text → bytes → apply merges in order → token IDs
                |Decoding: token IDs → lookup bytes → UTF-8 string
            """.trimMargin(),
            algorithm = "Greedy frequency-based merge. Each iteration picks the pair with highest count.",
            why = "Modern LLMs use tokenization to balance vocabulary size with sequence length. BPE handles rare words gracefully by decomposing them into subwords.",
            references = listOf(
                Reference("BPE Paper (Sennrich et al.)", "https://arxiv.org/abs/1508.07909"),
                Reference("GPT-2 Tokenizer Deep Dive", "https://www.youtube.com/watch?v=zduSFxRajkE"),
                Reference("Andrej Karpathy's minBPE", "https://github.com/karpathy/minbpe")
            )
        ),
        zh = OverviewContent(
            title = "BPE 分词",
            formula = """
                |算法：Byte-Pair Encoding (Sennrich et al., 2016)
                |
                |1. 起始：每个字节 (0-255) 是一个 token
                |2. 统计：所有相邻对的出现频率 freq(a,b)
                |3. 合并：将最频繁的对替换为新 token
                |4. 重复 2-3 直到达到目标词汇表大小
                |
                |编码：文本 → 字节 → 按顺序应用合并 → token ID
                |解码：token ID → 查找字节 → UTF-8 字符串
            """.trimMargin(),
            algorithm = "贪心频率合并。每次迭代选择计数最高的对。",
            why = "现代 LLM 使用分词来平衡词汇表大小和序列长度。BPE 通过将罕见词分解为子词来优雅地处理它们。",
            references = listOf(
                Reference("BPE 论文 (Sennrich et al.)", "https://arxiv.org/abs/1508.07909"),
                Reference("GPT-2 分词器详解", "https://www.youtube.com/watch?v=zduSFxRajkE"),
                Reference("Karpathy 的 minBPE", "https://github.com/karpathy/minbpe")
            )
        )
    )

    val rmsnorm = Overview(
        en = OverviewContent(
            title = "RMS Normalization",
            formula = """
                |RMSNorm(x) = x / RMS(x) * w
                |where RMS(x) = sqrt(mean(x²) + ε)
                |
                |Complexity: O(d) per token (vs O(2d) for LayerNorm)
                |
                |Key difference from LayerNorm:
                |• No mean subtraction (x - μ)
                |• No bias parameter (only weight w)
                |• ~15% faster, same quality
            """.trimMargin(),
            algorithm = "Compute RMS along last dimension → scale by 1/RMS → multiply by learnable weight w. Use float32 for numerical stability.",
            why = "RMSNorm re-centers implicitly through the weight parameter and is computationally simpler than LayerNorm. Used in LLaMA, Mistral, and DeepSeek.",
            references = listOf(
                Reference("RMSNorm Paper (Zhang & Sennrich, 2019)", "https://arxiv.org/abs/1910.07467"),
                Reference("LLaMA Paper", "https://arxiv.org/abs/2302.13971"),
                Reference("LayerNorm vs RMSNorm Analysis", "https://blog.briankitano.com/llama-from-scratch/")
            )
        ),
        zh = OverviewContent(
            title = "均方根归一化",
            formula = """
                |RMSNorm(x) = x / RMS(x) * w
                |其中 RMS(x) = sqrt(mean(x²) + ε)
                |
                |复杂度：O(d) 每 token（LayerNorm 为 O(2d)）
                |
                |与 LayerNorm 的关键区别：
                |• 无均值减法 (x - μ)
                |• 无 bias 参数（仅有权重 w）
                |• 快约 15%，效果相同
            """.trimMargin(),
            algorithm = "沿最后一维计算 RMS → 除以 RMS 进行缩放 → 乘以可学习权重 w。使用 float32 保证数值稳定。",
            why = "RMSNorm 通过权重参数隐式重新中心化，计算比 LayerNorm 更简单。用于 LLaMA、Mistral 和 DeepSeek。",
            references = listOf(
                Reference("RMSNorm 论文 (Zhang & Sennrich, 2019)", "https://arxiv.org/abs/1910.07467"),
                Reference("LLaMA 论文", "https://arxiv.org/abs/2302.13971"),
                Reference("LayerNorm vs RMSNorm 分析", "https://blog.briankitano.com/llama-from-scratch/")
            )
        )
    )

    val rope = Overview(
        en = OverviewContent(
            title = "Rotary Position Embedding",
            formula = """
                |RoPE rotates query and key vectors by position-dependent angles:
                |
                |f(q, m) = q * e^(i*m*θ)
                |f(k, n) = k * e^(i*n*θ)
                |
                |After rotation: Q·K = g(m-n)  (depends only on relative position!)
                |
                |Frequency: θ_i = 1 / (10000^(2i/d))
                |• Low i → high frequency → short-range patterns
                |• High i → low frequency → long-range patterns
            """.trimMargin(),
            algorithm = "Precompute cos/sin for all positions using log-scale frequencies. During attention, rotate Q and K by multiplying with complex exponentials.",
            why = "RoPE encodes relative position directly into the dot product without adding position vectors. More efficient than learned absolute embeddings for long sequences.",
            references = listOf(
                Reference("RoPE Paper (Su et al., 2021)", "https://arxiv.org/abs/2104.09864"),
                Reference("RoPE Explained Visually", "https://blog.eleuther.ai/rotary-embeddings/"),
                Reference("LLaMA's RoPE Implementation", "https://github.com/meta-llama/llama/blob/main/llama/model.py")
            )
        ),
        zh = OverviewContent(
            title = "旋转位置编码",
            formula = """
                |RoPE 通过与位置相关角度旋转 query 和 key 向量：
                |
                |f(q, m) = q * e^(i*m*θ)
                |f(k, n) = k * e^(i*n*θ)
                |
                |旋转后：Q·K = g(m-n)（仅依赖于相对位置！）
                |
                |频率：θ_i = 1 / (10000^(2i/d))
                |• 低 i → 高频 → 近距离模式
                |• 高 i → 低频 → 远距离模式
            """.trimMargin(),
            algorithm = "使用对数尺度频率预计算所有位置的 cos/sin。在注意力中，通过复数乘法旋转 Q 和 K。",
            why = "RoPE 将相对位置直接编码到点积中，无需添加位置向量。对于长序列比学习到的绝对嵌入更高效。",
            references = listOf(
                Reference("RoPE 论文 (Su et al., 2021)", "https://arxiv.org/abs/2104.09864"),
                Reference("RoPE 可视化解释", "https://blog.eleuther.ai/rotary-embeddings/"),
                Reference("LLaMA 的 RoPE 实现", "https://github.com/meta-llama/llama/blob/main/llama/model.py")
            )
        )
    )

    val attention = Overview(
        en = OverviewContent(
            title = "Multi-Head Self-Attention",
            formula = """
                |Scaled Dot-Product Attention:
                |
                |Attention(Q,K,V) = softmax(QK^T/√d_k + mask) * V
                |
                |Multi-Head: split into h heads, compute attention per head, concatenate
                |
                |head_i = Attention(x·Wq_i, x·Wk_i, x·Wv_i)
                |output  = concat(head_1,...,head_h) · Wo
                |
                |Causal mask: upper triangle = -∞ (position i can only see positions ≤ i)
            """.trimMargin(),
            algorithm = "Project input to Q/K/V → split into heads → apply RoPE → compute attention scores → scale by 1/√d_k → apply causal mask → softmax → weighted sum of values → concat heads → output projection.",
            why = "Attention allows each token to directly attend to all previous tokens. Multi-head lets the model focus on different representation subspaces simultaneously.",
            references = listOf(
                Reference("Attention Is All You Need (Vaswani et al., 2017)", "https://arxiv.org/abs/1706.03762"),
                Reference("The Annotated Transformer", "https://nlp.seas.harvard.edu/2018/04/03/attention.html"),
                Reference("3Blue1Brown Attention Video", "https://www.youtube.com/watch?v=eMlx5fFNoYc"),
                Reference("karpathy/nanoGPT Implementation", "https://github.com/karpathy/nanoGPT/blob/master/model.py")
            )
        ),
        zh = OverviewContent(
            title = "多头自注意力",
            formula = """
                |缩放点积注意力：
                |
                |Attention(Q,K,V) = softmax(QK^T/√d_k + mask) * V
                |
                |多头：分成 h 个头，每头计算注意力，拼接
                |
                |head_i = Attention(x·Wq_i, x·Wk_i, x·Wv_i)
                |output  = concat(head_1,...,head_h) · Wo
                |
                |因果掩码：上三角 = -∞（位置 i 只能看到位置 ≤ i）
            """.trimMargin(),
            algorithm = "投影输入到 Q/K/V → 分成多头 → 应用 RoPE → 计算注意力分数 → 除以 √d_k 缩放 → 因果掩码 → softmax → 值的加权和 → 拼接头 → 输出投影。",
            why = "注意力机制允许每个 token 直接关注所有之前的 token。多头让模型同时关注不同的表示子空间。",
            references = listOf(
                Reference("Attention 论文 (Vaswani et al., 2017)", "https://arxiv.org/abs/1706.03762"),
                Reference("图解 Transformer", "https://nlp.seas.harvard.edu/2018/04/03/attention.html"),
                Reference("3Blue1Brown 注意力视频", "https://www.youtube.com/watch?v=eMlx5fFNoYc"),
                Reference("karpathy/nanoGPT 实现", "https://github.com/karpathy/nanoGPT/blob/master/model.py")
            )
        )
    )

    val ffn = Overview(
        en = OverviewContent(
            title = "SwiGLU Feed-Forward Network",
            formula = """
                |SwiGLU(x) = (SiLU(x·W_gate) ⊙ x·W_up) · W_down
                |
                |where SiLU(x) = x * σ(x)  (Sigmoid Linear Unit)
                |
                |Shazeer (2020) showed SwiGLU outperforms ReLU FFN:
                |FFN_ReLU(x)  = ReLU(x·W1) · W2
                |FFN_SwiGLU(x) = SiLU(x·W1) ⊙ (x·W3) · W2
                |
                |Note: 3 weight matrices in SwiGLU vs 2 in ReLU FFN
            """.trimMargin(),
            algorithm = "Expand input from d → hidden_dim via two projections (gate + up). Apply SiLU to gate. Element-wise multiply gate ⊙ up. Project back to d via down projection.",
            why = "SwiGLU provides a learned gating mechanism: the gate projection determines which features pass through. This outperforms simple ReLU thresholding.",
            references = listOf(
                Reference("GLU Variants Paper (Shazeer, 2020)", "https://arxiv.org/abs/2002.05202"),
                Reference("SwiGLU in LLaMA", "https://arxiv.org/abs/2302.13971"),
                Reference("PaLM: SwiGLU at Scale", "https://arxiv.org/abs/2204.02311")
            )
        ),
        zh = OverviewContent(
            title = "SwiGLU 前馈网络",
            formula = """
                |SwiGLU(x) = (SiLU(x·W_gate) ⊙ x·W_up) · W_down
                |
                |其中 SiLU(x) = x * σ(x)（Sigmoid 线性单元）
                |
                |Shazeer (2020) 证明 SwiGLU 优于 ReLU FFN：
                |FFN_ReLU(x)  = ReLU(x·W1) · W2
                |FFN_SwiGLU(x) = SiLU(x·W1) ⊙ (x·W3) · W2
                |
                |注意：SwiGLU 有 3 个权重矩阵，ReLU FFN 有 2 个
            """.trimMargin(),
            algorithm = "通过两个投影将输入从 d 扩展到 hidden_dim（gate + up）。对 gate 应用 SiLU。逐元素乘 gate ⊙ up。通过 down 投影回到 d。",
            why = "SwiGLU 提供可学习的门控机制：gate 投影决定哪些特征通过。这优于简单的 ReLU 阈值。",
            references = listOf(
                Reference("GLU 变体论文 (Shazeer, 2020)", "https://arxiv.org/abs/2002.05202"),
                Reference("LLaMA 中的 SwiGLU", "https://arxiv.org/abs/2302.13971"),
                Reference("PaLM：大规模 SwiGLU", "https://arxiv.org/abs/2204.02311")
            )
        )
    )

    val transformer = Overview(
        en = OverviewContent(
            title = "Transformer Block",
            formula = """
                |Decoder-Only Transformer Block (Pre-LN):
                |
                |x = x + Attention(RMSNorm(x))
                |x = x + FFN(RMSNorm(x))
                |
                |Full model: N blocks stacked sequentially
                |
                |LLaMA-7B:   N=32, d=4096, h=32
                |LLaMA-13B:  N=40, d=5120, h=40
                |LLaMA-70B:  N=80, d=8192, h=64
            """.trimMargin(),
            algorithm = "For each block: normalize → attention with causal mask + RoPE → add residual → normalize → SwiGLU FFN → add residual. Output shape = input shape enables stacking.",
            why = "The Transformer block is the fundamental building block of modern LLMs. Pre-LN + residual connections enable stable training of very deep networks.",
            references = listOf(
                Reference("Attention Is All You Need", "https://arxiv.org/abs/1706.03762"),
                Reference("LLaMA: Open Foundation Models", "https://arxiv.org/abs/2302.13971"),
                Reference("DeepSeek-V3 Technical Report", "https://arxiv.org/abs/2412.19437"),
                Reference("nanoGPT (educational GPT-2)", "https://github.com/karpathy/nanoGPT")
            )
        ),
        zh = OverviewContent(
            title = "Transformer 模块",
            formula = """
                |仅解码器 Transformer 块（Pre-LN）：
                |
                |x = x + Attention(RMSNorm(x))
                |x = x + FFN(RMSNorm(x))
                |
                |完整模型：N 个块顺序堆叠
                |
                |LLaMA-7B:   N=32, d=4096, h=32
                |LLaMA-13B:  N=40, d=5120, h=40
                |LLaMA-70B:  N=80, d=8192, h=64
            """.trimMargin(),
            algorithm = "每块：归一化 → 带因果掩码 + RoPE 的注意力 → 加残差 → 归一化 → SwiGLU FFN → 加残差。输出形状 = 输入形状，可堆叠。",
            why = "Transformer 块是现代 LLM 的基本构建单元。Pre-LN + 残差连接使极深网络的训练稳定。",
            references = listOf(
                Reference("Attention Is All You Need", "https://arxiv.org/abs/1706.03762"),
                Reference("LLaMA：开放基础模型", "https://arxiv.org/abs/2302.13971"),
                Reference("DeepSeek-V3 技术报告", "https://arxiv.org/abs/2412.19437"),
                Reference("nanoGPT（教育版 GPT-2）", "https://github.com/karpathy/nanoGPT")
            )
        )
    )

    fun getOverview(componentId: String): Overview? = when (componentId) {
        "bpe" -> bpe
        "rmsnorm" -> rmsnorm
        "rope" -> rope
        "attention" -> attention
        "ffn" -> ffn
        "transformer" -> transformer
        else -> null
    }
}
