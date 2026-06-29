# Portfolio Watcher - Complete Architecture Documentation

## 📋 Overview

This document explains the three-layer MVVM architecture implemented in the Portfolio Watcher Android app. Every line of code is documented to help you understand the system as you learn.

---

## 🏗️ Architecture Layers

### **Layer 1: PRESENTATION LAYER** 
*What users see and interact with*

**Location:** `presentation/` package

**Components:**
- **Activities & Fragments:** User interface screens
- **ViewModels:** Manage UI state and handle user interactions
- **Composables:** Jetpack Compose functions that build the UI
- **Theme:** Material3 colors, typography, styling

**Key Files Documented:**
- `MainActivity.kt` - App entry point with full screen documentation
- `presentation/viewmodel/PortfolioViewModel.kt` - Complete state management with comments explaining:
  - `UiState` sealed class for type-safe state
  - StateFlow for reactive state updates
  - All business logic functions (load, delete, refresh)
  - Error handling and loading states

**How it works:**
1. User interacts with UI (taps button, enters data)
2. ViewModel function is called (e.g., `loadPortfolios()`)
3. ViewModel updates StateFlow with new state
4. UI automatically re-renders thanks to Flow collection
5. User sees updated screen

---

### **Layer 2: DOMAIN LAYER**
*Business logic and data models*

**Location:** `domain/` package

**Components:**
- **Models:** Pure data classes representing business entities
- **Repository Interfaces:** Define data access contracts

**Key Files Documented:**

1. **User.kt** - User data model
   - `userId`: Unique identifier
   - `email`: User email
   - `phone`: Optional phone number
   - `createdAt`: Account creation timestamp

2. **Portfolio.kt** - Investment portfolio model
   - Represents a collection of mutual fund holdings
   - `portfolioId`: Unique ID
   - `holdings`: List of funds owned
   - **Calculated properties:**
     - `currentTotalValue`: Current market value (units × NAV)
     - `totalInvestment`: Total rupees invested
     - `totalGainLoss`: Total profit/loss
     - `totalGainLossPercentage`: Return percentage

3. **Holding.kt** - Individual fund holding
   - Represents one mutual fund investment
   - `isin`: Fund identification code
   - `fundName`: Display name
   - `units`: Number of shares held
   - `currentNAV`: Current price per unit
   - `investmentAmount`: Total rupees invested
   - `gainLoss`: Profit/loss in rupees
   - **Calculated properties:**
     - `currentValue`: Current total worth
     - `gainLossPercentage`: Return percentage

4. **Repository Interfaces:**
   - `PortfolioRepository.kt` - Contract for portfolio data operations:
     - `getPortfolio()` - Fetch one portfolio
     - `getPortfoliosByUserId()` - Fetch all user portfolios
     - `savePortfolio()` - Create new portfolio
     - `updatePortfolio()` - Modify existing portfolio
     - `deletePortfolio()` - Remove portfolio
     - `updateNAV()` - Update fund prices
     - `addHolding()` / `removeHolding()` / `updateHolding()`
   
   - `UserRepository.kt` - Contract for user operations:
     - `getUser()` - Fetch by ID
     - `getUserByEmail()` - Fetch by email
     - `saveUser()` - Create new user
     - `updateUser()` - Modify user
     - `deleteUser()` - Remove user
     - `observeUser()` - Reactive updates (Flow)

**Why this layer?**
- Isolates business logic from UI and database details
- Easy to test (interfaces make mocking simple)
- Easy to change data source (swap implementation, keep interface)
- Reusable across mobile/web/desktop

---

### **Layer 3: DATA LAYER**
*Where data is actually stored and accessed*

**Location:** `data/` package

**Components:**
- **Entities:** Database table representations
- **DAOs:** Database access methods
- **Database:** Room ORM configuration
- **TypeConverters:** Custom type handling

**Key Files Documented:**

1. **UserEntity.kt** - Database table for users
   - Maps to "users" table in SQLite
   - @Entity annotation tells Room this is a table
   - @PrimaryKey marks userId as unique identifier
   - Email is indexed (fast lookups)
   - Includes created_at timestamp

