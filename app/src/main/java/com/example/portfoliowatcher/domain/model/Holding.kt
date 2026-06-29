/**
 * Holding.kt - Domain Model for Individual Fund Holding
 *
 * This file represents a single mutual fund holding within a portfolio.
 * A Holding is a specific instance of an investor owning shares/units of a mutual fund.
 *
 * Example: If a user owns 100 units of HDFC Bank Fund, that's one Holding.
 */

package com.example.portfoliowatcher.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Holding - Represents a single mutual fund holding
 *
 * This data class models one fund investment within a portfolio.
 * It tracks both the investment amount and current valuation.
 *
 * @property holdingId - Unique identifier for this holding (composite key with portfolioId)
 * @property portfolioId - Foreign key linking to the portfolio this holding belongs to
 * @property isin - ISIN (International Securities Identification Number) - unique code for the fund scheme
 * @property fundName - Display name of the mutual fund (e.g., "HDFC Bank Fund")
 * @property units - Number of units held (e.g., 100.5 units)
 * @property currentNAV - Net Asset Value per unit in rupees (real-time market value)
 * @property investmentAmount - Total rupees invested in this holding (sum of all purchases)
 * @property gainLoss - Current profit/loss in rupees (currentValue - investmentAmount)
 * @property lastUpdated - Timestamp when NAV was last updated from the market
 */
data class Holding(
    // Unique identifier for this specific holding
    val holdingId: String,

    // Link to parent portfolio - establishes which portfolio owns this holding
    val portfolioId: String,

    // International Securities ID - standard code to identify the mutual fund scheme globally
    val isin: String,

    // Human-readable name of the fund for UI display
    val fundName: String,

    // Number of units owned - can be fractional (e.g., 100.75 units)
    val units: BigDecimal,

    // Current price per unit in rupees - fetched from market data APIs
    val currentNAV: BigDecimal,

    // Total amount invested in rupees - sum of all purchase prices
    val investmentAmount: BigDecimal,

    // Absolute profit/loss in rupees - positive if profit, negative if loss
    val gainLoss: BigDecimal,

    // Timestamp of last NAV update - helps track data freshness
    val lastUpdated: LocalDateTime = LocalDateTime.now()
) {
    /**
     * Calculated property - Current total value of this holding
     * Formula: units × currentNAV
     * Example: 100 units × ₹50 NAV = ₹5000 current value
     */
    val currentValue: BigDecimal
        get() = units * currentNAV

    /**
     * Calculated property - Gain/Loss percentage
     * Formula: (gainLoss / investmentAmount) × 100
     * Example: If invested ₹1000 and gained ₹100 = 10% return
     * Returns 0 if investmentAmount is zero (no investment yet)
     */
    val gainLossPercentage: BigDecimal
        get() = if (investmentAmount > BigDecimal.ZERO) {
            (gainLoss / investmentAmount) * BigDecimal("100")
        } else {
            BigDecimal.ZERO
        }
}
