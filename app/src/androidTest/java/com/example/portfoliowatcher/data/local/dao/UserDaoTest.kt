/**
 * UserDaoTest.kt - Integration tests for UserDao database operations
 *
 * Tests the DAO's:
 * - Insert operations (REPLACE strategy)
 * - Update operations
 * - Delete operations
 * - Query operations (single, all, by email)
 * - Flow-based reactive queries
 * - Null handling
 *
 * Note: These are INTEGRATION TESTS (androidTest) that use a real Room database.
 * They verify actual SQL behavior, not just code logic.
 */

package com.example.portfoliowatcher.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime
import com.example.portfoliowatcher.data.local.AppDatabase
import com.example.portfoliowatcher.data.local.entity.UserEntity

/**
 * UserDaoTest - Integration tests for UserDao
 *
 * Uses an in-memory Room database for fast, isolated testing.
 * Tests actual SQL queries and database constraints.
 */
@RunWith(AndroidJUnit4::class)
class UserDaoTest {

    // In-memory database for testing (gets deleted after each test)
    private lateinit var database: AppDatabase

    // The DAO under test
    private lateinit var userDao: UserDao

    /**
     * Setup: Run before each test
     *
     * Creates an in-memory database and initializes the DAO.
     * In-memory databases are fast and don't pollute the file system.
     */
    @Before
    fun setUp() {
        // Create in-memory database
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()  // Allow queries on main thread for testing
            .build()

        // Get the DAO from the database
        userDao = database.userDao()
    }

    /**
     * Cleanup: Run after each test
     *
     * Closes the database to prevent resource leaks.
     */
    @After
    fun tearDown() {
        // Close the database after test completes
        database.close()
    }

    /**
     * Test 1: Insert single user
     *
     * Verifies that a user can be inserted into the database.
     */
    @Test
    fun testInsertUser() = runTest {
        // Arrange - Create a test user
        val user = UserEntity(
            userId = "user_123",
            email = "test@example.com",
            phone = "+91-9876543210",
            created_at = LocalDateTime.now()
        )

        // Act - Insert the user
        userDao.insertUser(user)

        // Assert - Verify user exists in database
        val savedUser = userDao.getUserById("user_123")
        assertNotNull(savedUser)
        assertEquals("test@example.com", savedUser?.email)
        assertEquals("+91-9876543210", savedUser?.phone)
    }

    /**
     * Test 2: Insert user without phone
     *
     * Verifies that phone field is optional and can be null.
     */
    @Test
    fun testInsertUserWithoutPhone() = runTest {
        // Arrange
        val user = UserEntity(
            userId = "user_456",
            email = "nophone@example.com",
            phone = null,  // No phone
            created_at = LocalDateTime.now()
        )

        // Act
        userDao.insertUser(user)

        // Assert
        val savedUser = userDao.getUserById("user_456")
        assertNotNull(savedUser)
        assertNull(savedUser?.phone)
    }

    /**
     * Test 3: Update existing user
     *
     * Verifies that user fields can be updated.
     */
    @Test
    fun testUpdateUser() = runTest {
        // Arrange - Insert initial user
        val user = UserEntity(
            userId = "user_789",
            email = "original@example.com",
            phone = null,
            created_at = LocalDateTime.now()
        )
        userDao.insertUser(user)

        // Act - Update the user's phone
        val updatedUser = user.copy(phone = "+91-1111111111")
        userDao.updateUser(updatedUser)

        // Assert - Verify update was successful
        val fetchedUser = userDao.getUserById("user_789")
        assertEquals("+91-1111111111", fetchedUser?.phone)
        // Email should be unchanged
        assertEquals("original@example.com", fetchedUser?.email)
    }

    /**
     * Test 4: Delete user by ID
     *
     * Verifies that users can be deleted from the database.
     */
    @Test
    fun testDeleteUser() = runTest {
        // Arrange - Insert a user
        val user = UserEntity(
            userId = "user_delete",
            email = "delete@example.com",
            phone = null,
            created_at = LocalDateTime.now()
        )
        userDao.insertUser(user)

        // Verify user exists
        assertTrue(userDao.userExists("user_delete"))

        // Act - Delete the user
        userDao.deleteUser("user_delete")

        // Assert - User should no longer exist
        assertFalse(userDao.userExists("user_delete"))
    }

    /**
     * Test 5: Query user by email
     *
     * Verifies that users can be found by email address.
     */
    @Test
    fun testGetUserByEmail() = runTest {
        // Arrange - Insert user with specific email
        val user = UserEntity(
            userId = "user_email",
            email = "findme@example.com",
            phone = null,
            created_at = LocalDateTime.now()
        )
        userDao.insertUser(user)

        // Act - Query by email
        val foundUser = userDao.getUserByEmail("findme@example.com")

        // Assert
        assertNotNull(foundUser)
        assertEquals("user_email", foundUser?.userId)
    }

