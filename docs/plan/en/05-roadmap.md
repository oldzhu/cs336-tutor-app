# CS336 Tutor — Roadmap & Tracking

> **English** · [中文版](../zh/05-roadmap.md)

## ✅ Completed
| Item | Status |
|---|---|
| 10-component pipeline | ✅ |
| Language switching (CN/EN) | ✅ |
| Bilingual docs + explanations | ✅ |
| Overview formulas + references | ✅ |
| Per-component judging | ✅ |
| DeepSeek LLM provider | ✅ |
| Assignment-level judging (FAB + BottomSheet) | ✅ |
| Chat history (ChatGPT-style Q&A) | ✅ |
| Full code review (dynamic assembly) | ✅ |
| Qwen2.5-1.5B model on phone (1.1GB) | ✅ |
| java-llama.cpp AAR (pre-built) | ✅ |
| LocalLLMProvider (reflection-based) | ✅ |

## 🔧 In Progress
| Item | Status |
|---|---|
| Local LLM testing on phone | 🔧 User testing |
| Custom libllama.so build | ⏸ Documented, blocked by linker |

## 📋 Next
| Item | Priority |
|---|---|
| Chaquopy Python integration | P1 |
| Progress tracking UI | P2 |
| Custom native build (static lib approach) | P2 |

## 📄 Docs
| Doc | Path |
|---|---|
| Local LLM design | `docs/plan/en/08-local-llm-implementation.md` |
| Custom native build | `docs/plan/en/09-custom-native-build.md` |
| Chat history | `docs/plan/en/06-chat-history.md` |
| Full code review | `docs/plan/en/07-full-code-review.md` |
| Dev log | `docs/log/en/01-dev-log.md` |

## 🔑 Key Knowledge
- Python `\\\\n` (4 backslashes) → Kotlin `\n` escape
- Dialog → BottomSheet (DEX stripping issue)
- WSL proxy: `172.20.80.1:10808` not `127.0.0.1`
- llama.cpp v4.x: ggml-backend.c → .cpp, needs C++17, 100+ source files
- NDK extraction: use `unzip -o` with timeout ≥ 300s
