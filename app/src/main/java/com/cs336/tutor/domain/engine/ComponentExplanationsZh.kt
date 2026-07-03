package com.cs336.tutor.domain.engine

object ComponentExplanationsZh {
    
    val rmsnorm = mapOf(
        1 to "导入 PyTorch 神经网络模块，提供 nn.Module 基类和 nn.Parameter。",
        2 to "导入 PyTorch，用于 pow、rsqrt、ones 等张量操作。",
        4 to "将 RMSNorm 定义为 PyTorch 模块。继承 nn.Module 使其可作为网络层使用。",
        5 to "构造函数。dim=隐藏维度（如 7B 模型为 4096）。eps=防止除以零的小值，通常为 1e-6。",
        6 to "调用父类 nn.Module 构造函数完成模块注册。",
        7 to "将 epsilon 存储为实例变量，供 _norm() 和 forward() 使用。",
        8 to "关键行：可学习缩放参数。初始化为全 1 使 RMSNorm 从恒等变换开始。无 bias——仅单一权重向量。",
        10 to "计算 RMS 归一化的私有辅助方法。与 forward() 分离以保持类型转换逻辑清晰。",
        11 to "核心公式：RMSNorm(x)=x/sqrt(mean(x²)+ε)。平方→均值→+ε→rsqrt→乘以 x。keepdim=True 保持维度用于广播。rsqrt 比 1/sqrt() 更高效。",
        13 to "前向传播。转为 float 以保证数值稳定，应用 RMS 归一化，转回原 dtype，乘以 w。",
        14 to "转为 float32（保证计算稳定），归一化，转回原 dtype。处理混合精度训练（fp16/bf16）。",
        15 to "乘以可学习权重 w。RMSNorm 唯一可学习参数——让网络学习归一化后应强调或抑制哪些特征。"
    )
    
    val rope = mapOf(
        1 to "导入 PyTorch。",
        2 to "导入 math，用于 log 和三角函数。",
        4 to "为所有位置预计算复指数。仅初始化时运行一次。返回 cos+sin 值作为复数 (cis)。",
        5 to "关键行：对数尺度计算频率。低维→高频（近距离位置信息），高维→低频（远距离信息）。",
        6 to "创建位置索引 [0,1,...,end-1]。",
        7 to "外积：每个位置×每个频率→形状 (seq_len, dim//2)。",
        8 to "转为复数：polar(1,angle)=e^(i·angle)=cos(angle)+i·sin(angle)。",
        9 to "返回所有位置的预计算复指数。",
        11 to "将旋转嵌入应用到 Q 和 K 张量。(batch,seq_len,n_heads,head_dim)。",
        12 to "重塑为对并视为复数——(real,imag)对→复数，实现高效旋转。",
        14 to "Key 同样转为复数表示。",
        16 to "旋转 Q：复数 Q × 复数 cis→按位置角度旋转。然后转回实数。",
        18 to "旋转 K：同样操作。将相对位置信息编码到点积 Q·K 中。",
        20 to "转回原始 dtype。"
    )
    
    val attention = mapOf(
        1 to "导入 PyTorch 的 nn 模块。",
        2 to "导入 functional API（softmax 用）。",
        4 to "多头因果自注意力模块。",
        5 to "dim=模型维度，n_heads=注意力头数。head_dim=dim//n_heads。",
        6 to "调用父类 nn.Module 构造函数。",
        7 to "存储头数。",
        8 to "每个头在 dim//n_heads 维子空间中操作。",
        9 to "Q 投影。bias=False 因为使用 RoPE（bias 会给每个位置加相同值）。",
        10 to "K 投影。形状同 query。",
        11 to "V 投影。",
        12 to "输出投影融合所有头的输出。",
        14 to "前向传播。x:(batch,seq_len,dim)。freqs_cis:预计算的 RoPE 频率。",
        15 to "解包 batch、seq 长度和模型维度。",
        17 to "投影 queries 并重塑：(batch,seq,dim)→(batch,seq,n_heads,head_dim)。",
        18 to "Keys 同样操作。",
        19 to "Values 同样操作。",
        21 to "对 Q 和 K 应用 RoPE——将位置编码到注意力分数中。",
        23 to "核心公式：缩放点积 Q·K^T/√d_k 防止 softmax 在大维度饱和。",
        25 to "因果掩码（上三角=-inf）：位置 i 只能关注位置≤i。",
        26 to "归一化注意力权重和为 1。每行是对过去位置的概率分布。",
        28 to "值的加权和：每个位置组合所有可关注位置的值。",
        29 to "重塑回：(batch,n_heads,seq,head_dim)→(batch,seq,dim)。",
        30 to "输出投影融合所有头的信息。"
    )
    
