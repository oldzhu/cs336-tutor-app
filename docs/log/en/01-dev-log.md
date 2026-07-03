# Development Log

> **English** · [中文版](../zh/01-dev-log.md)

## 2026-07-02 — Phase 2: All Component Specs Complete

### Components Implemented (TDD: RED → GREEN)
| # | Component | Spec | Tests | Lines |
|---|---|---|---|---|
| 1 | BPE Tokenizer | ✅ | ✅ 5/5 | 432 |
| 2 | RMSNorm | ✅ | ✅ 10 tests | 16 |
| 3 | RoPE | ✅ | ✅ 7 tests | 21 |
| 4 | Multi-Head Attention | ✅ | ✅ 8 tests | 31 |
| 5 | SwiGLU FFN | ✅ | ✅ 7 tests | 14 |
| 6 | Transformer Block | ✅ | ✅ 6 tests | 20 |

### Architecture
- **Pre-LN**: RMSNorm BEFORE each sub-layer (LLaMA-style)
- **Residual connections**: x + sublayer(norm(x))
- **RoPE**: Rotary embeddings on Q and K
- **SwiGLU**: Gated activation with SiLU
- **Causal masking**: Future tokens masked in attention

### All components unlocked — free exploration available in dashboard

### Git: 27 commits on `oldzhu/cs336-tutor-app` master

## 2026-07-02 — Language Switching Complete
- ContextWrapper approach (Hilt-safe), bilingual strings.xml, BPEExplanationsZh

## 2026-07-01 — Phase 1: BPE Tutor & UI Polish

## 2026-06-30 — Project Initiation
