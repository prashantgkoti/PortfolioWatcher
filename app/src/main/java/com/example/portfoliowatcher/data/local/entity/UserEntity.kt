package com.example.portfoliowatcher.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey
    val userId: String,
    val email: String,
    val phone: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
