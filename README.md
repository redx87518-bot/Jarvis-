# JARVIS Launcher — Android AI Launcher & Autonomous Agent

> **Note:** This project is an Android-native codebase. Building the APK requires
> the Android SDK, JDK 17+, and a compatible Android Gradle Plugin version.
> This environment does not include the Android toolchain, so the APK cannot be
> compiled here, but the full source tree is ready to open in Android Studio
> (Arctic Fox / Hedgehog / Iguana / later).

## Phases

Implementation follows the master spec phases:

| Phase | Focus | Status |
|-------|-------|--------|
| 1 | Launcher foundation (Home, App drawer, search, cards, settings) | In progress |
| 2 | JARVIS chat + model router | Planned |
| 3 | Android control via AccessibilityService | Planned |
| 4 | Vision (screenshot, OCR, vision model) | Planned |
| 5 | Voice (wake word, Whisper STT, Chatterbox/Kokoro TTS) | Planned |
| 6 | Autonomous agent (planner, executor, verifier, recovery) | Planned |
| 7 | Skills (WhatsApp, Gmail, Browser, GitHub, …) | Planned |
| 8 | Memory & task resume | Planned |
| 9 | Developer + Cybersecurity skills | Planned |
| 10 | Optimization | Planned |

## Building

```bash
# From repo root, after installing Android SDK + setting ANDROID_HOME:
./gradlew :app:assembleDebug
```

See [AGENTS.md](AGENTS.md) for contributor guidelines.
