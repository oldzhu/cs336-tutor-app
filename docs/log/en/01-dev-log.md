# Development Log

> **English** · [中文版](../zh/01-dev-log.md)
> Track all decisions, actions, and progress

## 2026-06-30 — Project Initiation

### Decisions
- **Python on Android**: Chaquopy (embedded CPython in APK) — see [Spec §7](../../spec/en/01-project-overview.md#7-python-code-execution-chaquopy)
- **Project location**: Windows filesystem (`C:\Users\orien\cs336-tutor-app`) — WSL networking was unreliable due to Windows proxy (`127.0.0.1:10808`)
- **OpenCode**: Installed via npm on Windows (v1.17.11), will be used for Android project scaffolding once network is available
- **Bilingual docs**: Each English doc links to its Chinese counterpart and vice versa

### Actions Taken
- Created Git repo with initial doc structure
- Drafted Project Spec (CN/EN)
- Drafted Development Plan with 5 phases (Foundation → MVP → Full Transformer → Advanced → Polish)
- Created bilingual docs scaffold under `docs/`

### Network-Dependent (Pending)
- [ ] Android SDK installation
- [ ] OpenCode scaffold of Android project
- [ ] npm installs in WSL for Chaquopy dependencies
- [ ] Verify APK build on emulator

### Next Steps
1. Complete offline project scaffolding (Gradle files, source stubs)
2. Initial Git commit
3. Wait for user signal to resume network-dependent tasks
