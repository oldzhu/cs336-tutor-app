# Development Log

> **English** · [中文版](../zh/01-dev-log.md)

## 2026-07-02 — Language Switching (Partial) + Crash Fixes

### Language Switching — RESOLVED (Partially)
- ✅ **No crash**: `LocaleContextWrapper` (extends `ContextWrapper`) only overrides `getResources()` — preserves Activity context for Hilt
- ✅ **Language persists**: Saved to SharedPreferences, correctly loaded on restart
- ✅ **UI strings translate**: All screens use `stringResource()` — switches between EN/CN `strings.xml`
- ⚠️ **Not yet translated**: BPE code explanations, AI tutor content, component descriptions (hardcoded in `bpe_component.kt`)

**Key fix**: `CompositionLocalProvider(LocalContext provides localeContext)` in `TutorApp.kt` using `LocaleContextWrapper` — safe with Hilt.

**What was tried** (all failed due to Hilt `getResources()` conflict):
- `applyOverrideConfiguration` → crash
- `AppCompatDelegate.setApplicationLocales` → crash (AppCompatActivity still has Hilt issue)
- `resources.updateConfiguration` after `super.onCreate` → no effect on Compose

**Files changed**:
- `TutorApp.kt` — added `LocaleContextWrapper` + `CompositionLocalProvider`
- `MainActivity.kt` — back to minimal `ComponentActivity` (clean)
- `SettingsScreen.kt` — unwrap ContextWrapper for `recreate()`
- All screen files — replaced hardcoded strings with `stringResource(R.string.xxx)`
- `values/strings.xml` + `values-zh/strings.xml` — synced keys

### Remaining
- ⚠️ BPE explanations and tutor content still English
- ⚠️ OpenCode WSL file permission issue

## 2026-07-01 — Phase 1: BPE Tutor & UI Polish
- ✅ Split-screen tutor, typing animation, landscape layout, settings
- ✅ 21 commits total on `oldzhu/cs336-tutor-app`

## 2026-06-30 — Project Initiation
*(foundation details)*
