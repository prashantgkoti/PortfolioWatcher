/**
 * LocalDateTimeConverter.kt - Type Converter for LocalDateTime
 *
 * Room database can only store primitive types (String, Int, Long, Boolean, etc.)
 * To store complex types like LocalDateTime, we need TypeConverters to convert them.
 *
 * This converter transforms:
 * - LocalDateTime → String (for storage in database)
 * - String → LocalDateTime (when loading from database)
 */

package com.example.portfoliowatcher.data.local

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * LocalDateTimeConverter - Converts LocalDateTime to/from String for Room
 *
 * Room Database stores everything as primitives (String, Long, Int, etc.)
 * This class provides conversion methods for LocalDateTime objects
 *
 * ISO format (2024-06-29T14:30:45) is used because:
 * - Human-readable
 * - Sortable as strings (dates sort chronologically)
 * - Standard international format
 * - Can be compared directly in SQL queries
 */
class LocalDateTimeConverter {

    /**
     * Convert LocalDateTime to String for database storage
     *
     * @param dateTime - LocalDateTime object to convert
     * @return String representation in ISO format
     *
     * Example:
     * Input: LocalDateTime.of(2024, 6, 29, 14, 30, 45)
     * Output: "2024-06-29T14:30:45"
     *
     * @TypeConverter tells Room this is a converter method
     * Room will call this automatically when saving LocalDateTime to database
     */
    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? {
        // If dateTime is null, return null (nullable handling)
        return dateTime?.let {
            // Format using ISO_LOCAL_DATE_TIME which produces format: "2024-06-29T14:30:45"
            it.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        }
    }

    /**
     * Convert String back to LocalDateTime when loading from database
     *
     * @param dateTimeString - String representation from database
     * @return LocalDateTime object, or null if input is null
     *
     * Example:
     * Input: "2024-06-29T14:30:45"
     * Output: LocalDateTime.of(2024, 6, 29, 14, 30, 45)
     *
     * @TypeConverter tells Room this is a converter method
     * Room will call this automatically when loading LocalDateTime from database
     */
    @TypeConverter
    fun toLocalDateTime(dateTimeString: String?): LocalDateTime? {
        // If string is null, return null (nullable handling)
        return dateTimeString?.let {
            // Parse from ISO format string back to LocalDateTime
            LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        }
    }
}
