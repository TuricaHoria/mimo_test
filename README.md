# Mimo Test App

An Android application built with Jetpack Compose that implements a lesson-based learning system using the MVI (Model-View-Intent) architecture pattern.

## Features

- 📚 **Lesson System**: Displays interactive lessons fetched from a remote API
- ✏️ **Input Validation**: Supports lessons with and without user input fields
- ✅ **Progress Tracking**: Tracks lesson completion with start and end timestamps
- 💾 **Local Storage**: Persists lesson completion data using Room database
- 🎨 **Animations**: Smooth animations for correct/incorrect answers and completion
- 🔄 **Error Handling**: User-friendly error dialogs with retry functionality
- 🎯 **Done Screen**: Completion screen with restart and close options

## Architecture

The app follows the **MVI (Model-View-Intent)** architecture pattern:

- **Model**: State management through `StateFlow` and `SharedFlow`
- **View**: Jetpack Compose UI components
- **Intent**: User actions and system events
- **ViewModel**: Business logic and state management

### Key Components

- **MVI Core**: Base classes for ViewModel, State, Intent, and Effects
- **Feature Modules**: Organized by feature (lesson)
- **Data Layer**: Repository pattern with remote API and local database
- **Dependency Injection**: Koin for dependency management

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: MVI (Model-View-Intent)
- **Dependency Injection**: Koin
- **Networking**: Retrofit + Gson
- **Database**: Room
- **Coroutines**: Kotlin Coroutines & Flow
- **Testing**: JUnit, MockK, Turbine, Robolectric

## Project Structure

```
app/src/main/java/com/example/mimotest/
├── core/
│   └── mvi/
│       └── MviCore.kt              # MVI base classes
├── feature/
│   └── lesson/
│       ├── LessonContract.kt      # State, Intent, Effect definitions
│       ├── LessonViewModel.kt     # Business logic
│       ├── LessonScreen.kt        # Main UI screen
│       ├── LessonContentRow.kt    # Lesson content rendering
│       └── LessonAnimations.kt    # Animation composables
│       └── data/
│           ├── LessonNetwork.kt  # API service & data sources
│           ├── LessonRepository.kt # Repository implementation
│           ├── LessonDatabase.kt  # Room database
│           └── LessonDao.kt       # Database access
├── di/
│   └── AppModule.kt              # Koin dependency injection
└── ui/
    ├── splash/
    │   └── SplashScreen.kt       # Splash screen
    └── theme/                     # Material theme
```

## Setup

### Prerequisites

- Android Studio Hedgehog or later
- JDK 17 or later
- Android SDK (API level 24+)

### Installation

1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Run the app on an emulator or physical device

### Build

```bash
./gradlew assembleDebug
```

### Run Tests

```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest
```

## API Integration

The app fetches lessons from:
```
https://mimochallenge.azurewebsites.net/api/lessons
```

The API returns a JSON object with a `lessons` array containing lesson data.

## Database

The app uses Room database to persist:
- **Lesson Start Times**: When each lesson was started
- **Lesson Completions**: Completion events with start and end timestamps

To view database contents in debug builds, check Logcat for `DatabaseDebug` tag.

## Key Features Explained

### Lesson Rendering

Lessons can have:
- **Text segments** with different colors
- **Optional input fields** where users must type the correct answer

The `LessonContentRow` composable handles the complex logic of:
- Rendering text segments with their colors
- Finding which segment contains the input field
- Splitting segments around input fields
- Rendering the input field inline with text

### State Management

The app uses a unidirectional data flow:
1. User actions trigger `Intent`s
2. ViewModel processes intents and updates `State`
3. UI observes state changes and recomposes
4. One-time events are emitted via `Effect`

### Animations

- **Correct Answer**: Checkmark animation with "Correct!" message
- **Wrong Answer**: Cross animation with "Try again" message
- **Done Screen**: Pulsing logo and text animation

## Resources

All UI values are externalized to resource files:
- `strings.xml`: All text content
- `colors.xml`: Color definitions
- `dimens.xml`: Dimensions and animation parameters

## Testing

The project includes:
- **Unit Tests**: ViewModel, Repository, Data Sources
- **Instrumented Tests**: Room database operations
- **Test Utilities**: MockK for mocking, Turbine for Flow testing

## License

This project is a test application for demonstration purposes.
