package com.cs336.tutor.domain.engine

import com.cs336.tutor.domain.model.CodeLineStub
import com.cs336.tutor.domain.model.ComponentSpec
import com.cs336.tutor.domain.model.Exercise
import com.cs336.tutor.domain.model.JudgeCriterion

/**
 * BPE Tokenizer component specification for CS336 Assignment 1.
 * Code lines are extracted from the reference Python implementation bundled
 * with the app at app/src/main/python/bpe_tokenizer.py.
 *
 * Each CodeLineStub represents a line of code the student must understand
 * and implement, with an explanation of what that line does.
 */
object BPEComponent {
    val spec = ComponentSpec(
        id = "bpe",
        name = "BPE Tokenizer",
        description = "Byte-Pair Encoding — subword tokenization from scratch. " +
                "Learn how modern LLMs break text into tokens.",
        prerequisites = emptyList(),
        codeLines = listOf(
            // ── Section 1: Imports and Setup ──
            CodeLineStub(
                lineNumber = 1,
                code = "import re",
                explanation = "Import Python's regular expression module. " +
                        "Used for text preprocessing (e.g., splitting on whitespace/punctuation). " +
                        "GPT-2's tokenizer uses regex for pre-tokenization."
            ),
            CodeLineStub(
                lineNumber = 2,
                code = "from collections import Counter, OrderedDict",
                explanation = "Counter efficiently counts frequencies; OrderedDict preserves " +
                        "merge order — critical because BPE merges must be applied in the exact " +
                        "order they were learned.",
                isEditable = false,
                hints = listOf("Counter is like a dict but defaults missing keys to 0")
            ),
            CodeLineStub(
                lineNumber = 3,
                code = "from typing import Dict, List, Optional, Tuple",
                explanation = "Type annotations for cleaner, more readable code. " +
                        "Helps the AI tutor understand your code structure.",
                isEditable = false
            ),

            // ── Section 2: get_stats ──
            CodeLineStub(
                lineNumber = 5,
                code = "def get_stats(ids: List[int]) -> Dict[Tuple[int, int], int]:",
                explanation = "The heart of BPE training. This function counts how many times " +
                        "each adjacent pair of tokens appears. The most frequent pair will be " +
                        "merged into a new token."
            ),
            CodeLineStub(
                lineNumber = 6,
                code = "    \"\"\"Count frequency of each consecutive pair of token IDs.\"\"\"",
                explanation = "Docstring describing the function. Always document your code — " +
                        "it helps the AI tutor and your future self!",
                isEditable = false
            ),
            CodeLineStub(
                lineNumber = 7,
                code = "    counts = {}",
                explanation = "Initialize an empty dictionary. Keys will be (token_i, token_i+1) " +
                        "tuples; values will be integer counts.",
                hints = listOf("You can also use defaultdict(int) for cleaner code")
            ),
            CodeLineStub(
                lineNumber = 8,
                code = "    for pair in zip(ids, ids[1:]):",
                explanation = "zip(ids, ids[1:]) creates sliding window pairs: " +
                        "(ids[0],ids[1]), (ids[1],ids[2]), etc. This is Python's elegant way " +
                        "to iterate over consecutive elements."
            ),
            CodeLineStub(
                lineNumber = 9,
                code = "        counts[pair] = counts.get(pair, 0) + 1",
                explanation = "dict.get(pair, 0) returns the current count or 0 if the pair " +
                        "hasn't been seen yet. Then add 1 to record this occurrence.",
                hints = listOf("Equivalent to: if pair in counts: counts[pair] += 1 else: counts[pair] = 1")
            ),
            CodeLineStub(
                lineNumber = 10,
                code = "    return counts",
                explanation = "Return the complete frequency dictionary. Example: " +
                        "{(116, 104): 3, (104, 101): 2} means 'th' appeared 3 times, 'he' 2 times."
            ),

            // ── Section 3: merge ──
            CodeLineStub(
                lineNumber = 12,
                code = "def merge(ids: List[int], pair: Tuple[int, int], idx: int) -> List[int]:",
                explanation = "The core BPE merge operation. Given a list of token IDs and a pair " +
                        "to replace, substitute every occurrence of (a,b) with the new token id."
            ),
            CodeLineStub(
                lineNumber = 13,
                code = "    newids = []",
                explanation = "Build a new list from scratch rather than modifying in-place. " +
                        "This is cleaner and avoids index-shifting bugs."
            ),
            CodeLineStub(
                lineNumber = 14,
                code = "    i = 0",
                explanation = "Manual index tracking because we skip 2 positions when " +
                        "we merge (consuming both tokens) vs 1 position for non-merged tokens."
            ),
            CodeLineStub(
                lineNumber = 15,
                code = "    while i < len(ids):",
                explanation = "Loop until we've processed all tokens. Using while instead of for " +
                        "because the index can advance by 1 or 2 per iteration.",
                hints = listOf("Could also use a for loop with a manual index, but while is clearer here")
            ),
            CodeLineStub(
                lineNumber = 16,
                code = "        if i < len(ids) - 1 and ids[i] == pair[0] and ids[i + 1] == pair[1]:",
                explanation = "Check three conditions: (1) there is a next token, " +
                        "(2) current token matches pair[0], (3) next token matches pair[1]. " +
                        "All must be true to trigger a merge.",
                hints = listOf("i < len(ids) - 1 prevents IndexError on the last element")
            ),
            CodeLineStub(
                lineNumber = 17,
                code = "            newids.append(idx)",
                explanation = "Replace the matched pair with the NEW merged token ID. " +
                        "This is where the vocabulary grows — a new token (idx >= 256) " +
                        "represents the concatenation of the two original tokens."
            ),
            CodeLineStub(
                lineNumber = 18,
                code = "            i += 2",
                explanation = "Skip BOTH tokens that were just merged. We consumed two " +
                        "input tokens to produce one output token.",
                isEditable = false
            ),
            CodeLineStub(
                lineNumber = 19,
                code = "        else:",
                explanation = "No merge at this position — just copy the token as-is.",
                isEditable = false
            ),
            CodeLineStub(
                lineNumber = 20,
                code = "            newids.append(ids[i])",
                explanation = "Keep the current token unchanged in the output.",
                isEditable = false
            ),
            CodeLineStub(
                lineNumber = 21,
                code = "            i += 1",
                explanation = "Advance by 1 since we only consumed one input token.",
                isEditable = false
            ),
            CodeLineStub(
                lineNumber = 22,
                code = "    return newids",
                explanation = "Return the merged token sequence. Each call reduces the " +
                        "sequence length by replacing pair occurrences with single tokens."
            ),

            // ── Section 4: train_bpe ──
            CodeLineStub(
                lineNumber = 24,
                code = "def train_bpe(text: str, vocab_size: int) -> Dict[Tuple[int, int], int]:",
                explanation = "The main training loop. Takes raw text, learns BPE merges " +
                        "iteratively until the vocabulary reaches the target size. " +
                        "Returns an OrderedDict of merges to apply during encoding."
            ),
            CodeLineStub(
                lineNumber = 25,
                code = "    assert vocab_size >= 256, \"vocab_size must be at least 256\"",
                explanation = "Safety check. Base vocabulary is always 256 bytes (0-255). " +
                        "You can't have fewer tokens than byte values.",
                isEditable = false
            ),
            CodeLineStub(
                lineNumber = 27,
                code = "    ids = list(text.encode(\"utf-8\"))",
                explanation = "Convert text to UTF-8 bytes, then to a list of integers (0-255). " +
                        "This is the starting point — every character becomes 1-4 bytes, " +
                        "and we'll learn to compress common byte pairs into single tokens."
            ),
            CodeLineStub(
                lineNumber = 29,
                code = "    merges = {}",
                explanation = "Stores the learned merge rules. Key: (token_a, token_b) pair, " +
                        "Value: the new token ID assigned to that merged pair.",
                hints = listOf("Use OrderedDict to preserve merge order")
            ),
            CodeLineStub(
                lineNumber = 30,
                code = "    num_merges = vocab_size - 256",
                explanation = "How many new tokens to create. If vocab_size=300, " +
                        "we need 44 merges on top of the 256 base byte tokens.",
                isEditable = false
            ),
            CodeLineStub(
                lineNumber = 32,
                code = "    for i in range(num_merges):",
                explanation = "Main training loop — one iteration per new token. " +
                        "Each iteration: find the most frequent pair, merge it, record the rule."
            ),
            CodeLineStub(
                lineNumber = 33,
                code = "        stats = get_stats(ids)",
                explanation = "Count all adjacent pairs in the current token sequence. " +
                        "This is called once per merge — the most expensive part of BPE.",
                hints = listOf("O(n) per iteration — consider optimizing for large texts")
            ),
            CodeLineStub(
                lineNumber = 34,
                code = "        if not stats:",
                explanation = "If there are no pairs left (single token), stop early.",
                isEditable = false
            ),
            CodeLineStub(
                lineNumber = 35,
                code = "            break",
                explanation = "Exit the loop — no more merges possible.",
                isEditable = false
            ),
            CodeLineStub(
                lineNumber = 36,
                code = "        pair = max(stats, key=stats.get)",
                explanation = "KEY LINE: Find the pair with the highest frequency. " +
                        "max() with key=stats.get returns the dictionary key with the " +
                        "largest value. If there's a tie, max returns the first encountered.",
                hints = listOf("What happens if two pairs have the same count? Is this deterministic?")
            ),
            CodeLineStub(
                lineNumber = 37,
                code = "        idx = 256 + i",
                explanation = "Assign a new token ID. Base tokens use 0-255, " +
                        "so the first new token is 256, then 257, 258, etc.",
                isEditable = false
            ),
            CodeLineStub(
                lineNumber = 38,
                code = "        ids = merge(ids, pair, idx)",
                explanation = "Apply the merge! Replace all occurrences of the chosen " +
                        "pair with the new token, producing a (slightly) shorter sequence."
            ),
            CodeLineStub(
                lineNumber = 39,
                code = "        merges[pair] = idx",
                explanation = "Record the merge rule. During encoding, we'll apply " +
                        "these rules in exactly this order to new text.",
                hints = listOf("The ORDER of merges matters for correct encoding")
            ),
            CodeLineStub(
                lineNumber = 42,
                code = "    return merges",
                explanation = "Return the complete set of learned merge rules. " +
                        "This dictionary IS the tokenizer — it maps byte pairs to new tokens."
            ),

            // ── Section 5: BPETokenizer class ──
            CodeLineStub(
                lineNumber = 46,
                code = "class BPETokenizer:",
                explanation = "Wraps the merge rules into a usable tokenizer with " +
                        "encode (text → tokens) and decode (tokens → text) methods. " +
                        "This is what you'd actually use in a language model."
            ),
            CodeLineStub(
                lineNumber = 49,
                code = "    def __init__(self, merges: Dict[Tuple[int, int], int]):",
                explanation = "Initialize with the merges learned from train_bpe(). " +
                        "The constructor also pre-computes the vocabulary for fast decoding.",
                hints = listOf("Store merges and call _build_vocab() immediately")
            ),
            CodeLineStub(
                lineNumber = 53,
                code = "    def _build_vocab(self) -> None:",
                explanation = "Pre-compute what each token ID decodes to as raw bytes. " +
                        "Start with base tokens (0-255 → single bytes), then build up " +
                        "by concatenating the byte representations of merged pairs.",
                hints = listOf("vocab[idx] = vocab[p0] + vocab[p1] recursively builds byte strings")
            ),
            CodeLineStub(
                lineNumber = 56,
                code = "        self.vocab = {idx: bytes([idx]) for idx in range(256)}",
                explanation = "Base vocabulary: token 65 → b'A', token 32 → b' ', etc. " +
                        "Each byte value maps to its single-byte representation."
            ),
            CodeLineStub(
                lineNumber = 57,
                code = "        for (p0, p1), idx in self.merges.items():",
                explanation = "Iterate through each merge rule. p0 and p1 are the two " +
                        "token IDs that were merged; idx is the new token ID."
            ),
            CodeLineStub(
                lineNumber = 58,
                code = "            self.vocab[idx] = self.vocab[p0] + self.vocab[p1]",
                explanation = "KEY LINE: Recursive vocabulary building. The bytes for " +
                        "the merged token = bytes(subtoken_a) + bytes(subtoken_b). " +
                        "Since merges are applied in order, subtokens are already in vocab."
            ),
            CodeLineStub(
                lineNumber = 61,
                code = "    def encode(self, text: str) -> List[int]:",
                explanation = "Convert text to token IDs using the learned BPE merges. " +
                        "Start with bytes, then iteratively apply the merge with the " +
                        "SMALLEST index until no more merges are possible.",
                hints = listOf("Apply merges in priority order, not by frequency")
            ),
            CodeLineStub(
                lineNumber = 63,
                code = "        ids = list(text.encode(\"utf-8\"))",
                explanation = "Same as training: text → UTF-8 bytes → integer list (0-255)."
            ),
            CodeLineStub(
                lineNumber = 64,
                code = "        while len(ids) >= 2:",
                explanation = "Keep merging as long as there are at least 2 tokens. " +
                        "Each iteration finds the best pair to merge."
            ),
            CodeLineStub(
                lineNumber = 65,
                code = "            stats = get_stats(ids)",
                explanation = "Count pairs in the current sequence. We need this to find " +
                        "which pairs are present before checking if they can be merged."
            ),
            CodeLineStub(
                lineNumber = 67,
                code = "            pair = min(stats, key=lambda p: self.merges.get(p, float(\"inf\")))",
                explanation = "CRITICAL LINE: Find the pair with the smallest merge index. " +
                        "We apply merges in the ORDER they were learned (earliest first). " +
                        "float('inf') handles pairs that were never seen during training.",
                hints = listOf("Why min and not max? Because lower index = earlier merge = higher priority")
            ),
            CodeLineStub(
                lineNumber = 68,
                code = "            if pair not in self.merges:",
                explanation = "If the best candidate pair isn't in our merge rules, " +
                        "no more merges are possible."
            ),
            CodeLineStub(
                lineNumber = 70,
                code = "            idx = self.merges[pair]",
                explanation = "Look up the token ID for this merge rule."
            ),
            CodeLineStub(
                lineNumber = 71,
                code = "            ids = merge(ids, pair, idx)",
                explanation = "Apply the merge, shortening the sequence by replacing " +
                        "each occurrence of the pair with the merged token."
            ),
            CodeLineStub(
                lineNumber = 73,
                code = "    def decode(self, ids: List[int]) -> str:",
                explanation = "Convert token IDs back to human-readable text. " +
                        "Look up each token's byte representation and concatenate."
            ),
            CodeLineStub(
                lineNumber = 74,
                code = "        pieces = []",
                explanation = "Collect byte chunks for each token."
            ),
            CodeLineStub(
                lineNumber = 75,
                code = "        for idx in ids:",
                explanation = "Process each token in the sequence."
            ),
            CodeLineStub(
                lineNumber = 76,
                code = "            pieces.append(self.vocab[idx])",
                explanation = "Look up the byte representation from the pre-built vocab."
            ),
            CodeLineStub(
                lineNumber = 77,
                code = "        return b\"\".join(pieces).decode(\"utf-8\", errors=\"replace\")",
                explanation = "Concatenate all byte chunks, then decode the complete " +
                        "byte string back to UTF-8 text. errors='replace' handles " +
                        "invalid byte sequences gracefully."
            )
        ),
        exercises = listOf(
            Exercise(
                id = "bpe_ex_1",
                description = "Implement get_stats(): count frequency of consecutive token pairs",
                expectedOutput = "{(1,2): 2, (2,3): 1} for input [1,2,1,2,3]",
                judgeCriteria = listOf(
                    JudgeCriterion("Correctly counts all adjacent pairs", 0.3f),
                    JudgeCriterion("Handles empty input (returns empty dict)", 0.2f),
                    JudgeCriterion("Handles single-element list (returns empty dict)", 0.2f),
                    JudgeCriterion("Uses zip or equivalent sliding window", 0.3f)
                )
            ),
            Exercise(
                id = "bpe_ex_2",
                description = "Implement merge(): replace all occurrences of a pair with a new token",
                expectedOutput = "[256, 3] for input [1,2,3] with pair=(1,2) and idx=256",
                judgeCriteria = listOf(
                    JudgeCriterion("Correctly replaces all occurrences of the pair", 0.4f),
                    JudgeCriterion("Does not merge overlapping pairs", 0.3f),
                    JudgeCriterion("Returns correct length after merge", 0.3f)
                )
            ),
            Exercise(
                id = "bpe_ex_3",
                description = "Implement train_bpe(): the complete training loop",
                expectedOutput = "Merges dictionary with vocab_size - 256 entries",
                judgeCriteria = listOf(
                    JudgeCriterion("Correctly converts text to bytes then IDs", 0.2f),
                    JudgeCriterion("Finds most frequent pair at each iteration", 0.3f),
                    JudgeCriterion("Applies merge correctly each iteration", 0.3f),
                    JudgeCriterion("Returns correct number of merges", 0.2f)
                )
            ),
            Exercise(
                id = "bpe_ex_4",
                description = "Implement BPETokenizer.encode(): apply learned merges to new text",
                expectedOutput = "Token IDs that decode back to original text",
                judgeCriteria = listOf(
                    JudgeCriterion("Applies merges in correct priority order", 0.4f),
                    JudgeCriterion("Handles unseen byte pairs gracefully", 0.3f),
                    JudgeCriterion("Stops when no more merges are applicable", 0.3f)
                )
            )
        ),
        judgeCriteria = listOf(
            JudgeCriterion("Code correctness (matches expected behavior)", 0.5f),
            JudgeCriterion("Code style and readability", 0.2f),
            JudgeCriterion("Handles edge cases", 0.3f)
        )
    )
}
