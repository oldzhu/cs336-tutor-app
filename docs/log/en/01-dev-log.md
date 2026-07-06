# Development Log

> **English** · [中文版](../zh/01-dev-log.md)

## 2026-07-06 — Assignment-Level Judging Complete + DeepSeek LLM Provider

### Assignment Judging
- ✅ Judge FAB on Dashboard with loading spinner + progress text
- ✅ ModalBottomSheet displays full DeepSeek evaluation response
- ✅ `max_tokens` increased to 8192 for complete API responses
- ✅ Manual DeepSeekLLMProvider creation (bypasses Hilt injection issues)
- ✅ Mock fallback when no API key configured
- ✅ Score persists until dismissed by user
- ✅ Tests: DashboardViewModelJudgeTest (state transitions, companion object)

### Bug Fixes
- Fixed R8 code stripping: Dialog/AlertDialog composables were stripped from DEX
- Solved with ModalBottomSheet (Material3 standard component, not stripped)
- Fixed duplicate `judgeAssignment` method in DeepSeekLLMProvider
- Fixed Java `..` backtick compile errors in test names
- Key sanitization: `filter { !it.isWhitespace() }` removes newlines from API keys

### Pipeline Complete
| # | Component | Spec | Test | Chinese | Hints |
|---|---|---|---|---|---|
| 1-10 | Full pipeline | ✅ | ✅ | ✅ | ✅ |

### Git: 47 commits on `oldzhu/cs336-tutor-app` master

## 2026-07-03 — DeepSeek LLM Provider + All Explanations

## 2026-07-02 — Language Switching + Component Expansion

## 2026-07-01 — Phase 1: BPE Tutor & UI Polish

## 2026-06-30 — Project Initiation