    val ffn = mapOf(
        1 to "导入 PyTorch nn 模块。",
        2 to "导入 F（SiLU 在 F.silu() 中）。",
        4 to "SwiGLU 前馈网络：扩展到 hidden_dim，应用门控激活，投影回原始维度。",
        5 to "dim=模型维度（如 4096）。hidden_dim 通常=8/3*dim≈14336（7B 模型）。",
        6 to "调用父类构造函数。",
        7 to "默认 4x 扩展。LLaMA 风格用 8/3*dim，但 4x 是经典 Transformer 默认值。",
        8 to "门控投影：产生门控值。",
        9 to "上投影：产生待门控的值。",
        10 to "下投影：投影回模型维度。",
        12 to "SwiGLU 前向：gate=SiLU(x·w1), up=x·w3, output=(gate⊙up)·w2。",
        13 to "关键行：完整 SwiGLU 公式！SiLU(w1·x)作为 w3·x 的学习门控，w2 投影回。*是元素乘法——门控激活不同于简单阈值 ReLU。"
    )
    
    val transformer = mapOf(
        1 to "导入 PyTorch nn 模块。",
        2 to "导入 RMSNorm 实现。",
        3 to "导入 Attention 实现。",
        4 to "导入 FeedForward 实现。",
        6 to "仅解码器 Transformer 的完整构建块。通过注意力+FFN 转换隐藏状态。",
        7 to "初始化所有子组件：2个 RMSNorm、1个 Attention、1个 FFN。",
        8 to "调用父类构造函数。",
        9 to "多头自注意力子层。",
        10 to "SwiGLU 前馈子层。",
        11 to "注意力前归一化（Pre-LN 架构）。注意力之前归一化，非之后。现代 LLaMA 风格模型。",
        12 to "FFN 前归一化。同样的模式：子层之前归一化。",
        14 to "完整前向传播：norm→attn→+残差→norm→ffn→+残差。",
        16 to "关键行：Pre-LN 注意力+残差。(1)RMSNorm归一化输入(2)运行RoPE注意力(3)加残差:h=x+attn(norm(x))。残差让梯度直接流过网络不消失。",
        18 to "FFN 同样：归一化→FFN→加残差。out=h+ffn(norm(h))。",
        19 to "输出形状同输入(batch,seq_len,dim)，即可堆叠多个 block。"
    )
    

    // Hint translations (keyed by line number)
    val bpeHints = mapOf(
        2 to listOf("Counter 类似 dict，但对缺失的键默认返回 0"),
        7 to listOf("也可用 defaultdict(int) 让代码更简洁"),
        9 to listOf("等价于: if pair in counts: counts[pair] += 1 else: counts[pair] = 1"),
        15 to listOf("也可用 for 循环加手动索引，但 while 更清晰"),
        16 to listOf("i < len(ids) - 1 防止在最后一个元素出现 IndexError"),
        29 to listOf("用 OrderedDict 保留合并顺序"),
        33 to listOf("O(n) 每次迭代 — 考虑为长文本优化"),
        36 to listOf("两个 pair 计数相同时会怎样？这是确定性的吗？"),
        39 to listOf("合并的 ORDER 对正确编码至关重要"),
        49 to listOf("存储 merges 并立即调用 _build_vocab()"),
        53 to listOf("vocab[idx] = vocab[p0] + vocab[p1] 递归构建字节字符串"),
        61 to listOf("按优先级顺序应用合并，而非按频率"),
        67 to listOf("为什么是 min 不是 max？因为索引越小=越早合并=优先级越高")
    )

    val rmsnormHints = mapOf(
        4 to listOf("nn.Module 提供 .parameters()、.to(device)、.train()/.eval()"),
        5 to listOf("eps 通常为 1e-6 或 1e-5"),
        8 to listOf("nn.Parameter 告诉 PyTorch 此张量应在训练中更新", "为什么没有 bias？RMSNorm 隐式归一化为零均值"),
        10 to listOf("使用私有方法是一种设计选择——也可内联到 forward() 中"),
        11 to listOf("keepdim=True 保持维度用于广播", "rsqrt 比 1/sqrt() 更高效"),
        14 to listOf("为什么 float()？fp16 中 RMS 可能产生 NaN 而不进行此转换")
    )


    val ropeHints = mapOf(
        5 to listOf("为什么是 10000.0？这是原始 Transformer 论文的惯例"),
        7 to listOf("外积：为每个位置一次性计算所有频率"),
        8 to listOf("torch.polar 从幅度和角度创建复数"),
        12 to listOf("view_as_complex 将浮点数对视为单个复数")
    )

    val attentionHints = mapOf(
        9 to listOf("为什么 bias=False？RoPE 已编码位置——bias 会产生冲突"),
        23 to listOf("sqrt(d_k) 缩放防止点积在大维度时变得过大"),
        26 to listOf("dim=-1 表示沿键维度对每个 query 归一化"),
        29 to listOf("transpose 后需要 contiguous() 才能使用 view()")
    )

    val ffnHints = mapOf(
        7 to listOf("LLaMA 使用 8/3*dim 代替 4*dim 以获得更高效率"),
        10 to listOf("为什么 bias=False？现代 LLM 在所有 Linear 层中都移除了 bias"),
        13 to listOf("元素乘法（*）使其成为门控激活")
    )

