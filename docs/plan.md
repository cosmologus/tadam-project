# Currency Exchange App - Implementation Plan

## Guiding Principle: KEEP IT STUPID SIMPLE

This project must be implementable by a first-year student in under 2 hours. Every decision should favor the simplest possible approach:

- **No extra libraries** beyond what's listed in the dependencies section. If something needs a helper library, find another way or hardcode it.
- **Hardcode over dynamic** whenever possible. Hardcode the currency list, hardcode default values, hardcode strings directly in composables instead of using string resources (except where absolutely needed).
- **No abstractions for the sake of abstractions**. No interfaces unless needed for testing. No sealed classes for navigation routes - just use plain strings. No custom result wrappers - just use try/catch.
- **Flat and obvious code**. Prefer longer but readable code over clever/compact code. A student should be able to read any file and understand it immediately.
- **No generics, no extension functions, no DSLs**. Write plain, boring Kotlin.
- **Copy-paste is fine**. If two ViewModels look similar, that's OK. Don't create shared base classes.
- **Minimal error handling**. Just wrap API calls in try/catch, show "Error" to user. No retry logic, no custom exceptions.
- **No formatting libraries**. Format timestamps with simple string concatenation or basic SimpleDateFormat. No java.time, no custom formatters.
- **If ExposedDropdownMenuBox is too complex**, use a simple Column of clickable Texts, or even plain radio buttons. Pick whatever compiles easiest.
- **Skip animations, transitions, and polish**. Default Material3 look is fine. No custom colors beyond dark/light toggle.

## Overview

A simple Android currency exchange app built with Kotlin and Jetpack Compose. The app lets users convert between currencies using live exchange rates, stores conversion history locally, and supports theme customization.

## Requirements Coverage

### Mandatory Requirements
- Kotlin programming language
- Jetpack Compose toolkit
- Two screens (Converter + Settings)
- Jetpack Navigation for screen transitions
- Recommended application architecture (MVVM + Repository + Manual DI)
- API integration via Retrofit (Frankfurter API)

### Bonus Points
| Bonus | Approach |
|---|---|
| Room DB (0.5p) | Store conversion history in a single Room table |
| Input sanitization (0.5p) | Room parameterized queries prevent SQL injection; amount input restricted to `KeyboardType.Decimal` |
| Encrypted communication (0.5p) | All API calls use HTTPS/TLS (Frankfurter API endpoint: `https://api.frankfurter.dev`) |
| Settings screen (0.5p) | Dark/Light theme toggle persisted with DataStore Preferences |
| Unit testing (0.5p) | ViewModel unit tests with a fake repository |
| Clean code (0.5p) | MVVM architecture, clear package structure, code comments |

---

## Architecture

### Pattern
MVVM + Repository + Manual Dependency Injection (AppContainer pattern), following Android Basics with Compose codelab recommendations (Unidirectional Data Flow).

### Package Structure
```
com.example.tadamproject/
├── TadamApplication.kt              # Custom Application class, initializes AppContainer
├── AppContainer.kt                   # Manual DI container
├── MainActivity.kt                   # Entry point, sets up theme + NavHost
│
├── model/
│   └── ConversionHistory.kt          # Room entity
│
├── data/
│   ├── local/
│   │   ├── ConversionHistoryDao.kt   # Room DAO
│   │   └── AppDatabase.kt           # Room database
│   ├── remote/
│   │   └── FrankfurterApiService.kt  # Retrofit API interface
│   ├── preferences/
│   │   └── ThemePreferences.kt       # DataStore wrapper for theme setting
│   └── CurrencyRepository.kt        # Single repository: API calls + DB operations
│
├── ui/
│   ├── navigation/
│   │   └── AppNavigation.kt          # NavHost with routes
│   ├── converter/
│   │   ├── ConverterScreen.kt        # Main converter UI
│   │   └── ConverterViewModel.kt     # ViewModel for converter logic
│   ├── settings/
│   │   ├── SettingsScreen.kt         # Settings UI
│   │   └── SettingsViewModel.kt      # ViewModel for settings
│   └── theme/
│       ├── Theme.kt                  # Dynamic theme (already exists, modify)
│       ├── Color.kt                  # (already exists)
│       └── Type.kt                   # (already exists)
│
└── test/
    └── ConverterViewModelTest.kt     # Unit tests with fake repository
```

