/**
 * UserTest.kt - Unit tests for User domain model
 *
 * Tests verify:
 * - User creation with required fields
 * - Optional fields (phone) handling
 * - Data class properties (equals, hashCode, copy)
 * - Default timestamp creation
 */

package com.example.portfoliowatcher.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDateTime

/**
 * UserTest - Test suite for User data model
 *
 * Tests the User data class to ensure:
 * - Proper instantiation with required fields
 * - Optional fields work correctly
 * - Data class functionality (copy, equals)
 * - Timestamp defaults work
 */
class UserTest {

    /**
     * Test 1: Create user with required fields only
     *
     * Verifies that a User can be created with just userId and email.
     * Phone should default to null, and createdAt should have a value.
     */
    @Test
    fun testCreateUserWithRequiredFields() {
        // Arrange - Create a user with minimal required fields
        val userId = "user_123"
        val email = "user@example.com"

        // Act - Create the user
        val user = User(
            userId = userId,
            email = email
        )

        // Assert - Verify all fields are set correctly
        assertEquals(userId, user.userId)
        assertEquals(email, user.email)
        assertNull(user.phone)  // Phone should be null by default
        // Note: Can't test exact createdAt value due to timing, but verify it exists
        assertEquals(LocalDateTime::class, user.createdAt::class)
    }

    /**
     * Test 2: Create user with all fields
     *
     * Verifies that a User can be created with all fields including optional phone.
     */
    @Test
    fun testCreateUserWithAllFields() {
        // Arrange
        val userId = "user_456"
        val email = "test@example.com"
        val phone = "+91-9876543210"

        // Act
        val user = User(
            userId = userId,
            email = email,
            phone = phone
        )

        // Assert
        assertEquals(userId, user.userId)
        assertEquals(email, user.email)
        assertEquals(phone, user.phone)
    }

    /**
     * Test 3: Test data class equality
     *
     * Verifies that two User objects with same values are equal.
     * This tests Kotlin's auto-generated equals() method.
     */
    @Test
    fun testUserEquality() {
        // Arrange - Create two users with same data
        val user1 = User(
            userId = "123",
            email = "same@example.com",
            phone = "+91-1234567890"
        )
        val user2 = User(
            userId = "123",
            email = "same@example.com",
            phone = "+91-1234567890"
        )

        // Assert - They should be equal
        assertEquals(user1, user2)
    }

    /**
     * Test 4: Test data class inequality
     *
     * Verifies that two User objects with different values are not equal.
     */
    @Test
    fun testUserInequality() {
        // Arrange
        val user1 = User(userId = "123", email = "user1@example.com")
        val user2 = User(userId = "123", email = "user2@example.com")

        // Assert - Different emails means they're not equal
        assertNotEquals(user1, user2)
    }

    /**
     * Test 5: Test copy() method
     *
     * Verifies that the auto-generated copy() method creates a new User
     * with selected fields changed.
     */
    @Test
    fun testUserCopy() {
        // Arrange
        val originalUser = User(
            userId = "123",
            email = "original@example.com",
            phone = "+91-9999999999"
        )

        // Act - Copy user and change email only
        val copiedUser = originalUser.copy(email = "new@example.com")

        // Assert
        assertEquals("123", copiedUser.userId)  // Unchanged
        assertEquals("new@example.com", copiedUser.email)  // Changed
        assertEquals("+91-9999999999", copiedUser.phone)  // Unchanged
        // Original should be unmodified
        assertEquals("original@example.com", originalUser.email)
    }

    /**
     * Test 6: Test hash code consistency
     *
     * Verifies that equal objects have the same hash code.
     * This is important for using User in HashSet/HashMap.
     */
    @Test
    fun testUserHashCode() {
        // Arrange
        val user1 = User(userId = "123", email = "test@example.com")
        val user2 = User(userId = "123", email = "test@example.com")

        // Assert - Equal objects must have same hash code
        assertEquals(user1.hashCode(), user2.hashCode())
    }

    /**
     * Test 7: Test toString()
     *
     * Verifies that toString() generates a readable string representation.
     */
    @Test
    fun testUserToString() {
        // Arrange
        val user = User(userId = "123", email = "test@example.com")

        // Act
        val userString = user.toString()

        // Assert - Should contain class name and field values
        assert(userString.contains("User"))
        assert(userString.contains("123"))
        assert(userString.contains("test@example.com"))
    }

    /**
     * Test 8: Test nullable phone field
     *
     * Verifies that phone can be null without errors.
     */
    @Test
    fun testUserWithNullPhone() {
        // Act
        val user = User(
            userId = "123",
            email = "test@example.com",
            phone = null
        )

        // Assert
        assertNull(user.phone)
    }
}