    val trainingHints = mapOf(
        6 to listOf("忘记 zero_grad() 是常见 bug——loss 不会下降"),
        8 to listOf("logits 形状 (batch,seq,vocab_size)——每个位置预测下一个 token"),
        11 to listOf("view(-1, vocab_size) 展平批次和序列维度")
    )
    val lmheadHints = mapOf(
        6 to listOf("权重共享：LM Head 权重通常与 Embedding 共享以节省参数")
    )
    val transformerHints = mapOf(
        11 to listOf("Pre-LN vs Post-LN：Pre-LN 对训练深层网络更稳定"),
        16 to listOf("残差连接至关重要——没有它们，深层网络中梯度会消失"),
        18 to listOf("输出现在可以被输入到下一个 TransformerBlock")
    )


    val embedding = mapOf(
        3 to "Token 嵌入层：将整数 token ID 映射为 dim 维稠密向量，捕获语义含义。",
        4 to "vocab_size=唯一 token 数。dim=嵌入维度（7B 模型为 4096）。",
        6 to "关键行：PyTorch 内置 Embedding 查找。存储 (vocab_size,dim) 权重矩阵。",
        8 to "前向传播：x 为 token 索引 (batch,seq_len)，返回嵌入 (batch,seq_len,dim)。",
        9 to "按 token ID 查找嵌入向量。训练时梯度通过被选中行反向传播。"
    )

    val lmhead = mapOf(
        3 to "最后一层：隐藏状态→token 预测。(batch,seq,dim)→(batch,seq,vocab_size)。",
        4 to "dim=模型维度，vocab_size=词汇表大小。",
        6 to "关键行：从模型维度到词汇表的线性投影。bias=False（现代做法）。常与 Embedding 层共享权重。",
        8 to "前向传播：将每个位置的隐藏状态投影到词汇表 logits。",
        9 to "输出 (batch,seq,vocab_size)。每个位置对每个 token 有一个分数——最高分即预测词。"
    )

    val optimizer = mapOf(
        3 to "关键行：AdamW——动量+自适应学习率+解耦权重衰减。lr=3e-4（7B 模型标准）。betas 控制动量衰减。weight_decay=0.1 正则化。",
        5 to "经典 Adam（无权重衰减）。较简单但对大模型效果较差。",
        8 to "第一矩（梯度均值）：平滑噪声梯度，类似滚动平均。",
        9 to "第二矩（未中心化方差）：为每个参数自适应调整学习率。",
        10 to "参数更新：动量除以自适应学习率的平方根。每个参数有独立的等效学习率。"
    )

    val training = mapOf(
        4 to "一个完整训练步骤：前向→损失→反向→更新。batch=(input_ids,target_ids)。",
        5 to "设置模型为训练模式，启用 dropout 等。",
        6 to "关键：反向传播前清零所有梯度。忘记此步会导致梯度累积——常见 bug！",
        8 to "前向传播通过整个模型。logits 形状：(batch,seq,vocab_size)——每位置预测下一 token。",
        10 to "关键行：标准语言建模损失。比较预测分布与实际下一 token。",
        11 to "展平 logits：(batch*seq,vocab_size)。独立预测每个 token。",
        12 to "展平目标：(batch*seq,)。必须匹配 logits 第一维。",
        15 to "反向传播：通过自动微分计算所有参数的梯度。",
        16 to "应用梯度：使用优化器规则（Adam/SGD 等）更新所有参数。",
        17 to "返回标量损失值用于日志记录和监控。"
    )

    val lmheadHints = mapOf(
        6 to listOf("权重共享：LM Head 权重通常与 Embedding 共享以节省参数")
    )

    val trainingHints = mapOf(
        6 to listOf("忘记 zero_grad() 是常见 bug——loss 不会下降"),
        8 to listOf("logits 形状 (batch,seq,vocab_size)——每个位置预测下一个 token"),
        11 to listOf("view(-1, vocab_size) 展平批次和序列维度")
    )

    fun getHints(componentId: String): Map<Int, List<String>> = when (componentId) {
        "bpe" -> bpeHints; "rmsnorm" -> rmsnormHints; "rope" -> ropeHints
        "attention" -> attentionHints; "ffn" -> ffnHints
        "transformer" -> transformerHints; "lmhead" -> lmheadHints; "training" -> trainingHints
        else -> emptyMap()
    }

fun getExplanations(componentId: String): Map<Int, String> = when (componentId) {
        "bpe" -> BPEExplanationsZh.explanations; "embedding" -> embedding
        "rmsnorm" -> rmsnorm; "rope" -> rope; "attention" -> attention
        "ffn" -> ffn; "transformer" -> transformer
        "lmhead" -> lmhead; "optimizer" -> optimizer; "training" -> training
        else -> emptyMap()
    }
}
