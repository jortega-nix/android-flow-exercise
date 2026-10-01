# Flow Aggregation Exercise

A small Kotlin project with one function to implement and seven tests that must pass.
Pure Kotlin and coroutines — no Android SDK needed.

## Setup

You need a **JDK 11 or newer** (17, 21 and 25 all work) and nothing else. Gradle and all
dependencies download automatically on the first run.

```bash
git clone <this repository URL>
cd android-flow-exercise
./gradlew test          # macOS / Linux
gradlew.bat test        # Windows
```

Any IDE works: **IntelliJ IDEA** or **Android Studio** (any reasonably recent version) →
File → Open → select this folder and wait for the Gradle sync.

The first run takes a minute or two while it downloads. After that it is a few seconds.

**Before you start, all seven tests fail with `NotImplementedError`. That is expected** — it means
your setup is working. Please run this once before the session.

## Your task

Open:

```
src/main/kotlin/com/example/flowexercise/FlowAggregationModels.kt
```

Implement `aggregateDesignData()`. Three independent streams of `Result<T>` have to be combined
into one `Result<DesignScreenState>` stream, following the priority rules in the KDoc above the
function.

## Verifying

```bash
./gradlew test
```

Or in the IDE, right-click `FlowAggregationTest` → Run. When you are done, all seven pass.

## If something goes wrong

- **Gradle sync fails in the IDE** — try the command line first. If that works, the project is
  fine and it is an IDE setting (Settings → Build Tools → Gradle → Gradle JDK = 11 or newer).
- **Anything else** — say so straight away. Setup problems are not part of what is being assessed
  and we will not spend the session on them.

## Notes

- Please do not push to this repository. Work locally.
- Everything you need is in `FlowAggregationModels.kt` and `FlowAggregationTest.kt`.
- Think out loud as you work — the reasoning matters more than landing it.
