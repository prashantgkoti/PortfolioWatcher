/**
 * PortfolioTest.kt - Unit tests for Portfolio domain model
 *
 * Tests the Portfolio model's:
 * - Basic creation and fields
 * - Calculated properties (currentTotalValue, totalGainLoss, etc.)
 * - Aggregation logic across multiple holdings
 * - Percentage calculations
 * - Edge cases (empty portfolio, zero investment)
 */

package com.example.portfoliowatcher.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * PortfolioTest - Test suite for Portfolio data model
 *
 * Tests portfolio aggregation and calculated properties.
 * This is more complex than User because Portfolio has:
 * - Collections of Holdings
 * - Calculated properties that sum across holdings
 * - Percentage calculations
 */
class PortfolioTest {

    /**
     * Test 1: Create empty portfolio
     *
     * Verifies that a Portfolio can be created with no holdings.
     */
    @Test
    fun testCreateEmptyPortfolio() {
        // Act
        val portfolio = Portfolio(
            portfolioId = "port_123",
            userId = "user_123",
            name = "My Portfolio",
            holdings = emptyList(),
            totalValue = BigDecimal.ZERO
        )

        // Assert
        assertEquals("port_123", portfolio.portfolioId)
        assertEquals("user_123", portfolio.userId)
        assertEquals("My Portfolio", portfolio.name)
        assertTrue(portfolio.holdings.isEmpty())
        assertEquals(0, portfolio.holdingCount)
    }

    /**
     * Test 2: Create portfolio with single holding
     *
     * Verifies portfolio can contain holdings and
     * calculates correct aggregated values.
     */
    @Test
    fun testPortfolioWithSingleHolding() {
        // Arrange - Create one holding
        val holding = Holding(
            holdingId = "hold_1",
            portfolioId = "port_123",
            isin = "INF001K01AB4",
            fundName = "HDFC Bank Fund",
            units = BigDecimal("100"),
            currentNAV = BigDecimal("150"),
            investmentAmount = BigDecimal("10000"),
            gainLoss = BigDecimal("5000")  // ₹5000 profit
        )

        // Act - Create portfolio with this holding
        val portfolio = Portfolio(
            portfolioId = "port_123",
            userId = "user_123",
            name = "My Portfolio",
            holdings = listOf(holding),
            totalValue = BigDecimal("15000")
        )

        // Assert - Single holding values should match aggregates
        assertEquals(1, portfolio.holdingCount)
        assertEquals(BigDecimal("10000"), portfolio.totalInvestment)
        assertEquals(BigDecimal("5000"), portfolio.totalGainLoss)
        assertEquals(BigDecimal("15000"), portfolio.currentTotalValue)
    }

    /**
     * Test 3: Calculate gain/loss percentage for single holding
     *
     * Verifies percentage calculation: (gainLoss / investment) * 100
     * Example: ₹5000 profit on ₹10000 investment = 50%
     */
    @Test
    fun testPortfolioGainLossPercentage() {
        // Arrange
        val holding = Holding(
            holdingId = "hold_1",
            portfolioId = "port_123",
            isin = "INF001K01AB4",
            fundName = "Test Fund",
            units = BigDecimal("100"),
            currentNAV = BigDecimal("150"),
            investmentAmount = BigDecimal("10000"),
            gainLoss = BigDecimal("5000")  // 50% return
        )

        val portfolio = Portfolio(
            portfolioId = "port_123",
            userId = "user_123",
            name = "Test Portfolio",
            holdings = listOf(holding),
            totalValue = BigDecimal("15000")
        )

        // Act
        val percentage = portfolio.totalGainLossPercentage

        // Assert - 5000 / 10000 * 100 = 50
        assertEquals(BigDecimal("50"), percentage)
    }

    /**
     * Test 4: Portfolio with multiple holdings
     *
     * Verifies that values aggregate correctly across multiple holdings.
     */
    @Test
    fun testPortfolioWithMultipleHoldings() {
        // Arrange - Create multiple holdings
        val holding1 = Holding(
            holdingId = "hold_1",
            portfolioId = "port_123",
            isin = "INF001",
            fundName = "Fund A",
            units = BigDecimal("100"),
            currentNAV = BigDecimal("100"),
            investmentAmount = BigDecimal("5000"),
            gainLoss = BigDecimal("1000")  // ₹1000 profit
        )

        val holding2 = Holding(
            holdingId = "hold_2",
            portfolioId = "port_123",
            isin = "INF002",
            fundName = "Fund B",
            units = BigDecimal("50"),
            currentNAV = BigDecimal("200"),
            investmentAmount = BigDecimal("5000"),
            gainLoss = BigDecimal("2000")  // ₹2000 profit
        )

        val portfolio = Portfolio(
            portfolioId = "port_123",
            userId = "user_123",
            name = "Multi-Fund Portfolio",
            holdings = listOf(holding1, holding2),
            totalValue = BigDecimal("20000")
        )

        // Act & Assert
        // Should aggregate across both holdings
        assertEquals(2, portfolio.holdingCount)
        assertEquals(BigDecimal("10000"), portfolio.totalInvestment)  // 5000 + 5000
        assertEquals(BigDecimal("3000"), portfolio.totalGainLoss)     // 1000 + 2000
        assertEquals(BigDecimal("20000"), portfolio.currentTotalValue) // sum of current values

        // Percentage should be 30% (3000 / 10000 * 100)
        assertEquals(BigDecimal("30"), portfolio.totalGainLossPercentage)
    }

