# Development Log

> **English** · [中文版](../zh/01-dev-log.md)
> Track all decisions, actions, and progress

## 2026-06-30 — Project Initiation

### Decisions
- **Python on Android**: Chaquopy (embedded CPython in APK) — see [Spec §7](../../spec/en/01-project-overview.md#7-python-code-execution-chaquopy)
- **Project location**: Windows filesystem (`C:\Users\orien\cs336-tutor-app`) — WSL networking was unreliable due to Windows proxy (`127.0.0.1:10808`)
- **OpenCode**: Installed via npm on Windows (v1.17.11)
- **Bilingual docs**: Each English doc links to its Chinese counterpart and vice versa

### Actions Taken
- Created Git repo with initial doc structure
- Drafted Project Spec (CN/EN)
- Drafted Development Plan with 5 phases
- Created bilingual docs scaffold under `docs/`
- Created full Android project scaffold (Compose + Hilt + Room + Chaquopy)
- Generated Gradle wrapper (8.11.1) — fixed KAPT lock bug
- Installed Android SDK (platform 35, build-tools 35, platform-tools)
- **First APK build: SUCCESS** — `app-debug.apk` (43MB)
- Added BPE Tokenizer reference Python implementation
- Added launcher icon resources
- Set up GitHub remote (`origin → git@github.com:oldzhu/cs336-tutor-app.git`)
- Generated SSH key for GitHub auth

### Network-Dependent (Pending)
- [x] Android SDK installation
- [ ] GitHub push (manual — SSH proxy too slow)
- [ ] WSL setup still running (apt update slow through proxy)

### Next Steps
1. Complete offline project scaffolding (Gradle files, source stubs)
2. Initial Git commit
3. Wait for user signal to resume network-dependent tasks

### Rules Established
- **TDD** — Test-Driven Development mandatory for all features
- **OpenCode Continuity** — Always resume last session
- Project rules saved in  (auto-injected per session)
