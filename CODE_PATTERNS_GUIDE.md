# Code Patterns Guide - Portfolio Watcher

This guide explains the key patterns and idioms used throughout the codebase.

---

## 🎯 Pattern 1: Data Classes

### What It Is
```kotlin
data class User(
    val userId: String,
    val email: String,
    val phone: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
```

### Why We Use It
- Automatically generates `equals()`, `hashCode()`, `toString()`
- Generates `copy()` for creating modified versions
- Immutable by default (all properties are `val`)
- Perfect for representing domain models

### How to Use
```kotlin
// Create
val user = User(userId = "123", email = "user@example.com")

// Access properties
println(user.email)  // "user@example.com"

// Create modified copy
val updatedUser = user.copy(email = "new@example.com")
// Creates new User with email changed, everything else same
```

---

## 🎯 Pattern 2: Sealed Classes

### What It Is
```kotlin
sealed class UiState {
    object Loading : UiState()
    data class Success(val portfolios: List<Portfolio>) : UiState()
    data class Error(val message: String) : UiState()
    object Empty : UiState()
}
```

### Why We Use It
- Type-safe state representation
- Can't create invalid states (compiler enforces all states)
- Enables exhaustive `when` expressions (compiler warns if missing case)
- Clear, readable code for state management

### How to Use
```kotlin
when (val state = uiState.value) {
    is UiState.Loading -> showSpinner()
    is UiState.Success -> showPortfolios(state.portfolios)
    is UiState.Error -> showError(state.message)
    is UiState.Empty -> showEmptyState()
}
// Compiler error if you forget any case!
```

---

## 🎯 Pattern 3: StateFlow (Reactive Streams)

### What It Is
```kotlin
private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
val uiState: StateFlow<UiState> = _uiState.asStateFlow()  // Read-only for outside
```

### Why We Use It
- Emits state updates automatically
- UI can subscribe and update when state changes
- Thread-safe, survives configuration changes
- Part of Kotlin coroutines (modern approach)

### How to Use in ViewModel
```kotlin
// Update state (only ViewModel can do this)
_uiState.value = UiState.Success(portfolios)

// Notify observers of change (happens automatically)
// All code collecting from uiState Flow immediately sees the new value
```

### How to Use in UI
```kotlin
@Composable
fun PortfolioScreen(viewModel: PortfolioViewModel) {
    // Collect state changes and recompose when state changes
    val state by viewModel.uiState.collectAsState()
    
    when (state) {
        is UiState.Loading -> LoadingScreen()
        is UiState.Success -> PortfolioList(state.portfolios)
        is UiState.Error -> ErrorScreen(state.message)
        is UiState.Empty -> EmptyScreen()
    }
}
```

---

## 🎯 Pattern 4: Repository Interface

### What It Is
```kotlin
interface PortfolioRepository {
    suspend fun getPortfolio(portfolioId: String): Portfolio?
    suspend fun savePortfolio(portfolio: Portfolio): String
    suspend fun updateNAV(portfolioId: String, navUpdates: Map<String, BigDecimal>)
    // ... more methods
}
```

### Why We Use It
- Defines contract for data access (what operations are available)
- Implementation can be anything (Room, API, Cache, Mock)
- Easy to test (create mock implementation)
- Decouples UI from data source

### How to Use
```kotlin
// ViewModel doesn't care HOW data is stored
class PortfolioViewModel(private val repository: PortfolioRepository) {
    fun loadPortfolios(userId: String) {
        viewModelScope.launch {
            // Could be database, API, cache - ViewModel doesn't know!
            val portfolios = repository.getPortfoliosByUserId(userId)
        }
    }
}

// You can swap implementations without changing ViewModel:
// val repo: PortfolioRepository = RoomPortfolioRepository(database)
// val repo: PortfolioRepository = MockPortfolioRepository()  // For testing
```

---

## 🎯 Pattern 5: Room DAO Queries

### What It Is
```kotlin
@Dao
interface UserDao {
    // Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    
    // Read
    @Query("SELECT * FROM users WHERE user_id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?
    
    // Update
    @Update
    suspend fun updateUser(user: UserEntity)
    
    // Delete
    @Delete
    suspend fun deleteUser(user: UserEntity)
    
    // Reactive Read (with Flow)
    @Query("SELECT * FROM users WHERE user_id = :userId")
    fun observeUser(userId: String): Flow<UserEntity?>
}
```

### Why We Use It
- Compile-time SQL verification (catches errors early)
- Automatic SQL generation (no manual string concatenation)
- Suspend support (non-blocking database operations)
- Flow support (reactive queries)

### Anatomy of a Query
```kotlin
@Query("SELECT * FROM users WHERE email = :email LIMIT 1")
suspend fun getUserByEmail(email: String): UserEntity?
```

| Part | Meaning |
|------|---------|
| `@Query(...)` | Tell Room this is a query |
| `SELECT *` | Get all columns |
| `FROM users` | From the users table |
| `WHERE email = :email` | Where email matches the parameter |
| `:email` | Parameter - maps to function parameter `email: String` |
| `LIMIT 1` | Return only 1 result (since we want single user) |
| `suspend` | Non-blocking operation (runs on coroutine) |
| `: UserEntity?` | Return type (nullable - might not exist) |

