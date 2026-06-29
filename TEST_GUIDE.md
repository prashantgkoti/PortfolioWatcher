## 🧪 Portfolio Watcher - Complete Testing Guide

This document explains the test suite and how to run all tests.

---

## 📊 Test Coverage Summary

| Layer | Test File | Type | Tests | Focus |
|-------|-----------|------|-------|-------|
| **Domain** | `UserTest.kt` | Unit | 8 | User model data class functionality |
| **Domain** | `HoldingTest.kt` | Unit | 10 | Individual holding calculations |
| **Domain** | `PortfolioTest.kt` | Unit | 9 | Portfolio aggregation and math |
| **Presentation** | `PortfolioViewModelTest.kt` | Unit | 10 | State management and UI logic |
| **Data** | `UserDaoTest.kt` | Integration | 12 | Database operations |

**Total: 49 comprehensive tests across all layers**

---

## 🎯 Test Types Explained

### 1. **Unit Tests** (Fast, isolated)
Located in: `app/src/test/java/`

Test a single class/function without external dependencies. Use mocks for dependencies.

**Files:**
- `UserTest.kt` - Tests User data model
- `HoldingTest.kt` - Tests Holding calculations
- `PortfolioTest.kt` - Tests Portfolio aggregation
- `PortfolioViewModelTest.kt` - Tests ViewModel state

**Run with:** `./gradlew test`

### 2. **Integration Tests** (Slower, realistic)
Located in: `app/src/androidTest/java/`

Test multiple components together using real instances (like Room database).

**Files:**
- `UserDaoTest.kt` - Tests actual database operations with Room

**Run with:** `./gradlew connectedAndroidTest` (requires emulator/device)

---

## 📂 Test File Organization

```
app/src/
├── test/java/com/example/portfoliowatcher/    (Unit tests)
│   ├── domain/model/
│   │   ├── UserTest.kt ✅
│   │   ├── HoldingTest.kt ✅
│   │   └── PortfolioTest.kt ✅
│   └── presentation/viewmodel/
│       └── PortfolioViewModelTest.kt ✅
│
└── androidTest/java/com/example/portfoliowatcher/  (Integration tests)
    └── data/local/dao/
        └── UserDaoTest.kt ✅
```

---

## ✅ Test Details by File

### 1. **UserTest.kt** - User Model Tests (8 tests)

**What it tests:** The User data class

**Tests:**
1. ✅ `testCreateUserWithRequiredFields()` - Create user with just userId/email
2. ✅ `testCreateUserWithAllFields()` - Create user with phone number
3. ✅ `testUserEquality()` - Two users with same values are equal
4. ✅ `testUserInequality()` - Users with different emails are not equal
5. ✅ `testUserCopy()` - copy() method works correctly
6. ✅ `testUserHashCode()` - Hash codes are consistent
7. ✅ `testUserToString()` - toString() generates readable output
8. ✅ `testUserWithNullPhone()` - Nullable phone field works

**Concepts tested:**
- Kotlin data classes
- Nullable types
- copy() method
- equals() and hashCode()

**Run:** `./gradlew test -k UserTest`

---

### 2. **HoldingTest.kt** - Holding Model Tests (10 tests)

**What it tests:** Individual fund holding calculations

**Tests:**
1. ✅ `testCreateHolding()` - Create a holding with all fields
2. ✅ `testCalculateCurrentValue()` - currentValue = units × NAV
3. ✅ `testCalculateGainLossPercentage()` - Percentage calculations
4. ✅ `testFractionalUnitsValue()` - Decimal units calculations
5. ✅ `testHoldingWithLoss()` - Negative gains (losses)
6. ✅ `testHoldingWithZeroGainLoss()` - Break-even (0%) return
7. ✅ `testHoldingWithZeroInvestment()` - Divide-by-zero edge case
8. ✅ `testHoldingCopy()` - copy() method for holdings
9. ✅ `testSmallPercentageReturn()` - BigDecimal precision
10. ✅ `testLargePercentageReturn()` - Large numbers (200%+ returns)

**Concepts tested:**
- BigDecimal for financial calculations
- Calculated properties
- Edge cases and error handling
- Data class copy() method

**Why BigDecimal?** For financial calculations, we need exact precision. 
Floating-point doubles can have rounding errors (0.1 + 0.2 ≠ 0.3).

**Run:** `./gradlew test -k HoldingTest`

---

### 3. **PortfolioTest.kt** - Portfolio Aggregation Tests (9 tests)

**What it tests:** Portfolio (collection of holdings) calculations

