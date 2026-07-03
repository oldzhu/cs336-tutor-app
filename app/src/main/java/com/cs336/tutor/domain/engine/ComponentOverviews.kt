package com.cs336.tutor.domain.engine

object ComponentOverviews {

    data class Overview(val en: OverviewContent, val zh: OverviewContent)
    data class OverviewContent(
        val title: String, val formula: String, val algorithm: String,
        val purpose: String, val usage: String, val without: String,
        val why: String, val references: List<Reference>
    )
    data class Reference(val label: String, val url: String)

    private fun ref(label: String, url: String) = Reference(label, url)

    val bpe = Overview(
        en = OverviewContent("BPE Tokenization", """
Algorithm: Byte-Pair Encoding (Sennrich et al., 2016)
1. Start: each byte (0-255) is a token
2. Count: freq(a,b) for all adjacent pairs
3. Merge: replace most frequent pair → new token
4. Repeat until vocab_size reached
Encoding: text → bytes → apply merges in order → token IDs
Decoding: token IDs → lookup bytes → UTF-8 string""".trimIndent(),
            "Greedy frequency-based merge. Each iteration picks the pair with highest count.",
            "Converts raw text into integer token IDs. Splits text into subword units balancing vocabulary size with coverage.",
            "Applied to ALL text before entering the model. Training text is tokenized to learn merge rules. Output feeds into Embedding layer.",
            "Without BPE: one token per unique word (impossibly large vocabulary), or character-level tokens (extremely long sequences, poor efficiency).",
            "Modern LLMs use tokenization to balance vocabulary size with sequence length. BPE handles rare words gracefully via subword decomposition.",
            listOf(ref("BPE Paper (Sennrich et al.)","https://arxiv.org/abs/1508.07909"), ref("Karpathy's minBPE","https://github.com/karpathy/minbpe"))),
        zh = OverviewContent("BPE 分词", """
Byte-Pair Encoding (Sennrich et al., 2016)
1. 起始：每个字节(0-255)是一个 token
2. 统计：所有相邻对频率 freq(a,b)
3. 合并：最频繁对→新 token
4. 重复直到词汇表大小达标
编码：文本→字节→按顺序合并→token ID
解码：token ID→查找字节→UTF-8""".trimIndent(),
            "贪心频率合并。每次迭代选计数最高的对。",
            "将原始文本转为整数 token ID。分割为子词单元，平衡词汇大小与覆盖范围。",
            "应用于所有进入模型的文本。训练文本被分词以学习规则，输出进入 Embedding 层。",
            "没有 BPE：每个单词一个 token（词汇表过大），或用字符级（序列极长，效率差）。",
            "现代 LLM 用分词平衡词汇大小和序列长度。BPE 通过子词分解优雅处理罕见词。",
            listOf(ref("BPE 论文 (Sennrich et al.)","https://arxiv.org/abs/1508.07909"), ref("Karpathy 的 minBPE","https://github.com/karpathy/minbpe")))
    )

    val embedding = Overview(
        en = OverviewContent("Embedding Layer", """
Embedding: token_id → dense vector of dimension dim
Weight matrix: (vocab_size × dim)
Output shape: (batch, seq_len) → (batch, seq_len, dim)""".trimIndent(),
            "Lookup each token ID in the embedding table. Gradients flow back through selected rows during training.",
            "Maps discrete token IDs to continuous dense vectors that capture semantic meaning.",
            "Token IDs from BPE → Embedding lookup → dense vectors. Input to first Transformer block.",
            "Without embeddings, tokens remain meaningless integers. One-hot encoding is too sparse and high-dimensional.",
            "Similar words get similar vectors, enabling the model to generalize across vocabulary.",
            listOf(ref("Word Embeddings Explained","https://arxiv.org/abs/1301.3781"))
        ),
        zh = OverviewContent("Embedding 层", """
Embedding: token_id → dim 维稠密向量
权重矩阵：(vocab_size × dim)
输出：(batch, seq_len) → (batch, seq_len, dim)""".trimIndent(),
            "在 embedding 表中查找每个 token ID。训练时梯度通过被选中行反向传播。",
            "将离散 token ID 映射为捕获语义的连续稠密向量。",
            "BPE 的 token ID → Embedding → 稠密向量。作为第一个 Transformer 块的输入。",
            "没有 embedding：token 是无意义整数。独热编码过于稀疏且高维。",
            "相似词获得相似向量，使模型能跨词汇泛化。",
            listOf(ref("词嵌入详解","https://arxiv.org/abs/1301.3781")))
    )

