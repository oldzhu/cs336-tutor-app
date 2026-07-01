# CS336 Tutor App — Project Rules

> Auto-injected by Hermes, OpenCode, Claude Code, Codex, and other agentic tools.
> Bilingual docs at docs/spec/, docs/plan/, docs/log/ in CN/EN.

## RULE 1: Test-Driven Development (TDD)

**All features — existing and new — MUST follow strict TDD:**

1. **RED** — Write failing test FIRST
2. **GREEN** — Minimal code to pass
3. **REFACTOR** — Clean up, keep tests green

NO production code without a failing test first. Write code before the test? Delete it. Start over.

### Test Commands
```bash
./gradlew testDebug         # Unit tests (Kotlin JVM — fast)
./gradlew connectedCheck    # Instrumented tests (phone/emulator)
./gradlew check             # Both
```

### Test Structure
```
app/src/test/        → Kotlin JUnit tests (fast, JVM)
app/src/androidTest/ → Instrumented tests (needs device)
```

## RULE 2: OpenCode Session Continuity

**Always resume from the last OpenCode session:**

```bash
opencode session list        # List sessions
opencode -c                  # Continue last session
opencode -s <session_id>     # Continue specific session
```

Current session tracked in `docs/log/opencode-session.txt`.

## RULE 3: Manual Testing Before Moving On

**After automated tests pass, deploy to real device before proceeding:**

1. Auto tests pass → Ask user to connect phone
2. `./gradlew assembleDebug`
3. `adb install app/build/outputs/apk/debug/app-debug.apk`
4. User manual tests on phone
5. User confirms → Proceed to next feature

**Do NOT move to next feature until manual test is confirmed.**

## Language & Docs
- UI strings: English (extractable to strings.xml)
- All docs: Bilingual CN/EN, cross-linked
- Commits: English
- Code comments: English
