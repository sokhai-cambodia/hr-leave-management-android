# CLAUDE.md — hr-leave-management-android

Project-specific rules for this repo. These override the default global instructions where they
conflict.

## Git commits

- This is a MIT master's final project submission — commits must look like normal solo
  authorship, not AI-assisted.
- **Never** add `Co-Authored-By: Claude` (or any AI attribution) to commit messages.
- **Never** mention Claude, Claude Code, or AI assistance in commit messages, PR descriptions, or
  code comments.
- Push to `origin` is allowed without asking each time, once a change is committed and verified
  (build/lint clean) — this repo's earlier "ask first before pushing" boundary in `SPEC.md` §10
  is superseded by this instruction for routine task commits. Still ask first for anything
  destructive (force-push, branch deletion, history rewrite).

## Verification — no emulator by default

- Do **not** boot/use the Android emulator to verify a task as part of routine development — it's
  slow and burns a lot of tokens/time per run.
- After finishing a task, verify with `./gradlew assembleDebug`, `./gradlew lint`, and
  `./gradlew testDebugUnitTest` only (build/lint/unit-test clean = done).
- The user verifies actual on-device behavior themselves on a physical phone. Don't install APKs
  or drive the app via `adb`/emulator unless the user explicitly asks for it (e.g. debugging a
  report they bring back).
