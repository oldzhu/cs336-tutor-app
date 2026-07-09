# Development Log

> **English** · [中文版](../zh/01-dev-log.md)

## 2026-07-09 — Local LLM Inference Working (via AAR) + ModelScope Download

### Local LLM Now Functional
- ✅ Qwen2.5-1.5B running locally on Oppo RMX3700 (Snapdragon 8+ Gen 1)
- ✅ Model downloaded via ModelScope directly to app private storage (`/data/data/com.cs336.tutor/files/`)
- ✅ AAR fallback (`de.kherud:llama:4.1.0`) used for inference
- ✅ App auto-falls back: NativeBridge → AAR
- ✅ Provider switching: responsive on settings save (no restart needed)

### Issues Found & Fixed
1. **File permissions**: `/sdcard/models/` inaccessible from app → copied to app private storage via ModelScope download
2. **libllama.so crash**: Custom JNI library crashes in `llama_decode` — tracked as PENDING (see below)
3. **Context length**: 672-token prompts crash decode — shortened to <500 chars, chunked processing

### Models Available via ModelScope
| Model | Size | Status |
|---|---|---|
| Qwen2.5-1.5B-Instruct Q4_K_M | 1.1 GB | ✅ Downloaded, working |
| Qwen2.5-0.5B-Instruct Q4_K_M | 350 MB | Available |
| SmolLM2-360M-Instruct Q4_K_M | 200 MB | Available (HuggingFace) |

### PENDING: Custom libllama.so Crash Investigation
**Symptom**: `llama_decode()` SIGABRT with 672 tokens in ONE batch.
**Findings**:
- Batch=32 works for 5 batches (160 tokens) then crashes
- Model loads and vocab works fine
- Crash deep inside llama.cpp assertion
- AAR library (same API) works fine — PROVES model file is valid
- Root cause likely: static library build config mismatch (CMAKE flags, GGML backends)

**Next steps**: Compare our CMAKE configuration with AAR build, check GGML_BACKEND_CPU flags, thread config.

### Ollama References Removed
- Replaced `local_endpoint` with `local_model_path`
- All Ollama mentions cleaned from strings, UI, documentation

### Files Changed
- `app/src/main/cpp/llama_jni.cpp` — JNI bridge with NativeBridge + AAR fallback
- `app/src/main/java/.../LocalLLMProvider.kt` — Dual-path inference (Native → AAR)
- `app/src/main/java/.../ModelScopeDownloader.kt` — Model list + download
- `app/src/main/java/.../ModelDownloadList.kt` — Settings UI for downloads
- `app/src/main/java/.../AppModule.kt` — @Singleton removed, loadModel() called
- `app/src/main/java/.../TutorApplication.kt` — Model copy to internal storage
- `app/src/main/java/.../SettingsScreen.kt` — Model download UI
- `app/src/main/res/values/strings.xml` — Removed Ollama references
- `app/build.gradle.kts` — AAR dependency added back
- `docs/plan/en/09-custom-native-build.md` — Custom build documentation