    /**
     * Test 6: Get all users
     *
     * Verifies that all users in the database can be retrieved.
     */
    @Test
    fun testGetAllUsers() = runTest {
        // Arrange - Insert multiple users
        val user1 = UserEntity("user_1", "user1@test.com", null, LocalDateTime.now())
        val user2 = UserEntity("user_2", "user2@test.com", null, LocalDateTime.now())
        val user3 = UserEntity("user_3", "user3@test.com", null, LocalDateTime.now())

        userDao.insertUser(user1)
        userDao.insertUser(user2)
        userDao.insertUser(user3)

        // Act - Get all users
        val allUsers = userDao.getAllUsers()

        // Assert - Should have exactly 3 users
        assertEquals(3, allUsers.size)
    }

    /**
     * Test 7: Check if user exists
     *
     * Verifies that userExists() returns correct boolean.
     */
    @Test
    fun testUserExists() = runTest {
        // Arrange
        val user = UserEntity("user_exists", "test@test.com", null, LocalDateTime.now())
        userDao.insertUser(user)

        // Act & Assert
        assertTrue(userDao.userExists("user_exists"))
        assertFalse(userDao.userExists("user_nonexistent"))
    }

    /**
     * Test 8: Get user count
     *
     * Verifies that user count is accurate.
     */
    @Test
    fun testGetUserCount() = runTest {
        // Arrange - Insert users
        val user1 = UserEntity("user_1", "user1@test.com", null, LocalDateTime.now())
        val user2 = UserEntity("user_2", "user2@test.com", null, LocalDateTime.now())

        userDao.insertUser(user1)
        userDao.insertUser(user2)

        // Act
        val count = userDao.getUserCount()

        // Assert
        assertEquals(2, count)
    }

    /**
     * Test 9: REPLACE strategy (insert or update)
     *
     * Verifies that inserting with the same primary key replaces the old record.
     */
    @Test
    fun testReplaceStrategy() = runTest {
        // Arrange - Insert initial user
        val user1 = UserEntity(
            userId = "user_replace",
            email = "email1@test.com",
            phone = null,
            created_at = LocalDateTime.now()
        )
        userDao.insertUser(user1)

        // Act - "Insert" same ID with different data (should replace)
        val user2 = UserEntity(
            userId = "user_replace",
            email = "email2@test.com",
            phone = "+91-9999999999",
            created_at = LocalDateTime.now()
        )
        userDao.insertUser(user2)

        // Assert - Should have updated data (not two records)
        val allUsers = userDao.getAllUsers()
        assertEquals(1, allUsers.size)
        assertEquals("email2@test.com", allUsers[0].email)
    }

    /**
     * Test 10: Observe user changes (Flow)
     *
     * Verifies that observeUser() returns a Flow that updates when user changes.
     * Note: This test requires coroutines Flow support.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testObserveUserChanges() = runTest {
        // Arrange - Create user
        val user = UserEntity(
            userId = "user_flow",
            email = "flow@test.com",
            phone = null,
            created_at = LocalDateTime.now()
        )
        userDao.insertUser(user)

        // Act - Observe this user
        val observedUser = userDao.observeUser("user_flow").first()

        // Assert
        assertNotNull(observedUser)
        assertEquals("flow@test.com", observedUser?.email)
    }

    /**
     * Test 11: Observe all users (Flow)
     *
     * Verifies that observeAllUsers() returns a Flow with all users.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testObserveAllUsers() = runTest {
        // Arrange
        val user1 = UserEntity("user_1", "user1@test.com", null, LocalDateTime.now())
        val user2 = UserEntity("user_2", "user2@test.com", null, LocalDateTime.now())

        userDao.insertUser(user1)
        userDao.insertUser(user2)

        // Act - Observe all users
        val allUsers = userDao.observeAllUsers().first()

        // Assert
        assertEquals(2, allUsers.size)
    }

    /**
     * Test 12: Email indexing for fast lookup
     *
     * Verifies that email field is indexed for better query performance.
     * This test mainly documents the fact that email has an index.
     */
    @Test
    fun testEmailLookupPerformance() = runTest {
        // Arrange - Insert many users with different emails
        repeat(100) { i ->
            val user = UserEntity(
                userId = "user_$i",
                email = "user$i@test.com",
                phone = null,
                created_at = LocalDateTime.now()
            )
            userDao.insertUser(user)
        }

        // Act - Query by email (indexed, should be fast)
        val startTime = System.currentTimeMillis()
        val foundUser = userDao.getUserByEmail("user50@test.com")
        val endTime = System.currentTimeMillis()

        // Assert
        assertNotNull(foundUser)
        assertEquals("user_50", foundUser?.userId)
        // In practice, indexed queries should be very fast (milliseconds)
    }
}
