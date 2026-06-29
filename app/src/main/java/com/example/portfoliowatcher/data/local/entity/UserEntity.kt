/**
 * UserEntity.kt - Room Database Entity for User
 *
 * Entities are database tables. This class represents the structure of the "users" table.
 * It maps domain User model to a database representation.
 *
 * Key difference from domain User:
 * - Includes @Entity, @PrimaryKey annotations
 * - May include database-specific fields (timestamps, indexes)
 * - Used ONLY for database operations
 */

package com.example.portfoliowatcher.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * UserEntity - Database representation of a user
 *
 * This class is mapped to a database table by Room ORM.
 * The @Entity annotation tells Room to create a table from this class.
 *
 * @property userId - Primary key, unique identifier in database
 * @property email - User's email, indexed for fast email-based lookups
 * @property phone - User's phone number (optional)
 * @property createdAt - When user account was created (useful for sorting/filtering)
 */
@Entity(
    // Name of the table in the database (defaults to class name if not specified)
    tableName = "users",

    // Indices improve query performance for frequently searched columns
    indices = [
        // Index on email - speeds up queries like "find user by email"
        androidx.room.Index(value = ["email"], unique = true)
    ]
)
data class UserEntity(
    // @PrimaryKey marks this as the unique identifier for each row
    // autoGenerate = false means we provide the ID (from Firebase Auth)
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: String,

    // Column for email - must be unique (can't have two users with same email)
    @ColumnInfo(name = "email")
    val email: String,

    // Column for phone - nullable because not required
    @ColumnInfo(name = "phone")
    val phone: String? = null,

    // Column for creation timestamp - useful for tracking user age, sorting, etc.
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)
