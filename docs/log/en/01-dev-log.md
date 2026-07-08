# Development Log

> **English** · [中文版](../zh/01-dev-log.md)

## 2026-07-08 — Local LLM: Pre-built AAR + Custom Build Documentation + Proxy Fix

### java-llama.cpp Pre-built AAR
- ✅ Added `de.kherud:llama:4.1.0` Maven dependency
- ✅ LocalLLMProvider rewired via reflection (avoids compile-time AAR dependency for JVM tests)
- ✅ Model loads from `/sdcard/models/qwen2.5-1.5b-instruct-q4_k_m.gguf` (1.1GB)
- ✅ Falls back to mock when model not loaded

### Custom Native Build (Long-term)
- ✅ Documented in `docs/plan/en/09-custom-native-build.md`
- ✅ 10 issues found & fixed during investigation:
  1. NDK extraction corrupted (zip timeout)
  2. Proxy not mirrored to WSL (127.0.0.1 vs 172.20.80.1)
  3. sudo blocks in WSL (password required)
  4. Android SDK cmake is Windows binary
  5. CMake "No SOURCES" (sync issues)
  6. Missing ggml-backend.c → ggml-backend.cpp
  7. C++17 features without -std=c++17
  8. Missing GGML_VERSION macros
  9. Missing ggml-impl.h include
  10. Undefined symbols at link (100+ source files needed)
- CMakeLists.txt updated for llama.cpp v4.x structure
- Correct approach documented: build static lib via llama.cpp's CMake → link JNI

### Proxy Configuration Root Cause
- Windows: `http_proxy=http://127.0.0.1:10808` (Clash/V2Ray)
- WSL NAT can't reach `127.0.0.1`
- Correct WSL proxy: `http_proxy=http://172.20.80.1:10808`
- Persist: add to `~/.bashrc` in WSL

### Full Code Review
- ✅ Dynamic code assembly from all 10 components at runtime

### Chat History (ChatGPT-style)
- ✅ ChatMessage model, per-component history, full LLM context
- ✅ Colored chat display, scrollable, removable answerText
- ✅ `\\n` escaping fix: Python heredoc needs 4 backslashes (`\\\\n`)

### Tests
- LocalLLMProviderTest: 6 tests
- ChatAndReviewTests: 12 tests
- All JVM tests pass

### Git: 52 commits on `oldzhu/cs336-tutor-app` master

## 2026-07-03 — DeepSeek LLM Provider + All Explanations
## 2026-07-02 — Language Switching + Component Expansion
## 2026-07-01 — Phase 1: BPE Tutor & UI Polish
## 2026-06-30 — Project Initiation
