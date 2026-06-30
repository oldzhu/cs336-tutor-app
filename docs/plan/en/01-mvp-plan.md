# Development Plan: CS336 Tutor App

> **English** · [中文版](../zh/01-mvp-plan.md)
> References: [Project Spec](../../spec/en/01-project-overview.md)

## Phase 0: Foundation (Week 1)

**Goal**: Runnable Android project skeleton with bilingual docs

- [x] Create Git repo + doc structure
- [ ] Design spec drafted (spec/en, spec/zh)
- [ ] Plan drafted (this document)
- [ ] Scaffold Android project with Compose
- [ ] `Hello World` Compose screen renders on emulator
- [ ] Configure Gradle with Chaquopy SDK
- [ ] Configure Room database schemas
- [ ] Create LLM provider interface + mock implementation
- [ ] Verify APK builds successfully
- [ ] First Git tag: `v0.1.0-foundation`

## Phase 1: MVP — BPE Tokenizer Tutor (Week 2-3)

**Goal**: A working split-screen tutor for one component

### Core UI
- [ ] **SplitScreenTutor** activity with two-pane layout
- [ ] **AIExplanationPanel** — scrollable, shows line-by-line code + explanation
- [ ] **CodeEditorPanel** — editable code area with syntax highlighting
- [ ] **Q&A Overlay** — bottom sheet for asking LLM questions
- [ ] **StatusBar** — progress indicator, LLM provider switch, pause/resume

### BPE Tokenizer Component
- [ ] BPE spec loaded into Component Registry
- [ ] Line-by-line code: `train_bpe()`, `encode()`, `decode()`
- [ ] LLM generates explanations for each line
- [ ] **Judge feature**: user code vs expected, returns diff + score
- [ ] Python execution via Chaquopy for test runs
- [ ] Offline fallback: pre-bundled explanations (no LLM needed for basic flow)

### LLM Integration
- [ ] Remote provider: OpenAI (primary), Anthropic (backup)
- [ ] Local provider: Ollama (configurable endpoint)
- [ ] Provider selection UI in settings
- [ ] API key management (encrypted storage via EncryptedSharedPreferences)

### Verification
- [ ] User can complete the BPE tokenizer lesson end-to-end
- [ ] AI judge catches typical errors (wrong vocab size, missing merges)
- [ ] Q&A answers questions about the current line
- [ ] Progress persists across app restarts
- [ ] APK size < 80MB
- [ ] Tag: `v0.2.0-mvp-bpe`

## Phase 2: Full Transformer Pipeline (Week 4-6)

- [ ] **RMSNorm** component tutor
- [ ] **Rotary Position Embedding (RoPE)** component tutor
- [ ] **Multi-Head Self-Attention** component tutor
- [ ] **Position-wise FFN** (SwiGLU) component tutor
- [ ] **Full Transformer Block** — stitching components together
- [ ] **Output Layer** (LM head + softmax + cross-entropy loss)
- [ ] **Training Loop** tutor — forward, backward, optimizer step
- [ ] Visualization: attention maps, loss curves
- [ ] Component dependency graph — lock/unlock based on prerequisites
- [ ] Tag: `v0.3.0-full-transformer`

## Phase 3: Advanced Features (Week 7-8)

- [ ] **Dynamic component generation** — LLM creates new tutor modules at runtime
- [ ] **MoE (Mixture of Experts)** component
- [ ] **MLA (Multi-head Latent Attention)** component
- [ ] **DeepSeek V4-style architecture** component
- [ ] User code export (save completed code to files)
- [ ] Progress sharing / leaderboard
- [ ] Tag: `v0.4.0-extensible`

## Phase 4: Polish & Publish (Week 9-10)

- [ ] Performance optimization (cold start < 3s)
- [ ] Onboarding tutorial for new users
- [ ] Offline mode (full local LLM + cached content)
- [ ] App icon, theming, accessibility
- [ ] Play Store beta release
- [ ] Tag: `v1.0.0-beta`

## Technical Decisions & Rationale

| Decision | Choice | Rationale |
|---|---|---|
| Python runtime | Chaquopy | Full CPython in APK, no backend needed |
| Code editor | CodeMirror via WebView | Mature syntax highlighting, line-by-line interaction |
| DI framework | Hilt | Official Android recommendation, Compose-friendly |
| Architecture | MVVM + Clean Architecture | Testable, maintainable, Google-recommended |
| LLM streaming | Kotlin Flow | Reactive UI updates, cancellation support |
| Local LLM | Ollama | Simplest setup, OpenAI-compatible API, cross-platform |

## Risk Register

| Risk | Mitigation |
|---|---|
| Chaquopy compatibility with numpy | Use Chaquopy's built-in numpy build; pin version |
| APK size > 100MB | Modularize: download component packs on demand |
| LLM API costs | Support local models; cache explanations |
| Android emulator in WSL | Use Windows-side emulator, ADB from WSL |

---

> 🔗 **Related Docs**
> - [Project Spec (English)](../../spec/en/01-project-overview.md)
> - [Project Spec (中文)](../../spec/zh/01-project-overview.md)
> - [README](../../../README.md)
