/**
 * UserRepository.kt - Repository Interface for User Data Access
 *
 * This file defines the contract for accessing user information.
 * It provides a layer of abstraction so the app doesn't need to know where user data is stored.
 *
 * Examples of implementation:
 * - Local implementation: stores in Room database
 * - Remote implementation: fetches from Firebase Firestore
 * - Hybrid implementation: cache locally, sync with remote
 */

package com.example.portfoliowatcher.domain.repository

import com.example.portfoliowatcher.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * UserRepository - Interface for all user-related data operations
 *
 * Provides methods to:
 * - Create new user accounts
 * - Retrieve user information
 * - Update user details
 * - Delete user accounts
 * - Observe user changes in real-time
 */
interface UserRepository {

    /**
     * Retrieve a user by their unique ID
     *
     * @param userId - The unique identifier of the user (typically Firebase UID)
     * @return User object if found, null if user doesn't exist
     *
     * suspend - runs as a coroutine, doesn't block main thread
     */
    suspend fun getUser(userId: String): User?

    /**
     * Retrieve a user by their email address
     *
     * @param email - The email to search for
     * @return User object if found, null if email not registered
     *
     * Useful for login/authentication flows where we know email but not userId
     */
    suspend fun getUserByEmail(email: String): User?

    /**
     * Save a new user to the database
     *
     * @param user - User object to save (new user registration)
     * @return The generated user ID (useful if system generates IDs)
     *
     * Called when new user account is created (from Firebase Auth)
     */
    suspend fun saveUser(user: User): String

    /**
     * Update an existing user's information
     *
     * @param user - User object with updated values
     *
     * Called when user changes profile info (email, phone, etc.)
     * suspend - ensures non-blocking database update
     */
    suspend fun updateUser(user: User)

    /**
     * Delete a user account from the system
     *
     * @param userId - ID of user to delete
     *
     * Warning: This is irreversible!
     * Should also cascade delete:
     * - User's portfolios
     * - User's holdings
     * - User's preferences
     * This is typically handled by database foreign key constraints
     */
    suspend fun deleteUser(userId: String)

    /**
     * Retrieve all users in the system
     *
     * @return List of all User objects
     *
     * Mainly for admin purposes or batch operations
     * Should not expose in production UI (privacy concern)
     */
    suspend fun getAllUsers(): List<User>

    /**
     * Observe a specific user's data changes
     *
     * @param userId - User to observe
     * @return Flow<User> - emits user whenever their data changes
     *
     * Allows UI to automatically update when user profile changes
     * Example: If user changes phone number in settings, UI refreshes automatically
     */
    fun observeUser(userId: String): Flow<User>

    /**
     * Check if a user exists in the system
     *
     * @param userId - User ID to check
     * @return true if user exists, false otherwise
     *
     * Useful for validation before operations
     */
    suspend fun userExists(userId: String): Boolean
}
