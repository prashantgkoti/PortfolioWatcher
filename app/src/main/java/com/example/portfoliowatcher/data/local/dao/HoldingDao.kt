package com.example.portfoliowatcher.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.portfoliowatcher.data.local.entity.HoldingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HoldingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHolding(holding: HoldingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHoldings(holdings: List<HoldingEntity>)

    @Update
    suspend fun updateHolding(holding: HoldingEntity)

    @Query("SELECT * FROM holdings WHERE holdingId = :holdingId LIMIT 1")
    suspend fun getHoldingById(holdingId: String): HoldingEntity?

    @Query("SELECT * FROM holdings WHERE portfolioId = :portfolioId ORDER BY fundName ASC")
    suspend fun getHoldingsByPortfolioId(portfolioId: String): List<HoldingEntity>

    @Query("SELECT * FROM holdings WHERE portfolioId = :portfolioId ORDER BY fundName ASC")
    fun observeHoldingsByPortfolioId(portfolioId: String): Flow<List<HoldingEntity>>

    @Query("DELETE FROM holdings WHERE holdingId = :holdingId")
    suspend fun deleteHoldingById(holdingId: String)

    @Query("DELETE FROM holdings WHERE portfolioId = :portfolioId")
    suspend fun deleteHoldingsByPortfolioId(portfolioId: String)

    @Query("UPDATE holdings SET currentNAV = :newNAV, lastUpdated = :updatedAt WHERE isin = :isin")
    suspend fun updateNAVByIsin(isin: String, newNAV: java.math.BigDecimal, updatedAt: java.time.LocalDateTime)

    @Query("SELECT * FROM holdings WHERE isin = :isin")
    suspend fun getHoldingsByIsin(isin: String): List<HoldingEntity>

    @Query("SELECT COUNT(*) FROM holdings WHERE portfolioId = :portfolioId")
    suspend fun getHoldingCountForPortfolio(portfolioId: String): Int
}