---

## API

### Frankfurter API
- **Base URL**: `https://api.frankfurter.dev/`
- **Endpoint**: `GET /v1/latest?base={FROM}&symbols={TO}`
- **Example**: `GET /v1/latest?base=USD&symbols=EUR`
- **Response**:
```json
{
  "amount": 1.0,
  "base": "USD",
  "date": "2025-04-22",
  "rates": {
    "EUR": 0.88
  }
}
```
- No API key required
- HTTPS only (covers encrypted communication bonus)

### Retrofit Setup
- Use Retrofit with `kotlinx.serialization` (or Gson) converter
- Single `FrankfurterApiService` interface with one `@GET` function

---

## Database (Room)

### Entity: `ConversionHistory`
```kotlin
@Entity(tableName = "conversion_history")
data class ConversionHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fromCurrency: String,
    val toCurrency: String,
    val amount: Double,
    val result: Double,
    val timestamp: Long = System.currentTimeMillis()
)
```

### DAO: `ConversionHistoryDao`
```kotlin
@Dao
interface ConversionHistoryDao {
    // Parameterized queries - prevents SQL injection by design
    @Insert
    suspend fun insert(conversion: ConversionHistory)

    @Query("SELECT * FROM conversion_history ORDER BY timestamp DESC")
    fun getAll(): Flow<List<ConversionHistory>>
}
```

### Database: `AppDatabase`
- Single Room database with one entity and one DAO
- Singleton access via `AppContainer`

---

## Screens

### Screen 1: Converter Screen
**Route**: `"converter"`

**UI Components** (top to bottom):
1. **Top App Bar** with title "Currency Exchange" and a settings icon button (navigates to Settings)
2. **"From" dropdown** - `ExposedDropdownMenuBox` with hardcoded currencies: `USD, EUR, GBP, RON, JPY, CHF`
3. **"To" dropdown** - Same currency list
4. **Amount input** - `OutlinedTextField` with `KeyboardType.Decimal` (input sanitization: restricts to numbers and decimal point)
5. **Convert button** - `Button` that triggers API call
6. **Result display** - `Text` showing the converted amount (or loading indicator / error message)
7. **History section** - `Text` header "Recent Conversions" + `LazyColumn` showing past conversions from Room DB (fromCurrency -> toCurrency: amount = result, with formatted timestamp)

**ViewModel State**:
```kotlin
data class ConverterUiState(
    val fromCurrency: String = "USD",
    val toCurrency: String = "EUR",
    val amount: String = "",
    val result: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val history: List<ConversionHistory> = emptyList()
)
```

**ViewModel Actions**:
- `updateFromCurrency(currency: String)`
- `updateToCurrency(currency: String)`
- `updateAmount(amount: String)`
- `convert()` - calls repository, saves result to Room

### Screen 2: Settings Screen
**Route**: `"settings"`

**UI Components**:
1. **Top App Bar** with title "Settings" and a back arrow
2. **Theme toggle** - `Switch` with label "Dark Mode". Current value read from DataStore. Toggle writes to DataStore. Theme change is applied immediately across the app.

**ViewModel State**:
```kotlin
data class SettingsUiState(
    val isDarkMode: Boolean = false
)
```

---

## Navigation

- `NavHost` with `startDestination = "converter"`
- Two routes: `"converter"` and `"settings"`
- Converter -> Settings: via settings icon in top bar
- Settings -> Converter: via back arrow or system back

---

## DataStore Preferences (Theme)