2. **UserDao.kt** - All user database operations
   - **Insert:** `insertUser()` - Add new user (replace if exists)
   - **Read:** 
     - `getUserById()` - Fetch by ID
     - `getUserByEmail()` - Fetch by email
     - `getAllUsers()` - Get all users
     - `userExists()` - Check existence
   - **Update:** `updateUser()` - Modify existing user
   - **Delete:** `deleteUser()` - Remove user
   - **Reactive:** 
     - `observeUser()` - Watch single user for changes
     - `observeAllUsers()` - Watch all users for changes
   
   All methods are suspend functions (coroutine-compatible)
   Flow support for reactive UI updates

3. **AppDatabase.kt** - Main database configuration
   - `@Database` annotation specifies tables and version
   - Includes all DAOs (UserDao, PortfolioDao, HoldingDao)
   - Singleton pattern ensures one database connection
   - `getInstance()` - Get or create database
   - `createDatabase()` - Configure Room builder

4. **LocalDateTimeConverter.kt** - Type conversion
   - Room only stores primitive types (String, Int, Long, etc.)
   - This converter handles `java.time.LocalDateTime`
   - `fromLocalDateTime()` - DateTime → ISO String (for storage)
   - `toLocalDateTime()` - ISO String → DateTime (for loading)
   - Uses ISO format "2024-06-29T14:30:45" (sortable, standard)

**Why this layer?**
- Separates data access from business logic
- One source of truth (single database instance)
- Easy to swap implementations (Room → Firestore)
- Automatic SQL generation (compile-time safety)

---

## 🔄 Data Flow Example: "Load Portfolios"

### Step 1: User Taps "Load" Button (UI)
```
MainActivity → Calls PortfolioViewModel.loadPortfolios(userId)
```

### Step 2: ViewModel Handles Request (Domain/Presentation)
```
PortfolioViewModel.loadPortfolios():
1. Sets _isLoading = true (show spinner)
2. Sets _uiState = Loading (show loading state)
3. Calls repository.getPortfoliosByUserId(userId)
4. Launches in viewModelScope (non-blocking coroutine)
```

### Step 3: Repository Fetches Data (Data Layer)
```
PortfolioRepository.getPortfoliosByUserId(userId):
1. Calls portfolioDao.getPortfoliosByUserId(userId)
2. Room generates SQL: SELECT * FROM portfolios WHERE user_id = ?
3. Returns List<Portfolio>
```

### Step 4: ViewModel Updates State (Back to Presentation)
```
When repository returns data:
1. If empty: _uiState = Empty
2. If has data: _uiState = Success(portfolios)
3. Clear error: _errorMessage = null
4. Stop loading: _isLoading = false
```

### Step 5: UI Automatically Updates
```
UI was collecting from uiState Flow
When _uiState changes, UI automatically re-composes
Shows portfolio list or empty state
Loading spinner disappears
```

---

## 📦 Folder Structure

```
app/src/main/
├── java/com/example/portfoliowatcher/
│   ├── MainActivity.kt (documented ✅)
│   ├── domain/
│   │   ├── model/
│   │   │   ├── User.kt (documented ✅)
│   │   │   ├── Portfolio.kt (documented ✅)
│   │   │   └── Holding.kt (documented ✅)
│   │   └── repository/
│   │       ├── PortfolioRepository.kt (documented ✅)
│   │       └── UserRepository.kt (documented ✅)
│   ├── data/
│   │   ├── local/
│   │   │   ├── entity/
│   │   │   │   ├── UserEntity.kt (documented ✅)
│   │   │   │   ├── PortfolioEntity.kt
│   │   │   │   └── HoldingEntity.kt
│   │   │   ├── dao/
│   │   │   │   ├── UserDao.kt (documented ✅)
│   │   │   │   ├── PortfolioDao.kt
│   │   │   │   └── HoldingDao.kt
│   │   │   ├── AppDatabase.kt (documented ✅)
│   │   │   └── LocalDateTimeConverter.kt (documented ✅)
│   │   └── repository/ (implementations of domain interfaces)
│   └── presentation/
│       ├── viewmodel/
│       │   └── PortfolioViewModel.kt (documented ✅)
│       ├── ui/
│       │   ├── HomeScreen.kt
│       │   ├── UploadScreen.kt
│       │   ├── PortfolioDetailScreen.kt
│       │   └── SettingsScreen.kt
│       ├── navigation/
│       │   └── AppNavigation.kt
│       └── theme/
│           ├── Color.kt
│           ├── Type.kt
│           └── AppTheme.kt
└── AndroidManifest.xml
```

---

## 🔑 Key Concepts Explained

