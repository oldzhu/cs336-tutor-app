# Development Log

> **English** · [中文版](../zh/01-dev-log.md)

## 2026-07-06 — Chat History + Full Code Review + `\n` Escaping Fix

### Chat History (ChatGPT-style Q&A)
- ✅ ChatMessage data model (role, content, timestamp)
- ✅ ViewModel accumulates per-component chat history
- ✅ Full context sent to LLM: all code lines + current line + chat history
- ✅ Colored chat bubbles above Q&A input (user=primary, AI=secondary)
- ✅ Old `answerText` removed (history replaces it)
- ✅ Scrollable at 300dp, no truncation
- ✅ Tests: 4 ChatMessage tests

### Full Code Review Component
- ✅ 11th component on Dashboard: `fullreview`
- ✅ Shows assembled assignment overview
- ✅ Prerequisites: all 10 components
- ✅ Tests: 5 component spec tests

### `\n` Escaping Fix
- Root cause: Python heredoc's `\n` → literal newline in Kotlin
- Solution: `\\\\n` (4 backslashes) in Python → `\n` in Kotlin
- Pattern saved for all future WSL file edits

### Assignment-Level Judging
- ✅ FAB with loading spinner + progress text
- ✅ DeepSeek API evaluation via manual provider creation
- ✅ Score persists until dismissed
- ✅ ModalBottomSheet display

### Tests
- ChatAndReviewTests.kt: 12 new tests
- DashboardViewModelJudgeTest.kt: 2 tests (fixed companion object ref)
- All JVM tests pass: BUILD SUCCESSFUL

### Git: 49 commits on `oldzhu/cs336-tutor-app` master

## 2026-07-03 — DeepSeek LLM Provider + All Explanations

## 2026-07-02 — Language Switching + Component Expansion

## 2026-07-01 — Phase 1: BPE Tutor & UI Polish

## 2026-06-30 — Project Initiation