**Tests:**
1. ✅ `testCreateEmptyPortfolio()` - Empty portfolio with no holdings
2. ✅ `testPortfolioWithSingleHolding()` - Single holding aggregation
3. ✅ `testPortfolioGainLossPercentage()` - Portfolio-level percentages
4. ✅ `testPortfolioWithMultipleHoldings()` - Aggregating across 2+ funds
5. ✅ `testEmptyPortfolioPercentage()` - Zero investment (no divide-by-zero)
6. ✅ `testPortfolioWithLoss()` - Portfolio with negative gains
7. ✅ `testPortfolioHoldingCount()` - Accurate holding count
8. ✅ `testPortfolioCopy()` - copy() with modified fields
9. ✅ `testLargePortfolioValues()` - Large amounts (₹1 crore)

**Concepts tested:**
- Aggregation using fold()
- Edge case handling (empty, zero investment)
- Calculated properties across collections

**Business logic verified:**
- Portfolio total = sum of all holdings
- Portfolio gain/loss = sum of individual gains/losses
- Percentage = (totalGainLoss / totalInvestment) × 100

**Run:** `./gradlew test -k PortfolioTest`

---

### 4. **PortfolioViewModelTest.kt** - State Management Tests (10 tests)

**What it tests:** ViewModel UI state and repository integration

**Tests:**
1. ✅ `testInitialStateIsLoading()` - Starts in Loading state
2. ✅ `testLoadPortfoliosSuccess()` - Loading → Success transition
3. ✅ `testLoadPortfoliosEmpty()` - Loading → Empty transition
4. ✅ `testLoadPortfoliosError()` - Loading → Error transition
5. ✅ `testSelectPortfolio()` - Select a portfolio
6. ✅ `testClearSelectedPortfolio()` - Clear selection (back to null)
7. ✅ `testDismissError()` - Close error dialog
8. ✅ `testLoadMultiplePortfolios()` - Handle multiple portfolios
9. ✅ `testDeletePortfolio()` - Delete operation
10. ✅ `testCompleteStateTransition()` - Full state cycle

**Concepts tested:**
- Sealed classes for type-safe states
- StateFlow for reactive updates
- Coroutines with test dispatcher
- Mocking repository for isolation

**State machine:**
```
    ┌─────────────────────────────────┐
    │         Loading (Start)         │
    └──────────────┬──────────────────┘
                   │
        ┌──────────┴──────────┐
        │                     │
    ┌───▼────┐          ┌────▼────┐
    │ Success│          │  Error   │
    │(has    │          │(has msg) │
    │data)   │          └────┬─────┘
    └───┬────┘               │
        │         ┌──────────┘
        │         │
        └────┬────┴──────┐
             │           │
         ┌───▼──┐    ┌───▼───┐
         │Empty │    │Try Reload
         └──────┘    └────────┘
```

**Run:** `./gradlew test -k PortfolioViewModelTest`

---

### 5. **UserDaoTest.kt** - Database Integration Tests (12 tests)

**What it tests:** Room database operations for users

**Tests:**
1. ✅ `testInsertUser()` - Insert user into database
2. ✅ `testInsertUserWithoutPhone()` - Nullable phone handling
3. ✅ `testUpdateUser()` - Update user fields
4. ✅ `testDeleteUser()` - Delete user by ID
5. ✅ `testGetUserByEmail()` - Query by email address
6. ✅ `testGetAllUsers()` - Retrieve all users
7. ✅ `testUserExists()` - Check user existence
8. ✅ `testGetUserCount()` - Count users
9. ✅ `testReplaceStrategy()` - REPLACE on conflict strategy
10. ✅ `testObserveUserChanges()` - Flow-based reactive queries
11. ✅ `testObserveAllUsers()` - Observe all users Flow
12. ✅ `testEmailLookupPerformance()` - Index optimization

**Concepts tested:**
- Room @Entity and @Dao annotations
- CRUD operations (Create, Read, Update, Delete)
- Type converters (LocalDateTime)
- Flow for reactive queries
- Database indexes for performance

**Database operations:**
```
INSERT → Create new record
UPDATE → Modify existing record
DELETE → Remove record
SELECT → Query records
REPLACE → Insert or update if exists
```

**Run:** `./gradlew connectedAndroidTest` (requires emulator)

---

## 🚀 How to Run Tests

### Run All Tests
```bash
./gradlew test                          # Run all unit tests
./gradlew connectedAndroidTest          # Run all integration tests
./gradlew test connectedAndroidTest     # Run everything
```

### Run Specific Test File
```bash
./gradlew test -k UserTest              # Only UserTest
./gradlew test -k HoldingTest           # Only HoldingTest
./gradlew test -k PortfolioTest         # Only PortfolioTest
./gradlew test -k PortfolioViewModelTest # Only ViewModel tests
./gradlew connectedAndroidTest -k UserDaoTest  # Only DAO tests
```