### **Suspend Functions (suspend keyword)**
```kotlin
suspend fun getPortfolio(portfolioId: String): Portfolio?
```
- Can be paused and resumed
- Don't block threads
- Used for long-running IO (database, network)
- Called from coroutines only
- Enable non-blocking UI operations

### **StateFlow**
```kotlin
private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
val uiState: StateFlow<UiState> = _uiState.asStateFlow()
```
- Emits state updates as a stream
- UI subscribes and automatically re-renders
- Thread-safe, hot flow (always has value)
- Part of Kotlin coroutines library

### **ViewModel Scope**
```kotlin
viewModelScope.launch { ... }
```
- Coroutine scope tied to ViewModel lifecycle
- Automatically cancelled when ViewModel destroyed
- Prevents memory leaks from orphaned coroutines

### **Room Database**
- Object-Relational Mapping (ORM) for SQLite
- Compile-time SQL verification (catches errors early)
- Automatic SQL generation from functions
- Type-safe database access
- Automatic schema migrations

---

## 📚 Learning Path

### To understand this architecture, read files in this order:

1. **Start with Models** (Domain Layer)
   - `User.kt` - Simple data model
   - `Portfolio.kt` - Model with calculations
   - `Holding.kt` - Model with relationships

2. **Learn Repositories** (Domain Layer)
   - `PortfolioRepository.kt` - What operations are available
   - `UserRepository.kt` - Another interface example

3. **Understand Database** (Data Layer)
   - `UserEntity.kt` - How tables are structured
   - `UserDao.kt` - How to query database
   - `AppDatabase.kt` - How everything connects
   - `LocalDateTimeConverter.kt` - Advanced type handling

4. **See it in Action** (Presentation Layer)
   - `PortfolioViewModel.kt` - How business logic works
   - `MainActivity.kt` - How UI is built

---

## ✅ All Documented Files

| File | Layer | Status | Comments |
|------|-------|--------|----------|
| User.kt | Domain | ✅ Complete | Data model with explanations |
| Portfolio.kt | Domain | ✅ Complete | Model with calculated properties |
| Holding.kt | Domain | ✅ Complete | Individual holding model |
| PortfolioRepository.kt | Domain | ✅ Complete | Repository interface with all operations |
| UserRepository.kt | Domain | ✅ Complete | User data access interface |
| UserEntity.kt | Data | ✅ Complete | Database table structure |
| UserDao.kt | Data | ✅ Complete | 11 different database operations documented |
| AppDatabase.kt | Data | ✅ Complete | Database configuration & singleton pattern |
| LocalDateTimeConverter.kt | Data | ✅ Complete | Type conversion logic |
| PortfolioViewModel.kt | Presentation | ✅ Complete | UI state management & business logic |
| MainActivity.kt | Presentation | ✅ Complete | App entry point |

---

## 🚀 Next Steps for Development

1. **Create remaining entity classes:**
   - PortfolioEntity.kt (similar to UserEntity)
   - HoldingEntity.kt (similar to UserEntity)

2. **Create remaining DAOs:**
   - PortfolioDao.kt (similar to UserDao)
   - HoldingDao.kt (similar to UserDao)

3. **Implement repositories:**
   - Create data/repository/PortfolioRepositoryImpl.kt
   - Create data/repository/UserRepositoryImpl.kt
   - These implement domain interfaces using DAOs

4. **Create UI screens:**
   - HomeScreen.kt - Portfolio list
   - UploadScreen.kt - File upload UI
   - PortfolioDetailScreen.kt - Portfolio details
   - SettingsScreen.kt - User settings

5. **Setup Navigation:**
   - AppNavigation.kt - Route definitions
   - Connect screens with NavHost

---

## 💡 Key Takeaways

1. **Three layers = Separation of concerns**
   - Each layer has clear responsibility
   - Changes in one layer don't affect others
   - Easy to test and maintain

2. **Interfaces = Flexibility**
   - Domain uses repository interfaces
   - Implementation can change (Room → Firestore)
   - Testable with mock implementations

3. **StateFlow = Reactive UI**
   - UI automatically updates when state changes
   - No manual refresh needed
   - Efficient change detection

4. **Documentation = Learning**
   - Every file explains WHY, not just WHAT
   - Comments help future developers (including you!)
   - Understanding architecture is more important than memorizing code

---

**Start reading the code now! Pick any file from the "Documented Files" table and dive in!**
