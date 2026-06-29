/**
 * Portfolio.kt - Domain Model for User's Investment Portfolio
 *
 * This file represents a complete portfolio - a collection of mutual fund holdings owned by a user.
 * A user can have multiple portfolios (e.g., "Retirement Portfolio", "Emergency Fund", "Growth Fund").
 *
 * The Portfolio aggregates multiple Holdings and calculates overall statistics.
 */

package com.example.portfoliowatcher.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Portfolio - Represents a collection of fund holdings
 *
 * A Portfolio is a named collection of mutual fund investments.
 * It aggregates the holdings and provides portfolio-level calculations.
 *
 * @property portfolioId - Unique identifier for this portfolio
 * @property userId - Foreign key linking to the owner user
 * @property name - Display name for the portfolio (e.g., "Retirement Fund")
 * @property holdings - List of all individual holdings in this portfolio
 * @property totalValue - Total current rupee value of all holdings
 * @property createdAt - When this portfolio was created/registered
 */
data class Portfolio(
    // Unique identifier for this specific portfolio
    val portfolioId: String,

    // Foreign key to the user who owns this portfolio
    val userId: String,

    // User-friendly name for the portfolio (helps when user has multiple portfolios)
    val name: String,

    // List of all holdings (mutual funds) in this portfolio
    val holdings: List<Holding> = emptyList(),

    // Snapshot of total portfolio value at time of data fetch
    val totalValue: BigDecimal,

    // Timestamp when portfolio was created
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    /**
     * Calculated property - Total rupees invested across all holdings
     * Formula: Sum of investmentAmount from all holdings
     * Example: If invested ₹10,000 in Fund A and ₹5,000 in Fund B = ₹15,000 total investment
     */
    val totalInvestment: BigDecimal
        get() = holdings.fold(BigDecimal.ZERO) { sum, holding ->
            sum + holding.investmentAmount
        }

    /**
     * Calculated property - Total profit/loss across all holdings
     * Formula: Sum of gainLoss from all holdings
     * Positive = overall profit, Negative = overall loss
     * Example: Fund A gained ₹1,000, Fund B lost ₹200 = ₹800 net profit
     */
    val totalGainLoss: BigDecimal
        get() = holdings.fold(BigDecimal.ZERO) { sum, holding ->
            sum + holding.gainLoss
        }

    /**
     * Calculated property - Overall portfolio return percentage
     * Formula: (totalGainLoss / totalInvestment) × 100
     * Example: Invested ₹10,000, gained ₹1,000 = 10% return
     * Returns 0 if totalInvestment is zero (empty portfolio)
     */
    val totalGainLossPercentage: BigDecimal
        get() = if (totalInvestment > BigDecimal.ZERO) {
            (totalGainLoss / totalInvestment) * BigDecimal("100")
        } else {
            BigDecimal.ZERO
        }

    /**
     * Calculated property - Current total value of all holdings
     * Formula: Sum of currentValue from all holdings (units × NAV for each)
     * This is the "today's market value" if you were to sell everything
     */
    val currentTotalValue: BigDecimal
        get() = holdings.fold(BigDecimal.ZERO) { sum, holding ->
            sum + holding.currentValue
        }

    /**
     * Calculated property - Number of different fund schemes in portfolio
     * Helps user understand diversification level
     */
    val holdingCount: Int
        get() = holdings.size
}