    val rmsnorm = Overview(
        en = OverviewContent("RMS Normalization", """
RMSNorm(x) = x / RMS(x) * w
RMS(x) = sqrt(mean(x²)+ε)
Complexity: O(d) vs LayerNorm O(2d)
Key: no mean subtraction, no bias""".trimIndent(),
            "Compute RMS along last dim → scale by 1/RMS → multiply by learnable weight w.",
            "Stabilizes training by normalizing activations. Prevents internal covariate shift.",
            "Applied before attention and FFN sub-layers (Pre-LN architecture).",
            "Without normalization: activations explode/vanish in deep networks. Training unstable, gradients diverge.",
            "Faster than LayerNorm (~15%), same quality. Used in LLaMA, Mistral, DeepSeek.",
            listOf(ref("RMSNorm Paper","https://arxiv.org/abs/1910.07467"), ref("LLaMA Paper","https://arxiv.org/abs/2302.13971"))
        ),
        zh = OverviewContent("RMS 归一化", """
RMSNorm(x) = x / RMS(x) * w
RMS(x) = sqrt(mean(x²)+ε)
复杂度 O(d) vs LayerNorm O(2d)
关键：无均值减法，无 bias""".trimIndent(),
            "沿最后一维计算 RMS → 除以 RMS 缩放 → 乘可学习权重 w。",
            "通过归一化激活值稳定训练，防止内部协变量偏移。",
            "在注意力和 FFN 子层前应用（Pre-LN 架构）。",
            "无归一化：深网络中激活值爆炸/消失。训练不稳定，梯度发散。",
            "比 LayerNorm 快约 15%，效果相同。用于 LLaMA、Mistral、DeepSeek。",
            listOf(ref("RMSNorm 论文","https://arxiv.org/abs/1910.07467"), ref("LLaMA 论文","https://arxiv.org/abs/2302.13971")))
    )

