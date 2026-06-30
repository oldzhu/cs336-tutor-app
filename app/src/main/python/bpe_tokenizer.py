# BPE Tokenizer - Reference Implementation for CS336 Assignment 1
# This script is bundled with the app via Chaquopy.
# Students will implement each function step by step with AI guidance.

import re
from collections import Counter, OrderedDict
from typing import Dict, List, Optional, Tuple


# ============================================================
# Section 1: Unicode Utilities
# ============================================================

def unicode_to_bytes() -> Dict[int, bytes]:
    """
    Map each of the 256 byte values (0-255) to their corresponding
    unicode characters, following GPT-2's tokenizer scheme.
    """
    # ... to be implemented by user
    pass


# ============================================================
# Section 2: BPE Tokenizer Training
# ============================================================

def get_stats(ids: List[int]) -> Dict[Tuple[int, int], int]:
    """
    Count frequency of each consecutive pair of token IDs.
    Returns: {(token1, token2): count, ...}
    """
    counts = {}
    for pair in zip(ids, ids[1:]):
        counts[pair] = counts.get(pair, 0) + 1
    return counts


def merge(ids: List[int], pair: Tuple[int, int], idx: int) -> List[int]:
    """
    Replace all occurrences of `pair` in `ids` with the new token `idx`.
    This is the core BPE merge operation.
    """
    newids = []
    i = 0
    while i < len(ids):
        if i < len(ids) - 1 and ids[i] == pair[0] and ids[i + 1] == pair[1]:
            newids.append(idx)
            i += 2
        else:
            newids.append(ids[i])
            i += 1
    return newids


def train_bpe(text: str, vocab_size: int) -> Dict[Tuple[int, int], int]:
    """
    Train a BPE tokenizer on the given text.
    1. Convert text to bytes, then to token IDs (0-255)
    2. Find the most frequent pair, merge it into a new token
    3. Repeat until vocab_size is reached

    Args:
        text: Training text
        vocab_size: Desired vocabulary size (must be >= 256)

    Returns:
        merges: Ordered dict of {(token1, token2): new_token_id}
    """
    assert vocab_size >= 256, "vocab_size must be at least 256"

    # Convert text to raw bytes, then to list of ints (0-255)
    ids = list(text.encode("utf-8"))
    print(f"Initial IDs: {len(ids)} tokens from 0-{max(ids) if ids else 0}")

    merges = {}
    num_merges = vocab_size - 256

    for i in range(num_merges):
        stats = get_stats(ids)
        if not stats:
            break  # No more pairs to merge
        pair = max(stats, key=stats.get)  # Most frequent pair
        idx = 256 + i  # New token ID
        ids = merge(ids, pair, idx)
        merges[pair] = idx
        if i % 100 == 0:
            print(f"Merge {i}/{num_merges}: {pair} -> {idx}")

    return merges


# ============================================================
# Section 3: Tokenizer Encoding / Decoding
# ============================================================

class BPETokenizer:
    """
    Complete BPE tokenizer with encode/decode functionality.
    """

    def __init__(self, merges: Dict[Tuple[int, int], int]):
        self.merges = merges
        self._build_vocab()

    def _build_vocab(self) -> None:
        """Build lookup tables from merges."""
        self.vocab = {idx: bytes([idx]) for idx in range(256)}
        for (p0, p1), idx in self.merges.items():
            self.vocab[idx] = self.vocab[p0] + self.vocab[p1]

    def encode(self, text: str) -> List[int]:
        """Encode text into token IDs using the trained merges."""
        ids = list(text.encode("utf-8"))
        while len(ids) >= 2:
            stats = get_stats(ids)
            # Find the pair with the smallest merge index
            pair = min(stats, key=lambda p: self.merges.get(p, float("inf")))
            if pair not in self.merges:
                break  # No more merges to apply
            idx = self.merges[pair]
            ids = merge(ids, pair, idx)
        return ids

    def decode(self, ids: List[int]) -> str:
        """Decode token IDs back to text."""
        pieces = []
        for idx in ids:
            pieces.append(self.vocab[idx])
        return b"".join(pieces).decode("utf-8", errors="replace")


# ============================================================
# Section 4: Experiments
# ============================================================

def experiment_vocab_sizes(text: str, sizes: List[int]) -> Dict[int, float]:
    """
    Train BPE with different vocabulary sizes and measure compression ratio.
    """
    results = {}
    byte_len = len(text.encode("utf-8"))

    for vs in sizes:
        merges = train_bpe(text, vs)
        tokenizer = BPETokenizer(merges)
        token_ids = tokenizer.encode(text)
        compression = byte_len / len(token_ids) if token_ids else 0
        results[vs] = round(compression, 2)
        print(f"Vocab size={vs}: {len(token_ids)} tokens, compression={compression:.2f}x")

    return results
