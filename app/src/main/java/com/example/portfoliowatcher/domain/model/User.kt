/**
 * User.kt - Domain Model for User Entity
 *
 * This file defines the User data model which represents a user of the Portfolio Watcher application.
 * It is part of the Domain Layer and contains only business logic representation, not database-specific details.
 *
 * The User model is used throughout the application to represent user information in a UI-independent way.
 * It serves as the contract between the presentation layer and data layer.
 */

package com.example.portfoliowatcher.domain.model

import java.time.LocalDateTime

/**
 * User - Data model representing a user in the Portfolio Watcher application
 *
 * This is a data class (immutable by default) that holds information about a user.
 * Data classes in Kotlin automatically provide equals(), hashCode(), toString(), and copy() methods.
 *
 * @property userId - Unique identifier for the user (e.g., Firebase UID or UUID)
 * @property email - User's email address, used for authentication and notifications
 * @property phone - User's phone number (optional), stored as nullable string for users who don't provide it
 * @property createdAt - Timestamp when the user account was created, defaults to current time
 */
data class User(
    // Unique identifier - typically from Firebase Authentication or local UUID generation
    val userId: String,

    // Email address for user identification and communication
    val email: String,

    // Optional phone number - nullable because not all users may provide it
    val phone: String? = null,

    // Timestamp of account creation - defaults to current date/time when User is instantiated
    val createdAt: LocalDateTime = LocalDateTime.now()
)
