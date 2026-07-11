# WSL File Sync — Known Issue

> **Status**: Documented · **Impact**: High — causes wasted debugging hours

## Problem

The `//wsl$/myUbuntu/...` path used by `execute_code`, `read_file`, `write_file`, and `patch` tools is **unreliable** for writing to the WSL filesystem. Writes sometimes succeed, sometimes fail silently, and sometimes get overwritten by subsequent git operations.

## Symptoms

- Code verified in `execute_code` output doesn't appear in the compiled APK
- `grep` on WSL terminal returns different results than `execute_code`
- Builds succeed but new features don't work (code appears to compile but is NOT in DEX)
- Strings in APK's DEX don't match what was written

## Root Cause

Hermes tools use the `//wsl$/myUbuntu/` UNC path (Windows WSL network filesystem) to access WSL files. This path:

1. Maps to WSL's `/home/oldzhu/...` but through a Windows translation layer
2. Writes may be cached and not flushed to the actual WSL ext4 filesystem
3. Git operations in WSL terminal can overwrite pending writes
4. Two different tools (`execute_code` and `patch`) use the same path but may hit different cache states

## Workaround: `sed` via WSL Terminal

The ONLY reliable way to modify WSL source files:

```bash
wsl -d myUbuntu -- bash -c "cd /path/to/file && sed -i 's|old|new|' File.kt"
```

Then build from the same WSL session:

```bash
wsl -d myUbuntu -- bash -c "cd /project && ./gradlew assembleDebug"
```

**Do NOT** use `cp /mnt/c/... → //wsl$/...` to sync — the Windows→WSL direction is also unreliable.

## Windows vs WSL File Divergence

The Windows filesystem (`C:\Users\orien\cs336-tutor-app`) and WSL filesystem (`/home/oldzhu/cs336-tutor-app`) are SEPARATE copies of the repository. When the user works on the project, they use the Windows IDE. Hermes builds use the WSL Gradle. Files must stay in sync.

**Current divergence**: 
- Windows `SplitScreenTutorScreen.kt` = 86-line STUB (no features)
- WSL `SplitScreenTutorScreen.kt` = 587-line real implementation

## Best Practice for File Edits

1. **Simple one-line changes**: Use `wsl -d myUbuntu -- sed -i` directly
2. **Multi-line changes**: Use `wsl -d myUbuntu -- python3 -c "..."` 
3. **Full file rewrites**: Write to WSL via `cat <<'EOF' > /path` in terminal
4. **Avoid**: `execute_code` writes to `//wsl$/` for WSL source files
5. **Avoid**: `read_file` with `C:\Users\...` path for WSL files (read the stale Windows copy)

## When to Use `//wsl$/` 

- **READING** WSL files: `read_file`/`execute_code` via `//wsl$/` is mostly reliable for reads
- **WRITING** WSL files: **NOT RELIABLE** — use terminal `sed` or `python3`
- **Windows files** (`C:\Users\...`): `write_file` and `patch` work fine — these are the user's IDE files
