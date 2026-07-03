# Development Log

> **English** · [中文版](../zh/01-dev-log.md)

## 2026-07-03 — DeepSeek LLM Provider + All Explanations Complete

### DeepSeek LLM Integration
- ✅ `DeepSeekLLMProvider` using Gson + OkHttp
- ✅ Settings persistence for API key, endpoint, model
- ✅ Key sanitization (`filter { !it.isWhitespace() }`) + format validation
- ✅ Falls back to "Configure API key" message when no key
- ✅ Q&A enriched with full component code context
- ✅ Tests written: judge fallback, Q&A fallback, provider name

### All Explanations Filled
- All 10 components: every code line has English + Chinese explanation
- Import lines, `super().__init__()`, closing parens — nothing left empty
- Hints translated for all components

### Language Switching
- ✅ ContextWrapper in Compose (Hilt-safe)
- ✅ LaunchedEffect re-triggers on language change
- ✅ Bilingual placeholder text
- ✅ Component names in Chinese at top of tutor page

### New Components
| # | Component | Spec | Tests | Chinese |
|---|---|---|---|---|
| 7 | Embedding Layer | ✅ | ✅ 7 | ✅ |
| 8 | LM Head | ✅ | ✅ | ✅ |
| 9 | Adam Optimizer | ✅ | ✅ | ✅ |
| 10 | Training Loop | ✅ | ✅ | ✅ |

### UI Features
- ℹ️ Overview button (formulas, algorithms, arxiv links — bilingual)
- 🔢 Line jump navigation (tap line number → type any line)
- 🧪 Judge Component (evaluates ALL lines)
- 📋 All-lines code overview dialog
- Clickable reference links (open browser)
- Correct pipeline order: ...LM Head → Optimizer → Training Loop

### Docs
- `docs/plan/en/02-dynamic-content.md` — Future: dynamic AI tutor content
- `docs/plan/en/03-local-llm-mobile.md` — Future: local LLM on mobile
- `docs/plan/en/04-multimodal-teaching.md` — Future: multimodal learning
- `docs/plan/en/05-roadmap.md` — Roadmap & tracking

### Git: 42 commits on `oldzhu/cs336-tutor-app` master

## 2026-07-02 — Language Switching + Component Expansion

## 2026-07-01 — Phase 1: BPE Tutor & UI Polish

## 2026-06-30 — Project Initiation