    val rope = Overview(
        en = OverviewContent("Rotary Position Embedding", """
RoPE rotates Q,K by position-dependent angles:
f(q,m)=q·e^(imθ), f(k,n)=k·e^(inθ)
After rotation: Q·K = g(m-n) (relative only!)
Frequency: θ_i = 1/10000^(2i/d)""".trimIndent(),
            "Precompute cos/sin via log-scale freqs. During attention, rotate Q and K via complex multiply.",
            "Encodes position into attention. Without it, 'A loves B' = 'B loves A' (invariant).",
            "Applied to Q and K before attention. Q·K naturally captures relative token distance.",
            "Without RoPE: model can't distinguish word order. Language understanding collapses.",
            "Encodes relative position directly into dot product. More efficient than learned embeddings for long sequences.",
            listOf(ref("RoPE Paper","https://arxiv.org/abs/2104.09864"), ref("LLaMA RoPE Code","https://github.com/meta-llama/llama")))
        ),
        zh = OverviewContent("旋转位置编码 (RoPE)", """
RoPE 按位置角度旋转 Q,K：
f(q,m)=q·e^(imθ), f(k,n)=k·e^(inθ)
旋转后 Q·K=g(m-n)（仅相对位置！）
频率 θ_i=1/10000^(2i/d)""".trimIndent(),
            "对数尺度预计算 cos/sin。注意力中通过复数乘法旋转 Q 和 K。",
            "将位置编码到注意力中。否则'A 爱 B'='B 爱 A'（排列不变）。",
            "在注意力计算前应用于 Q 和 K。Q·K 自然捕获相对距离。",
            "无 RoPE：模型无法区分词序。语言理解崩溃。",
            "将相对位置直接编码到点积。长序列比学习式嵌入更高效。",
            listOf(ref("RoPE 论文","https://arxiv.org/abs/2104.09864"), ref("LLaMA RoPE 实现","https://github.com/meta-llama/llama")))
    )

    val attention = Overview(
        en = OverviewContent("Multi-Head Self-Attention", """
Attention(Q,K,V) = softmax(QK^T/√d_k + mask)·V
Multi-head: split into h heads, concat, project
Causal mask: upper △ = -∞ (can't see future)""".trimIndent(),
            "Project→split→RoPE→scores/√d_k→mask→softmax→weighted V→concat→project.",
            "Each token attends to all previous tokens. Core of Transformer — replaces recurrence.",
            "Every Transformer block has one attention layer. Captures token relationships in parallel.",
            "Without attention: tokens only see themselves. Bag-of-words. No context, no grammar, no LLM.",
            "Enables parallel processing of all positions. Multi-head captures different relationship types.",
            listOf(ref("Attention Paper","https://arxiv.org/abs/1706.03762"), ref("nanoGPT","https://github.com/karpathy/nanoGPT")))
        ),
        zh = OverviewContent("多头自注意力", """
Attention(Q,K,V) = softmax(QK^T/√d_k + mask)·V
多头：分成 h 头，拼接，投影
因果掩码：上△=-∞（看不到未来）""".trimIndent(),
            "投影→分头→RoPE→分数/√d_k→掩码→softmax→加权V→拼接→投影。",
            "每个 token 关注所有之前 token。Transformer 核心——替代循环。",
            "每个 Transformer 块有一个注意力层。并行捕获 token 关系。",
            "无注意力：token 只看到自己。词袋模型。无上下文、无语法的 LLM。",
            "并行处理所有位置。多头捕获不同类型的关系。",
            listOf(ref("Attention 论文","https://arxiv.org/abs/1706.03762"), ref("nanoGPT","https://github.com/karpathy/nanoGPT")))
    )

    val ffn = Overview(
        en = OverviewContent("SwiGLU Feed-Forward", """
SwiGLU(x) = (SiLU(x·W_g) ⊙ x·W_u) · W_d
SiLU(x) = x·σ(x)
vs ReLU: SwiGLU has learned gating, not fixed threshold
~2/3 of model params live here""".trimIndent(),
            "Expand d→hidden_dim (2 projections: gate+up). SiLU(gate)⊙up. Project back d_dim→d.",
            "Adds non-linear capacity. After attention mixes tokens, FFN processes each independently.",
            "Applied after attention in every block. Takes ~2/3 of total model parameters.",
            "Without FFN: only linear transforms. No complex patterns. Perplexity spikes dramatically.",
            "SwiGLU provides learned gating — the gate projection determines which features pass through.",
            listOf(ref("GLU Variants (Shazeer)","https://arxiv.org/abs/2002.05202"), ref("PaLM: SwiGLU at Scale","https://arxiv.org/abs/2204.02311")))
        ),
        zh = OverviewContent("SwiGLU 前馈网络", """
SwiGLU(x)=(SiLU(x·W_g)⊙x·W_u)·W_d
SiLU(x)=x·σ(x)
vs ReLU：SwiGLU 有学习门控，非固定阈值
约 2/3 模型参数在此""".trimIndent(),
            "扩展 d→hidden_dim（2 投影：gate+up）。SiLU(gate)⊙up。投影回 d。",
            "增加非线性能力。注意力混合 token 后，FFN 独立处理每个位置。",
            "每个块中在注意力后应用。约占模型总参数 2/3。",
            "无 FFN：仅有线性变换。无法学习复杂模式。困惑度急剧上升。",
            "SwiGLU 提供学习门控——gate 投影决定哪些特征通过。",
            listOf(ref("GLU 变体 (Shazeer)","https://arxiv.org/abs/2002.05202"), ref("PaLM: 大规模 SwiGLU","https://arxiv.org/abs/2204.02311")))
    )

    val transformer = Overview(
        en = OverviewContent("Transformer Block", """
Pre-LN Decoder Block:
x = x + Attention(RMSNorm(x))
x = x + FFN(RMSNorm(x))
LLaMA-7B: N=32,d=4096  LLaMA-70B: N=80,d=8192""".trimIndent(),
            "Norm→Attn→+residual→Norm→FFN→+residual. Output shape=input shape→stackable.",
            "Fundamental building block. Stacked N times for hierarchical feature learning.",
            "Blocks stacked sequentially. Output of block N = input of block N+1.",
            "Without it: no model. It IS the architecture. Block quality = model quality.",
            "Pre-LN + residuals enable stable training of very deep networks.",
            listOf(ref("Attention is All You Need","https://arxiv.org/abs/1706.03762"), ref("DeepSeek-V3","https://arxiv.org/abs/2412.19437")))
        ),
        zh = OverviewContent("Transformer 块", """
Pre-LN 解码器块：
x = x + Attention(RMSNorm(x))
x = x + FFN(RMSNorm(x))
LLaMA-7B: N=32,d=4096  LLaMA-70B: N=80,d=8192""".trimIndent(),
            "归一化→注意力→+残差→归一化→FFN→+残差。输出=输入形状→可堆叠。",
            "基本构建块。堆叠 N 次实现层次化特征学习。",
            "块顺序堆叠。块 N 的输出=块 N+1 的输入。",
            "没有它就没有模型。它本身就是架构。块质量=模型质量。",
            "Pre-LN+残差连接使极深网络可稳定训练。",
            listOf(ref("Attention 论文","https://arxiv.org/abs/1706.03762"), ref("DeepSeek-V3","https://arxiv.org/abs/2412.19437")))
    )

    val training = Overview(
        en = OverviewContent("Training Loop", """
One step: forward→loss→backward→update
Loss = CrossEntropy(logits, targets)
Gradient: ∂L/∂W via autograd
Update: W = W - lr * ∇L (Adam)""".trimIndent(),
            "zero_grad→forward→cross_entropy→backward→optimizer.step. Repeat millions of times.",
            "Learning process that adjusts all parameters to minimize prediction error.",
            "For each batch of text: predict next token, compute error, backprop gradients, update weights.",
            "Without training: random weights → random outputs. Zero knowledge. Architecture→intelligence via training.",
            "Training transforms random architecture into language understanding through exposure to billions of tokens.",
            listOf(ref("Adam Optimizer","https://arxiv.org/abs/1412.6980"), ref("GPT-3 Training","https://arxiv.org/abs/2005.14165")))
        ),
        zh = OverviewContent("训练循环", """
一步：前向→损失→反向→更新
Loss=CrossEntropy(logits,targets)
梯度：∂L/∂W（自动微分）
更新：W=W-lr·∇L（Adam）""".trimIndent(),
            "zero_grad→前向→交叉熵→反向→optimizer.step。重复数百万次。",
            "调整所有参数以最小化预测误差的学习过程。",
            "每批文本：预测下一个 token，计算误差，反向传播梯度，更新权重。",
            "无训练：随机权重→随机输出。零知识。通过训练将架构转化为智能。",
            "训练通过接触数十亿 token 将随机架构转化为语言理解。",
            listOf(ref("Adam 优化器","https://arxiv.org/abs/1412.6980"), ref("GPT-3 训练","https://arxiv.org/abs/2005.14165")))
    )

    fun getOverview(componentId: String): Overview? = when (componentId) {
        "bpe" -> bpe; "embedding" -> embedding; "rmsnorm" -> rmsnorm
        "rope" -> rope; "attention" -> attention; "ffn" -> ffn
        "transformer" -> transformer; "training" -> training
        else -> null
    }
}
