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
    
    fun getExplanations(componentId: String): Map<Int, String> = when (componentId) {
        "bpe" -> BPEExplanationsZh.explanations
        "rmsnorm" -> rmsnorm
        "rope" -> rope
        "attention" -> attention
        "ffn" -> ffn
        "transformer" -> transformer
        else -> emptyMap()
    }
}
