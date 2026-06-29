package com.example.portfoliowatcher.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity(
    tableName = "holdings",
    indices = [Index(value = ["portfolioId"]), Index(value = ["isin"])],
    foreignKeys = [
        ForeignKey(
            entity = PortfolioEntity::class,
            parentColumns = ["portfolioId"],
            childColumns = ["portfolioId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class HoldingEntity(
    @PrimaryKey
    val holdingId: String,
    val portfolioId: String,
    val isin: String,
    val fundName: String,
    val units: BigDecimal,
    val currentNAV: BigDecimal,
    val investmentAmount: BigDecimal,
    val gainLoss: BigDecimal,
    val lastUpdated: LocalDateTime = LocalDateTime.now()
)
