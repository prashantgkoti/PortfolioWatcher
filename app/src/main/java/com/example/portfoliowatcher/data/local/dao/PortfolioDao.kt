package com.example.portfoliowatcher.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.portfoliowatcher.data.local.entity.PortfolioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PortfolioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPortfolio(portfolio: PortfolioEntity)

    @Update
    suspend fun updatePortfolio(portfolio: PortfolioEntity)

    @Delete
    suspend fun deletePortfolio(portfolio: PortfolioEntity)

    @Query("SELECT * FROM portfolios WHERE portfolioId = :portfolioId LIMIT 1")
    suspend fun getPortfolioById(portfolioId: String): PortfolioEntity?

    @Query("SELECT * FROM portfolios WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getPortfoliosByUserId(userId: String): List<PortfolioEntity>

    @Query("SELECT * FROM portfolios WHERE userId = :userId ORDER BY createdAt DESC")
    fun observePortfoliosByUserId(userId: String): Flow<List<PortfolioEntity>>

    @Query("SELECT * FROM portfolios ORDER BY createdAt DESC")
    fun observeAllPortfolios(): Flow<List<PortfolioEntity>>

    @Query("DELETE FROM portfolios WHERE portfolioId = :portfolioId")
    suspend fun deletePortfolioById(portfolioId: String)

    @Query("SELECT COUNT(*) FROM portfolios WHERE userId = :userId")
    suspend fun getPortfolioCountForUser(userId: String): Int
}
