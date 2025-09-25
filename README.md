# Play & Translate — WordSearch (MVP)

A tiny Jetpack Compose word-search game. It builds a grid from a bilingual word pack, lets you find words by tapping **start → end** (straight line), and includes **Hint** and **Shuffle** actions. The code is deliberately small and test-friendly.

---

## Quick start

    # clone and open in Android Studio
    ./gradlew test            # run JVM unit tests
    ./gradlew installDebug    # install on a connected device/emulator

**Minimum toolchain**

- JDK **17** (required by AGP 8.x)  
- Android Gradle Plugin **8.13.0**  
- Kotlin **2.0.21**  
- Compose BOM **2024.09.00**

---

## Project structure

    app/src/main/java/com/playandtranslate/wordsearch/
    ├─ WordSearchApp.kt # Application: initializes ServiceLocator
    ├─ MainActivity.kt # Single-activity Compose host
    ├─ di/
    │ └─ ServiceLocator.kt # Tiny DI for MVP
    ├─ data/
    │ ├─ PackRepository.kt # Repository interface
    │ ├─ AssetsPackRepository.kt # Loads packs from assets/packs/*.json
    │ └─ WordPackModels.kt # Kotlinx serialization models
    ├─ domain/
    │ ├─ Cell.kt / Pos.kt / Types.kt / WordPlacement.kt / GridBuild.kt
    ├─ words/
    │ ├─ GenerateGrid.kt # Grid generator interface
    │ ├─ SimpleGenerateGrid.kt # MVP implementation
    │ └─ Lines.kt # straightLine helper
    └─ ui/
    ├─ GameUiState.kt
    ├─ GameViewModel.kt # game logic + immutable state + hint/shuffle
    └─ screens/GameScreen.kt # Compose UI (grid + HUD + debug list)


---

## Features

- Tap **start → end** (horizontal / vertical / diagonal). If path matches a placed word (forward or reverse), cells are marked **Found**.  
- **Hint**: reveals one letter cell for the next unfound word.  
- **Shuffle**: regenerates the board using the same pack.  
- HUD: **Found X/Y · Hints N**.  
- Edge-to-edge UI with dynamic colors (Android 12+).  
- Release builds enable **R8** and **resource shrinking**.

---

## Packs (assets)

Built-in packs are JSON files under:
app/src/main/assets/packs/
basics.json


**Pack schema** (see `data/WordPackModels.kt`):

    {
      "packId": "basics",
      "version": 1,
      "sourceLang": "de",
      "targetLang": "en",
      "direction": "LTR",
      "title": "Basics (DE → EN)",
      "origin": "builtin",
      "words": [
        { "source": "Wasser", "target": "water" },
        { "source": "Apfel",  "target": "apple" },
        { "source": "Brot",   "target": "bread" }
      ]
    }

**Add a pack**

1. Create `app/src/main/assets/packs/<your-pack>.json` with the schema above.  
2. The app auto-discovers packs by filename.  
3. In MVP it loads the first pack returned by the repo; a pack picker can be added later.

---

## Build & run

- **Debug**: standard `installDebug` task.  
- **Release**: R8/resource shrink are enabled.

`app/build.gradle.kts` (excerpt)

    buildTypes {
      release {
        isMinifyEnabled = true
        isShrinkResources = true
        proguardFiles(
          getDefaultProguardFile("proguard-android-optimize.txt"),
          "proguard-rules.pro"
        )
      }
      debug {
        isMinifyEnabled = false
      }
    }

---

## Tests

JVM unit tests live in `app/src/test/java`:

    testutil/MainDispatcherRule.kt
    ui/GameViewModelLoadTest.kt # load & mapping
    ui/GameViewModelTest.kt # find word: forward/reverse
    ui/GameViewModelHintTest.kt # hint reveals one cell
    words/StraightLineTest.kt # path helper
    words/SimpleGenerateGridTest.kt # generator behavior


Run all tests:

    ./gradlew test

Notes:
- The ViewModel injects a computation dispatcher; tests pass the rule’s dispatcher so `advanceUntilIdle()` waits for grid generation.

---

## Code style & architecture

- **Single immutable UI state** (`GameUiState`) with small reducer-style updates.  
- **Domain models** are tiny value objects (`Cell`, `Pos`, etc.). `Pos` is `Comparable` (row-major).  
- **ServiceLocator** initializes once in `WordSearchApp`—no DI framework yet.  
- Heavy work (grid generation) runs on a background dispatcher.  
- Compose screens are pure: they read state and emit events.

An `.editorconfig` is included for consistent formatting.

---

## Continuous Integration

Minimal CI workflow in `.github/workflows/ci.yml`:

- Validates Gradle wrapper  
- Runs **unit tests**  
- Runs **Android Lint** for debug

(Assemble release is available but commented to keep CI fast.)

---

## Roadmap

- Final screen + New Game button.
- Word list UX (chips / translation toggle).  
- Persist progress (DataStore).
- Difficulty toggles: size, diagonals, intersections.  
- Pack picker UI & state save.  
- Menu for viewing and adding packs.
- More generator heuristics (nicer overlaps, fewer skips).