### ThemePreferences
```kotlin
class ThemePreferences(private val dataStore: DataStore<Preferences>) {
    val isDarkMode: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[DARK_MODE_KEY] ?: false
    }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[DARK_MODE_KEY] = enabled
        }
    }

    companion object {
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
    }
}
```

- DataStore instance created in `AppContainer`
- Theme state observed in `MainActivity` and passed to the `Theme` composable

---

## Manual Dependency Injection

### AppContainer
```kotlin
class AppContainer(context: Context) {
    // Database
    private val database = Room.databaseBuilder(
        context, AppDatabase::class.java, "tadam_database"
    ).build()

    // API
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.frankfurter.dev/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val apiService = retrofit.create(FrankfurterApiService::class.java)

    // DataStore
    val themePreferences = ThemePreferences(context.dataStore)

    // Repository
    val currencyRepository = CurrencyRepository(apiService, database.conversionHistoryDao())
}
```

### TadamApplication
```kotlin
class TadamApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
```

Register in `AndroidManifest.xml`: `android:name=".TadamApplication"`

---

## Repository

### CurrencyRepository
```kotlin
class CurrencyRepository(
    private val apiService: FrankfurterApiService,
    private val dao: ConversionHistoryDao
) {
    suspend fun convert(from: String, to: String, amount: Double): Double {
        val response = apiService.getLatestRates(base = from, symbols = to)
        return response.rates[to]!! * amount
    }

    suspend fun saveConversion(conversion: ConversionHistory) {
        dao.insert(conversion)
    }

    fun getConversionHistory(): Flow<List<ConversionHistory>> {
        return dao.getAll()
    }
}
```

---

## Unit Tests

### ConverterViewModelTest
Test the `ConverterViewModel` using a `FakeCurrencyRepository`:
- **Test 1**: `convert_updatesResultState` - Call `convert()`, verify `result` in UI state is updated correctly
- **Test 2**: `convert_savesToHistory` - Call `convert()`, verify the conversion appears in history
- **Test 3**: `convert_withError_updatesErrorState` - Fake repo throws exception, verify `error` state is set

The fake repository returns hardcoded values and stores conversions in an in-memory list.

---

## Dependencies to Add

Add to `app/build.gradle.kts`:
```kotlin
// Navigation
implementation("androidx.navigation:navigation-compose:2.8.9")

// Room
implementation("androidx.room:room-runtime:2.7.1")
implementation("androidx.room:room-ktx:2.7.1")
ksp("androidx.room:room-compiler:2.7.1")

// Retrofit
implementation("com.squareup.retrofit2:retrofit:2.11.0")
implementation("com.squareup.retrofit2:converter-gson:2.11.0")

// DataStore
implementation("androidx.datastore:datastore-preferences:1.1.7")

// Testing
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
```

Add KSP plugin to root `build.gradle.kts`:
```kotlin
id("com.google.devtools.ksp") version "2.2.10-1.0.33" apply false
```

Apply in `app/build.gradle.kts`:
```kotlin
id("com.google.devtools.ksp")
```

---

## Key Implementation Notes

1. **Input sanitization**: The amount `TextField` uses `KeyboardType.Decimal` to restrict input. Room DAO uses parameterized queries (`:parameter` syntax) which prevents SQL injection. Add comments in code explaining this.

2. **Encrypted communication**: All Retrofit calls go to `https://api.frankfurter.dev` (HTTPS/TLS). Add a comment in the Retrofit setup noting that communication is encrypted via TLS.

3. **Error handling**: The ConverterViewModel should catch network exceptions and display a user-friendly error message in the UI state.

4. **Theme**: `MainActivity` observes `ThemePreferences.isDarkMode` as state and passes it to the app's `Theme` composable. The existing `Theme.kt` should be modified to accept a `darkTheme` parameter controlled by DataStore rather than system default.

5. **ViewModelFactory**: Since we use manual DI, each ViewModel needs a `ViewModelProvider.Factory` that gets the repository from `AppContainer`. Use `viewModelFactory { initializer { ... } }` pattern with `CreationExtras`.
