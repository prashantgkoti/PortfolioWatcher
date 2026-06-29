/**
 * UserDao.kt - Data Access Object for User Entity
 *
 * DAO (Data Access Object) provides methods to perform CRUD operations on the database.
 * Room generates the SQL automatically from these function signatures.
 *
 * All methods are suspend functions (coroutine-compatible) for non-blocking database access.
 * Room handles thread management automatically.
 */

package com.example.portfoliowatcher.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.portfoliowatcher.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * UserDao - Database Access Object for user operations
 *
 * @Dao annotation tells Room this is a DAO class
 * All methods interact with the "users" table in SQLite database
 */
@Dao
interface UserDao {

    /**
     * Insert a new user into the database
     *
     * @param user - UserEntity object to insert
     *
     * suspend - non-blocking insert operation
     * OnConflictStrategy.REPLACE - if userId exists, replace the entire row
     * This is useful for handling duplicate insertions
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    /**
     * Update an existing user
     *
     * @param user - UserEntity with updated fields
     *
     * Must match by primary key (userId)
     * Only updates fields that exist in the entity
     */
    @Update
    suspend fun updateUser(user: UserEntity)

    /**
     * Delete a user from the database
     *
     * @param user - UserEntity to delete (matches by userId)
     *
     * Warning: Room doesn't cascade deletes by default
     * You must manually delete related portfolios/holdings
     */
    @Delete
    suspend fun deleteUser(user: UserEntity)

    /**
     * Retrieve a user by their ID
     *
     * @param userId - Unique identifier to search for
     * @return UserEntity if found, null if not found
     *
     * Query syntax:
     * - SELECT * - get all columns
     * - FROM users - from the users table
     * - WHERE user_id = :userId - where ID matches parameter
     * - LIMIT 1 - return only one result (since ID is unique)
     */
    @Query("SELECT * FROM users WHERE user_id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    /**
     * Retrieve a user by their email address
     *
     * @param email - Email to search for
     * @return UserEntity if found, null if not found
     *
     * Email is indexed, so this query is fast
     * Useful for login flows
     */
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    /**
     * Get all users in the database (admin/testing purposes)
     *
     * @return List of all UserEntity objects
     *
     * Don't expose this to regular users (privacy concern)
     * Only use for admin functions or batch operations
     */
    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserEntity>

    /**
     * Get count of all users
     *
     * @return Number of users in database
     *
     * Useful for analytics or validation
     * Much faster than loading all users just to count them
     */
    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    /**
     * Observe a specific user's data - reactive stream
     *
     * @param userId - User to observe
     * @return Flow<UserEntity?> - emits user whenever database data changes
     *
     * Flow is a Kotlin coroutine stream
     * UI can collect from this Flow and automatically update when data changes
     * Returns null if user doesn't exist
     */
    @Query("SELECT * FROM users WHERE user_id = :userId")
    fun observeUser(userId: String): Flow<UserEntity?>

    /**
     * Observe all users - reactive stream
     *
     * @return Flow<List<UserEntity>> - emits entire user list whenever ANY user changes
     *
     * Useful for admin screens or user listings
     * Automatically updates UI when users are added/modified/deleted
     */
    @Query("SELECT * FROM users ORDER BY created_at DESC")
    fun observeAllUsers(): Flow<List<UserEntity>>

    /**
     * Check if a user exists by ID
     *
     * @param userId - ID to check
     * @return true if user exists, false otherwise
     *
     * Efficient way to check existence without loading full user object
     * SELECT 1 returns a single row if match found (then COUNT = 1, true)
     */
    @Query("SELECT COUNT(*) FROM users WHERE user_id = :userId")
    suspend fun userExists(userId: String): Int
}
