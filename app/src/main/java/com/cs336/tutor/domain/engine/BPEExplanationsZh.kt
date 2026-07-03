package com.cs336.tutor.domain.engine

/**
 * Chinese explanations for BPE Tokenizer component.
 * Keyed by line number. Applied by SplitScreenTutorViewModel when language is Chinese.
 */
object BPEExplanationsZh {
    val explanations: Map<Int, String> = mapOf(
        1 to "导入 Python 正则表达式模块，用于文本预处理（按空格和标点分词）。GPT-2 的分词器使用正则表达式进行预分词。",
        2 to "Counter 高效统计频率；OrderedDict 保留合并顺序 — BPE 合并必须按照学习顺序精确应用。",
        3 to "类型注解让代码更清晰，帮助 AI 导师理解代码结构。",
        5 to "BPE 训练的核心函数。统计每个相邻 token 对的出现次数，出现最频繁的对将被合并为新 token。",
        7 to "初始化空字典。键为 (token_i, token_i+1) 元组，值为整数计数。",
        8 to "zip(ids, ids[1:]) 创建滑动窗口对。这是 Python 遍历连续元素的优雅方式。",
        9 to "dict.get(pair, 0) 返回当前计数，如果该对尚未出现则返回 0，然后加 1。",
        12 to "核心 BPE 合并操作。给定 token ID 列表和要替换的 pair，替换每个 (a,b) 为新的 token id。",
        13 to "从头构建新列表，避免原地修改的索引偏移错误。",
        14 to "手动索引跟踪——合并时跳过 2 个位置，未合并时跳过 1 个位置。",
        16 to "检查三个条件：存在下一个 token、当前匹配 pair[0]、下一个匹配 pair[1]。全部为真才合并。",
        17 to "用新的合并 token ID (idx >= 256) 替换匹配对——这是词汇表增长的地方。",
        18 to "跳过刚刚合并的两个 token，消耗两个输入产生一个输出。",
        20 to "保留当前 token 不变。",
        24 to "主训练循环。接收文本，迭代学习 BPE 合并规则直到词汇表达到目标大小。",
        27 to "将文本转为 UTF-8 字节再转为整数列表 (0-255)。每个字符变 1-4 字节，BPE 会学习压缩常见字节对。",
        29 to "存储学习到的合并规则。键: (token_a, token_b) 对，值: 新的 token ID。",
        30 to "要创建多少新 token。若 vocab_size=300，只需 44 次合并（300-256）。",
        32 to "主循环——每次迭代：找到最频繁的 pair → 合并 → 记录规则。",
        33 to "统计当前序列中所有相邻 pair。这是 BPE 最耗时的部分——每个 token 扫描一次。",
        36 to "关键行：找到出现频率最高的 pair。max(stats, key=stats.get)。",
        37 to "分配新 token ID。基础 token 0-255，第一个新 token 从 256 开始。",
        38 to "执行合并：将 pair 所有出现替换为新 token，产生稍短的序列。",
        39 to "记录合并规则——编码时必须按此顺序应用。顺序很重要！",
        46 to "将合并规则封装为可用的 tokenizer，提供 encode (文本→token) 和 decode (token→文本) 方法。",
        49 to "使用 train_bpe() 学习的 merges 初始化。构造函数预计算词汇表以便快速解码。",
        53 to "预计算每个 token ID 解码为何种原始字节。从基础 token 开始逐步构建。",
        56 to "基础词汇表：token 65 → b'A'，token 32 → b' ' 等。每个字节映射到其单字节表示。",
        58 to "关键行：递归构建词汇表。合并 token = bytes(subtoken_a) + bytes(subtoken_b)。",
        61 to "使用已学习的 BPE 合并将文本转换为 token ID。从字节开始，按索引优先级应用合并。",
        63 to "与训练相同：文本 → UTF-8 字节 → 整数列表 (0-255)。",
        67 to "关键行：找到合并索引最小的 pair——按学习顺序应用，最早的优先级最高。float('inf') 处理未见过的 pair。",
        71 to "应用合并，每次替换缩短序列。",
        73 to "将 token ID 转回可读文本。查找每个 token 的字节表示，连接后解码。",
        77 to "连接所有字节块，解码为 UTF-8 文本。errors='replace' 优雅处理无效字节。"
    )
}