---

## 🎯 Pattern 6: Type Converters

### What It Is
```kotlin
class LocalDateTimeConverter {
    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? {
        return dateTime?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }
    
    @TypeConverter
    fun toLocalDateTime(dateTimeString: String?): LocalDateTime? {
        return dateTimeString?.let {
            LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        }
    }
}
```

### Why We Use It
- Room only stores primitives (String, Int, Long, Boolean)
- Complex types like LocalDateTime need conversion
- Automatic conversion happens transparently
- Keeps domain models using proper types

### How It Works
```kotlin
// When you save:
val user = User(id = "1", email = "user@example.com", createdAt = LocalDateTime.now())
// Converter automatically converts createdAt (LocalDateTime) → String "2024-06-29T14:30:45"
// Saved in database as String

// When you load:
val userFromDb: UserEntity = userDao.getUserById("1")
// Converter automatically converts String "2024-06-29T14:30:45" → LocalDateTime
// userFromDb.createdAt is LocalDateTime (proper type in code)
```

---

## 🎯 Pattern 7: Suspend Functions & Coroutines

### What It Is
```kotlin
suspend fun getPortfolio(portfolioId: String): Portfolio? {
    // This function can be paused and resumed
    // Doesn't block the thread while waiting
}

// Call it from a coroutine scope
viewModelScope.launch {
    val portfolio = repository.getPortfolio("123")  // Pauses here
    // Resumes when repository returns
    updateUI(portfolio)
}
```

### Why We Use It
- Don't block main thread (prevents ANR - "App Not Responding")
- Cleaner syntax than callbacks or Rx
- Throwable exceptions (no try-catch hell)
- Tied to lifecycle (automatic cleanup)

### Key Concepts
| Term | Meaning |
|------|---------|
| `suspend` | Function can be paused and resumed (must run in coroutine) |
| `launch` | Fire-and-forget coroutine (ignore result) |
| `async` | Coroutine that returns a value (use `await()`) |
| `viewModelScope` | Coroutines automatically cancelled when ViewModel destroyed |
| `withContext` | Switch to different thread (e.g., IO, Main) |

---

## 🎯 Pattern 8: Nullable Types & Null Safety

### What It Is
```kotlin
val phone: String? = null     // Can be null
val userId: String = "123"    // Cannot be null

val portfolio: Portfolio? = repository.getPortfolio("123")
// Might return null if not found

// Must handle nullability
if (portfolio != null) {
    println(portfolio.name)  // Safe to use here
} else {
    println("Portfolio not found")
}

// Or use safe call operator
println(portfolio?.name)  // Returns null if portfolio is null, otherwise name

// Or use Elvis operator (provide default)
val name = portfolio?.name ?: "Unnamed Portfolio"
```

### Why We Use It
- Prevents NullPointerException crashes
- Compiler enforces null-safety
- Clear contract (? = might be null, no ? = never null)

---

## 🎯 Pattern 9: Extension Properties

### What It Is
```kotlin
// In Portfolio model:
val currentTotalValue: BigDecimal
    get() = holdings.fold(BigDecimal.ZERO) { sum, holding ->
        sum + holding.currentValue
    }
```

### Why We Use It
- Calculate values on-the-fly (always up-to-date)
- Don't store redundant data
- Read like properties (`portfolio.currentTotalValue`)
- Computed once per access (no caching)

### How It Works
```kotlin
val portfolio = Portfolio(...)

// First access: calculates sum
val value1 = portfolio.currentTotalValue  // Calculation happens

// Second access: calculates again (fresh value)
val value2 = portfolio.currentTotalValue  // Calculation happens again

// If you need to cache:
val cachedValue = portfolio.currentTotalValue  // Calculate once
// Later use cachedValue, not portfolio.currentTotalValue
```

---

## 🎯 Pattern 10: Higher-Order Functions

### What It Is
```kotlin
// fold = reduce list to single value by applying function
val totalValue = holdings.fold(BigDecimal.ZERO) { sum, holding ->
    sum + holding.currentValue
}

// Equivalent to:
var total = BigDecimal.ZERO
for (holding in holdings) {
    total = total + holding.currentValue
}
```

### Why We Use It
- Functional approach (no manual loops)
- Cleaner, more readable code
- Safe (automatic handling of edge cases)
- Chainable with other operations

### Common Higher-Order Functions
| Function | What It Does |
|----------|--------------|
| `map { }` | Transform each item (1-to-1) |
| `filter { }` | Keep only items matching condition |
| `fold { }` | Reduce to single value |
| `find { }` | Get first item matching condition |
| `any { }` | Check if any item matches |
| `all { }` | Check if all items match |

---

## 💡 Summary

These patterns work together to create:
- **Type Safety** - Compiler catches errors early
- **Null Safety** - No surprise null pointer crashes
- **Reactive UI** - Automatic updates when data changes
- **Clean Architecture** - Clear separation of concerns
- **Testability** - Easy to mock and test
- **Maintainability** - Clear, readable code

Master these patterns and you'll understand 90% of modern Android development!
