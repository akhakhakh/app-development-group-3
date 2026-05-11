# FiveSensors

A collection of 5 sensor-powered mini-games built with Kotlin and Android Studio.

## Project Structure

```
FiveSensors/
├── app/                  → Main app (splash, intro, game list, game detail screens)
├── touchscreen1p/        → Game 1: Touchscreen 1-player
├── touchscreen2p/        → Game 2: Touchscreen 2-player
├── microphone/           → Game 3: Microphone
├── gyroscope/            → Game 4: Gyroscope
├── camera/               → Game 5: Camera
```

The `app` module is the navigation shell that ties everything together. Each game module is independent, you only need to touch your own module folder.

---

## Getting Started

### 1. Clone the repository

```
git clone <repo-url>
cd FiveSensors
```

### 2. Open in Android Studio

- Open Android Studio
- Choose **Open** and select the `FiveSensors` folder (the one containing `settings.gradle.kts`)
- Wait for Gradle sync to finish — this may take a few minutes the first time

### 3. Find your module

Open the **Project** panel on the left. Your game module is the folder with your game name (e.g. `gyroscope/`). Your entry point is:

```
<your-module>/src/main/java/com/group3/<your-game>/MainActivity.kt
```

This is where you start coding your game.

### 4. Run your game

- At the top of Android Studio, open the run configuration dropdown and select your module (e.g. `gyroscope`)
- Click the green **Run** button
- This runs your game in isolation, without going through the main app — useful for development

---

## Module Rules

Each game module must follow these rules so it integrates correctly with the main app:

- **One entry-point Activity** — your `MainActivity.kt` (or whatever you rename it to) is what the main app will launch
- **No launcher intent-filter** in your `AndroidManifest.xml` — the main app handles launching, not Android's home screen
- **No `applicationId`** in your `build.gradle.kts` — only the `app` module has one
- **`android.exported="false"`** on your Activity in `AndroidManifest.xml`

---

## Renaming Your Package or Activity

### Renaming the package (e.g. `com.group3.gyroscope` → `com.group3.gyromaze`)

1. In the Project panel, right-click your package folder → **Refactor → Rename**
2. Android Studio will rename all references automatically
3. Manually update the `namespace` field in your `build.gradle.kts` to match:
   ```kotlin
   namespace = "com.group3.gyromaze"
   ```
4. Check your `AndroidManifest.xml` — the `package` attribute (if present) should also match
5. Sync Gradle (**File → Sync Project with Gradle Files**)

### Renaming your Activity (e.g. `MainActivity` → `GyroscopeGameActivity`)

1. In the Project panel, right-click `MainActivity.kt` → **Refactor → Rename**
2. Type the new name — Android Studio updates all references
3. Update the activity name in your `AndroidManifest.xml`:
   ```xml
   <activity android:name=".GyroscopeGameActivity" android:exported="false" />
   ```
4. Tell whoever owns the `app` module so they can update the game list to point to your new Activity name

### Renaming the module itself (e.g. `gyroscope` → `gyro-maze`)

This is more involved — only do it if really necessary:

1. Rename the folder on disk
2. Update `settings.gradle.kts` at the root — change `include(":gyroscope")` to `include(":gyro-maze")`
3. Update `app/build.gradle.kts` — change `implementation(project(":gyroscope"))` to `implementation(project(":gyro-maze"))`
4. Sync Gradle

---

## How the Main App Connects to Your Game

The `app` module launches your game using an explicit Intent. In `app/`, there is a list like this:

```kotlin
val games = listOf(
    GameInfo("Touchscreen 1P", TouchScreen1PActivity::class.java),
    GameInfo("Gyroscope",      GyroscopeGameActivity::class.java),
    // ...
)
```

When a player taps **Play Now**, the app calls:

```kotlin
startActivity(Intent(this, GyroscopeGameActivity::class.java))
```

When your game is done, call `finish()` and the user is returned to the game list automatically.

**What this means for you:** just build your game inside your Activity. You don't need to worry about navigation — the main app handles that.

---

## Git Workflow

Each person works on their own branch:

```
git checkout -b feat/gyroscope
```

Only commit files inside your module folder (and any agreed shared changes). When your game is ready, open a pull request into `main`.

Avoid committing:
- `build/` folders (already in `.gitignore`)
- `local.properties` (machine-specific, already in `.gitignore`)
- `.gradle/` (already in `.gitignore`)

---