    /**
     * Test 5: Empty portfolio gain/loss percentage
     *
     * Verifies that portfolios with zero investment return 0% (not divide by zero error).
     */
    @Test
    fun testEmptyPortfolioPercentage() {
        // Act
        val portfolio = Portfolio(
            portfolioId = "port_123",
            userId = "user_123",
            name = "Empty Portfolio",
            holdings = emptyList(),
            totalValue = BigDecimal.ZERO
        )

        // Assert - Should return 0, not crash with divide by zero
        assertEquals(BigDecimal.ZERO, portfolio.totalGainLossPercentage)
    }

    /**
     * Test 6: Portfolio with loss (negative gain)
     *
     * Verifies that portfolios can show losses and negative percentages.
     */
    @Test
    fun testPortfolioWithLoss() {
        // Arrange - Holding with loss
        val holding = Holding(
            holdingId = "hold_1",
            portfolioId = "port_123",
            isin = "INF001",
            fundName = "Losing Fund",
            units = BigDecimal("100"),
            currentNAV = BigDecimal("90"),
            investmentAmount = BigDecimal("10000"),
            gainLoss = BigDecimal("-1000")  // ₹1000 loss (negative)
        )

        val portfolio = Portfolio(
            portfolioId = "port_123",
            userId = "user_123",
            name = "Loss Portfolio",
            holdings = listOf(holding),
            totalValue = BigDecimal("9000")
        )

        // Act & Assert
        assertEquals(BigDecimal("-1000"), portfolio.totalGainLoss)
        // -1000 / 10000 * 100 = -10%
        assertEquals(BigDecimal("-10"), portfolio.totalGainLossPercentage)
    }

    /**
     * Test 7: Verify holding count
     *
     * Verifies that holdingCount accurately reflects the number of holdings.
     */
    @Test
    fun testPortfolioHoldingCount() {
        // Arrange
        val holdings = listOf(
            Holding(
                holdingId = "1",
                portfolioId = "port",
                isin = "INF001",
                fundName = "Fund 1",
                units = BigDecimal.ONE,
                currentNAV = BigDecimal("100"),
                investmentAmount = BigDecimal("100"),
                gainLoss = BigDecimal.ZERO
            ),
            Holding(
                holdingId = "2",
                portfolioId = "port",
                isin = "INF002",
                fundName = "Fund 2",
                units = BigDecimal.ONE,
                currentNAV = BigDecimal("100"),
                investmentAmount = BigDecimal("100"),
                gainLoss = BigDecimal.ZERO
            ),
            Holding(
                holdingId = "3",
                portfolioId = "port",
                isin = "INF003",
                fundName = "Fund 3",
                units = BigDecimal.ONE,
                currentNAV = BigDecimal("100"),
                investmentAmount = BigDecimal("100"),
                gainLoss = BigDecimal.ZERO
            )
        )

        // Act
        val portfolio = Portfolio(
            portfolioId = "port",
            userId = "user",
            name = "Test",
            holdings = holdings,
            totalValue = BigDecimal("300")
        )

        // Assert
        assertEquals(3, portfolio.holdingCount)
    }

    /**
     * Test 8: Portfolio copy with modified name
     *
     * Verifies that the copy() method works with Portfolio.
     */
    @Test
    fun testPortfolioCopy() {
        // Arrange
        val original = Portfolio(
            portfolioId = "port_123",
            userId = "user_123",
            name = "Original Name",
            holdings = emptyList(),
            totalValue = BigDecimal.ZERO
        )

        // Act
        val copied = original.copy(name = "New Name")

        // Assert
        assertEquals("port_123", copied.portfolioId)
        assertEquals("New Name", copied.name)
        assertEquals("Original Name", original.name)  // Original unchanged
    }

    /**
     * Test 9: Very large portfolio values
     *
     * Verifies that BigDecimal can handle large numbers correctly.
     * Example: ₹1 crore portfolio
     */
    @Test
    fun testLargePortfolioValues() {
        // Arrange - 1 crore rupees = 10,000,000
        val holding = Holding(
            holdingId = "hold_1",
            portfolioId = "port",
            isin = "INF001",
            fundName = "Large Fund",
            units = BigDecimal("10000"),
            currentNAV = BigDecimal("1100"),
            investmentAmount = BigDecimal("10000000"),
            gainLoss = BigDecimal("1000000")  // ₹10 lakh profit
        )

        val portfolio = Portfolio(
            portfolioId = "port",
            userId = "user",
            name = "Large Portfolio",
            holdings = listOf(holding),
            totalValue = BigDecimal("11000000")
        )

        // Act & Assert
        assertEquals(BigDecimal("10000000"), portfolio.totalInvestment)
        assertEquals(BigDecimal("1000000"), portfolio.totalGainLoss)
        // 1000000 / 10000000 * 100 = 10%
        assertEquals(BigDecimal("10"), portfolio.totalGainLossPercentage)
    }
}
