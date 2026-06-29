/**
 * AppDatabase.kt - Room Database Configuration
 *
 * This is the main database class that ties together:
 * - Entity classes (database tables)
 * - DAO classes (database operations)
 * - Database configuration (name, version, migrations)
 *
 * Room is Google's recommended persistence library for Android.
 * It provides an abstraction layer over SQLite with compile-time SQL verification.
 */

package com.example.portfoliowatcher.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.portfoliowatcher.data.local.dao.UserDao
import com.example.portfoliowatcher.data.local.dao.PortfolioDao
import com.example.portfoliowatcher.data.local.dao.HoldingDao
import com.example.portfoliowatcher.data.local.entity.UserEntity
import com.example.portfoliowatcher.data.local.entity.PortfolioEntity
import com.example.portfoliowatcher.data.local.entity.HoldingEntity

/**
 * AppDatabase - Main database class for Portfolio Watcher
 *
 * @Database annotation defines:
 * - entities: List of all entity classes (database tables)
 * - version: Database schema version (increment when structure changes)
 * - exportSchema: Whether to export schema for version history (false for simplicity)
 *
 * TypeConverters: Helps Room convert complex types (like LocalDateTime) to/from database storage
 */
@Database(
    // List of all entity classes that will be created as tables
    entities = [
        UserEntity::class,           // Table: users
        PortfolioEntity::class,      // Table: portfolios
        HoldingEntity::class         // Table: holdings
    ],
    // Version 1 = initial schema
    // Increment this when you change table structure (add/remove columns, etc.)
    // Then create a Migration to handle the upgrade
    version = 1,
    // exportSchema = false means don't save JSON schema files
    // true would create .json files for version control (useful for production apps)
    exportSchema = false
)
// TypeConverters tell Room how to convert unsupported types
// LocalDateTimeConverter handles converting java.time.LocalDateTime to/from String
@TypeConverters(LocalDateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Provide access to UserDao for user database operations
     * Room generates the implementation automatically
     */
    abstract fun userDao(): UserDao

    /**
     * Provide access to PortfolioDao for portfolio operations
     */
    abstract fun portfolioDao(): PortfolioDao

    /**
     * Provide access to HoldingDao for holding operations
     */
    abstract fun holdingDao(): HoldingDao

    /**
     * Companion object - allows singleton pattern for database instance
     * Ensures only one database connection exists (prevents data corruption)
     */
    companion object {
        // Volatile ensures the value is always fetched from main memory
        // This is important in multi-threaded environments
        @Volatile
        private var instance: AppDatabase? = null

        /**
         * Get or create the database instance (singleton pattern)
         *
         * @param context - Android context needed to access app files
         * @return The AppDatabase instance
         *
         * Synchronized - ensures only one thread creates the database at a time
         * Prevents race conditions and duplicate connections
         */
        fun getInstance(context: Context): AppDatabase {
            // Return existing instance if available
            return instance ?: synchronized(this) {
                // Double-check pattern: check again after acquiring lock
                // in case another thread created it while we were waiting
                instance ?: createDatabase(context).also { instance = it }
            }
        }

        /**
         * Create a new database instance
         *
         * @param context - Android context for database file access
         * @return New AppDatabase instance
         *
         * Room.databaseBuilder:
         * - context - Android context
         * - AppDatabase::class.java - the database class
         * - "portfolio_watcher_db" - name of the SQLite file
         *
         * allowMainThreadQueries() - DANGEROUS! Only for development/testing
         * In production, queries should run on background threads via coroutines
         *
         * fallbackToDestructiveMigration():
         * - If schema version changes but no migration provided
         * - Deletes old database and creates new one
         * - WARNING: This loses all user data!
         * - Only OK for app that's still in development
         * - In production, always provide proper migrations
         */
        private fun createDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "portfolio_watcher_db"  // Name of the SQLite database file
            )
            // Allow database queries on main thread (only for development!)
            // Remove this in production - causes ANR (app not responding) on large queries
            .allowMainThreadQueries()
            // If schema version increments without migration, recreate database
            // This is OK for development but will lose user data
            .fallbackToDestructiveMigration()
            // Build and return the database instance
            .build()
        }
    }
}