### Run Specific Test Method
```bash
# Run just one test method
./gradlew test -k UserTest.testUserEquality
```

### View Test Results
Test results are generated in: `app/build/reports/tests/`

Open in browser:
```
# Windows
start app\build\reports\tests\test\index.html

# macOS
open app/build/reports/tests/test/index.html

# Linux
firefox app/build/reports/tests/test/index.html
```

---

## 📊 Testing Patterns Used

### 1. **Arrange-Act-Assert (AAA)**
```kotlin
@Test
fun testExample() {
    // Arrange - Set up test data
    val input = "test"
    
    // Act - Do the thing being tested
    val result = functionUnderTest(input)
    
    // Assert - Verify the result
    assertEquals("expected", result)
}
```

### 2. **Given-When-Then (BDD Style)**
```kotlin
@Test
fun testGivenUserWhenLoadingThenShowPortfolios() {
    // Given - User loaded
    // When - We load portfolios
    // Then - They should appear
}
```

### 3. **Mocking with Mockito**
```kotlin
@Mock
private lateinit var mockRepository: PortfolioRepository

@Before
fun setUp() {
    MockitoAnnotations.openMocks(this)
}

@Test
fun testWithMock() {
    // Setup mock behavior
    whenever(mockRepository.getPortfolios())
        .thenReturn(listOf(portfolio))
    
    // Test with mocked repository
}
```

### 4. **Coroutine Testing**
```kotlin
@Test
fun testAsync() = runTest(testDispatcher) {
    // runTest() manages coroutines for you
    viewModel.loadData()
    advanceUntilIdle()  // Wait for all coroutines to complete
    
    // Assert state
    assertEquals(UiState.Success::class, uiState::class)
}
```

---

## 🎓 What Each Test Teaches

### UserTest → Data Classes
- How Kotlin data classes work
- Equality, hashing, copying
- Optional/nullable fields

### HoldingTest → Calculations
- BigDecimal for money
- Calculated properties
- Edge case handling

### PortfolioTest → Aggregation
- fold() function
- Summing collections
- Percentage math

### PortfolioViewModelTest → State Management
- Sealed classes
- StateFlow
- Coroutines
- Mocking

### UserDaoTest → Database
- Room ORM
- CRUD operations
- Reactive Flows
- Integration testing

---

## ✅ Test Checklist

Before shipping code, ensure:

- [ ] All unit tests pass: `./gradlew test`
- [ ] All integration tests pass: `./gradlew connectedAndroidTest`
- [ ] Code coverage > 80% for business logic
- [ ] Tests cover happy path (success cases)
- [ ] Tests cover error cases (exceptions)
- [ ] Tests cover edge cases (null, empty, zero)
- [ ] No tests depend on external services
- [ ] Tests run in < 30 seconds
- [ ] Tests are readable and well-commented
- [ ] Test names clearly describe what's being tested

---

## 🔧 Common Testing Issues & Solutions

### Issue: "Test times out"
**Cause:** Infinite loop or waiting forever
**Solution:** Use `advanceUntilIdle()` in coroutine tests

### Issue: "Mock not working"
**Cause:** Forgot to call `MockitoAnnotations.openMocks(this)`
**Solution:** Add this in @Before method

### Issue: "Permission denied for database"
**Cause:** Emulator doesn't have storage permissions
**Solution:** Grant permissions: Settings → Apps → Permissions

### Issue: "Test only passes on my machine"
**Cause:** Test has hardcoded paths or time dependencies
**Solution:** Use absolute paths, mock time, use test fixtures

---

## 🚀 Next Testing Steps

1. **Create PortfolioEntity/DAO tests** (similar to UserDaoTest)
2. **Create Integration tests** for ViewModel + Repository
3. **Create Firebase tests** (mocking Firebase SDK)
4. **Create UI tests** with Compose testing library
5. **Add code coverage** with Jacoco
6. **Set up CI/CD** to run tests automatically

---

## 📚 Testing Resources

**Learn more about:**
- [JUnit 4 Documentation](https://junit.org/junit4/)
- [Mockito Kotlin](https://github.com/mockito/mockito-kotlin)
- [Coroutines Testing](https://kotlinlang.org/docs/coroutine-context-and-dispatchers.html)
- [Room Testing](https://developer.android.com/training/data-storage/room/testing-db)
- [Android Testing Codelab](https://developer.android.com/codelabs/android-testing)

---

## 🎉 You're All Set!

You now have:
- ✅ 49 comprehensive tests
- ✅ All 3 layers tested (domain, data, presentation)
- ✅ Both unit and integration tests
- ✅ Clear testing patterns and best practices
- ✅ Full documentation for each test

**Next:** Run `./gradlew test` to execute all tests!

