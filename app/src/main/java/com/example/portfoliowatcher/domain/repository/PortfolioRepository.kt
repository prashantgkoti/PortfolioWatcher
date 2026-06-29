/**
 * PortfolioRepository.kt - Repository Interface for Portfolio Data Access
 *
 * This file defines the contract for accessing portfolio data.
 * Repository pattern provides a single point of access for data - whether from database, API, or cache.
 *
 * This is an interface (contract) - the actual implementation is in the data layer.
 * This separation allows the domain/presentation layers to NOT depend on where data comes from.
 */

package com.example.portfoliowatcher.domain.repository

import com.example.portfoliowatcher.domain.model.Portfolio
import com.example.portfoliowatcher.domain.model.Holding
import kotlinx.coroutines.flow.Flow

/**
 * PortfolioRepository - Interface defining all portfolio data access operations
 *
 * This interface acts as a contract that any implementation (Room DB, API, Cache) must follow.
 * All functions are suspend functions (coroutine-compatible) for non-blocking operations.
 *
 * The repository handles:
 * - CRUD operations (Create, Read, Update, Delete)
 * - Querying portfolios by various criteria
 * - Reactive data streams (Flow for real-time updates)
 */
interface PortfolioRepository {

    /**
     * Retrieve a single portfolio by its ID
     *
     * @param portfolioId - Unique ID of the portfolio to fetch
     * @return Portfolio object if found, null if not found
     *
     * suspend - this function runs on a coroutine and doesn't block main thread
     */
    suspend fun getPortfolio(portfolioId: String): Portfolio?

    /**
     * Retrieve all portfolios owned by a specific user
     *
     * @param userId - Unique ID of the user whose portfolios to fetch
     * @return List of all portfolios owned by the user (empty list if none)
     *
     * suspend - non-blocking IO operation
     */
    suspend fun getPortfoliosByUserId(userId: String): List<Portfolio>

    /**
     * Retrieve all portfolios as a reactive stream
     *
     * @param userId - User whose portfolios to observe
     * @return Flow<List<Portfolio>> - stream that emits updated portfolio list when data changes
     *
     * Flow allows UI to automatically update when portfolio data changes in database
     */
    fun observePortfoliosByUserId(userId: String): Flow<List<Portfolio>>

    /**
     * Save a new portfolio to the database
     *
     * @param portfolio - Portfolio object to save
     * @return The ID of the saved portfolio
     *
     * Use this when creating a new portfolio (insert operation)
     */
    suspend fun savePortfolio(portfolio: Portfolio): String

    /**
     * Update an existing portfolio
     *
     * @param portfolio - Portfolio object with updated values
     *
     * Use this when portfolio data changes (like name, holdings changed, etc.)
     */
    suspend fun updatePortfolio(portfolio: Portfolio)

    /**
     * Delete a portfolio and all its holdings
     *
     * @param portfolioId - ID of portfolio to delete
     *
     * Warning: This is irreversible - deletes portfolio and all holdings
     */
    suspend fun deletePortfolio(portfolioId: String)

    /**
     * Update the NAV (Net Asset Value) for all holdings in a portfolio
     *
     * @param portfolioId - Portfolio to update
     * @param navUpdates - Map of ISIN to new NAV price
     *
     * Used when fetching latest market prices from MFapi.in
     * Example: {"INF001K01AB4" to BigDecimal("150.50")}
     */
    suspend fun updateNAV(portfolioId: String, navUpdates: Map<String, java.math.BigDecimal>)

    /**
     * Add a new holding to an existing portfolio
     *
     * @param portfolioId - Portfolio to add holding to
     * @param holding - Holding object to add
     *
     * Used when user uploads new portfolio file with additional funds
     */
    suspend fun addHolding(portfolioId: String, holding: Holding)

    /**
     * Remove a holding from a portfolio
     *
     * @param portfolioId - Portfolio containing the holding
     * @param holdingId - ID of holding to remove
     *
     * Used when user manually removes a fund from portfolio
     */
    suspend fun removeHolding(portfolioId: String, holdingId: String)

    /**
     * Update an existing holding's details
     *
     * @param portfolioId - Portfolio containing the holding
     * @param holding - Updated holding data
     *
     * Used when holding details change (more units purchased, NAV updated, etc.)
     */
    suspend fun updateHolding(portfolioId: String, holding: Holding)
}
