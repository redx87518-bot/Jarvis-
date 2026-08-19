# AGENTS.md — Contributor Guide

## Project layout

```
JARVIS/
├── app/                      # Application module (launcher entry point)
├── core/                     # Agent core: intent, planner, executor, memory, tasks
├── ai/                       # AI model providers (LLM, vision, STT, TTS)
├── android/                  # Android platform interaction (apps, notifications, gestures)
├── skills/                   # Skills as Gradle modules (whatsapp, email, browser, …)
├── data/                     # Local storage (memory, tasks, settings)
├── voice/                    # Voice pipeline (wake word, STT, TTS)
├── docs/                     # Specifications & design docs
└── build-conventions/        # Shared Gradle convention plugins
```

## Tooling requirements

- **JDK** 17 or later
- **Android Gradle Plugin** 8.5+ (managed by Gradle wrapper)
- **Android SDK** — set `ANDROID_HOME` / `ANDROID_SDK_ROOT`
- **Kotlin** 2.0+ (via Gradle)

## Conventions

- Kotlin idiomatic multi-platform where feasible; Android modules are `com.android.application` / `com.android.library`.
- Jetpack Compose for all UI.
- Coroutines + StateFlow for async/streaming state.
- Single source of truth: each module exposes a small public API surface.
- No hard-coded API keys. Secrets live in Android Keystore / encrypted prefs, never in source.
- All sensitive actions are confirmation-gated (see §22 Safety).

## Phase 1 deliverables (this pass)

- Installable launcher APK (Home, App drawer, App search, App cards, Settings, dark/light).

## Building & verifying

```bash
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
```
