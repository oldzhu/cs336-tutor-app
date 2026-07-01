# Development Log

> **English** · [中文版](../zh/01-dev-log.md)

## 2026-07-01 — Phase 1: BPE Tutor & UI Polish

### BPE Tokenizer Tutor
- ✅ Implemented split-screen tutor with AI explanation panel + code editor
- ✅ BPE component spec (432 lines, 64 code lines, 4 exercises)
- ✅ Typing animation: character-by-character at 80ms, auto-repeat loop
- ✅ All-lines code dialog (📋 button)
- ✅ Landscape layout: side-by-side split, both panels scrollable
- ✅ Settings screen: provider toggle, API endpoint, key, model selection

### Typing Fixes
- ✅ Changed from 30ms to 80ms per character (human speed)
- ✅ Fixed repeat loop: type → 2s pause → clear → 500ms → retype
- ✅ Fixed `repeatTrigger` declaration order for Kotlin compilation

### Settings
- ✅ LLM Provider selection (Remote/Local)
- ✅ API endpoint, key, model fields
- ✅ Default model: deepseek-v4-flash

### Testing
- ✅ 5 BPE component unit tests pass
- ✅ SettingsViewModel test created (androidTest, needs device)

### ⚠️ OPEN ISSUE: Language Switching (CN/EN) Not Working

**Problem**: Selecting Chinese in Settings → Save does not change UI to Chinese.

**What's been tried**:
1. `updateConfiguration` (deprecated) — Compose doesn't pick up changes
2. `applyOverrideConfiguration` — crashes because Hilt calls `getResources()` before `onCreate`
3. `attachBaseContext` + `createConfigurationContext` — doesn't crash but locale doesn't apply to Compose
4. `AppCompatDelegate.setApplicationLocales` — needs appcompat dependency not in project

**Root cause**: `ComponentActivity` + Hilt calls `getResources()` during DI injection before `onCreate`, making `applyOverrideConfiguration` crash. `attachBaseContext` wraps the context but Compose may not re-read it after `recreate()`.

**Current state**: `attachBaseContext` approach committed — no crash but locale doesn't switch.
**Next**: Research Compose-compatible locale switching or use `AppCompatDelegate` with appcompat dependency.

### Git
- ✅ 17 commits pushed to `oldzhu/cs336-tutor-app`
- ✅ Branch: master

### OpenCode
- ⚠️ OpenCode has file permission issues inside WSL — currently coding directly
- ⚠️ Need to resolve OpenCode WSL setup

## 2026-06-30 — Project Initiation
*(see previous commits for foundation details)*
