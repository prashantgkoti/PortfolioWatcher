/**
 * HoldingTest.kt - Unit tests for Holding domain model
 *
 * Tests the Holding model's:
 * - Basic creation and fields
 * - Calculated properties (currentValue, gainLossPercentage)
 * - BigDecimal math precision
 * - Edge cases (zero units, zero investment)
 */

package com.example.portfoliowatcher.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

/**
 * HoldingTest - Test suite for individual fund holding
 *
 * Tests a single holding (one mutual fund investment).
 */
class HoldingTest {

    /**
     * Test 1: Create holding with standard values
     *
     * Verifies basic holding creation and field assignment.
     */
    @Test
    fun testCreateHolding() {
        // Act
        val holding = Holding(
            holdingId = "hold_123",
            portfolioId = "port_123",
            isin = "INF001K01AB4",
            fundName = "HDFC Bank Fund",
            units = BigDecimal("100.5"),
            currentNAV = BigDecimal("150.25"),
            investmentAmount = BigDecimal("10000"),
            gainLoss = BigDecimal("5000")
        )

        // Assert
        assertEquals("hold_123", holding.holdingId)
        assertEquals("HDFC Bank Fund", holding.fundName)
        assertEquals(BigDecimal("100.5"), holding.units)
        assertEquals(BigDecimal("150.25"), holding.currentNAV)
    }

    /**
     * Test 2: Calculate current value
     *
     * Verifies that currentValue = units * currentNAV
     * Example: 100 units * ₹150 NAV = ₹15,000
     */
    @Test
    fun testCalculateCurrentValue() {
        // Arrange
        val holding = Holding(
            holdingId = "hold_1",
            portfolioId = "port",
            isin = "INF001",
            fundName = "Test Fund",
            units = BigDecimal("100"),
            currentNAV = BigDecimal("150"),
            investmentAmount = BigDecimal("10000"),
            gainLoss = BigDecimal("5000")
        )

        // Act
        val currentValue = holding.currentValue

        // Assert - 100 * 150 = 15000
        assertEquals(BigDecimal("15000"), currentValue)
    }

    /**
     * Test 3: Calculate gain/loss percentage
     *
     * Verifies that gainLossPercentage = (gainLoss / investmentAmount) * 100
     * Example: ₹5000 gain / ₹10000 investment = 50%
     */
    @Test
    fun testCalculateGainLossPercentage() {
        // Arrange
        val holding = Holding(
            holdingId = "hold_1",
            portfolioId = "port",
            isin = "INF001",
            fundName = "Test Fund",
            units = BigDecimal("100"),
            currentNAV = BigDecimal("150"),
            investmentAmount = BigDecimal("10000"),
            gainLoss = BigDecimal("5000")  // 50% return
        )

        // Act
        val percentage = holding.gainLossPercentage

        // Assert - 5000 / 10000 * 100 = 50
        assertEquals(BigDecimal("50"), percentage)
    }

    /**
     * Test 4: Fractional units calculation
     *
     * Verifies that fractional units work correctly.
     * Example: 100.5 units * ₹150.25 NAV
     */
    @Test
    fun testFractionalUnitsValue() {
        // Arrange
        val holding = Holding(
            holdingId = "hold_1",
            portfolioId = "port",
            isin = "INF001",
            fundName = "Test Fund",
            units = BigDecimal("100.5"),
            currentNAV = BigDecimal("150.25"),
            investmentAmount = BigDecimal("10000"),
            gainLoss = BigDecimal("5000")
        )

        // Act
        val currentValue = holding.currentValue

        // Assert - 100.5 * 150.25 = 15100.625
        assertEquals(BigDecimal("15100.625"), currentValue)
    }

    /**
     * Test 5: Negative gain (loss)
     *
     * Verifies that holdings can show losses with negative gainLoss values.
     */
    @Test
    fun testHoldingWithLoss() {
        // Arrange
        val holding = Holding(
            holdingId = "hold_1",
            portfolioId = "port",
            isin = "INF001",
            fundName = "Losing Fund",
            units = BigDecimal("100"),
            currentNAV = BigDecimal("90"),
            investmentAmount = BigDecimal("10000"),
            gainLoss = BigDecimal("-1000")  // Loss of ₹1000
        )

        // Act & Assert
        assertEquals(BigDecimal("-1000"), holding.gainLoss)
        // -1000 / 10000 * 100 = -10%
        assertEquals(BigDecimal("-10"), holding.gainLossPercentage)
    }

    /**
     * Test 6: Zero gain/loss
     *
     * Verifies that holdings with break-even investment show 0% return.
     */
    @Test
    fun testHoldingWithZeroGainLoss() {
        // Arrange
        val holding = Holding(
            holdingId = "hold_1",
            portfolioId = "port",
            isin = "INF001",
            fundName = "Stable Fund",
            units = BigDecimal("100"),
            currentNAV = BigDecimal("100"),
            investmentAmount = BigDecimal("10000"),
            gainLoss = BigDecimal.ZERO  // No profit or loss
        )

        // Act & Assert
        assertEquals(BigDecimal.ZERO, holding.gainLoss)
        assertEquals(BigDecimal.ZERO, holding.gainLossPercentage)
    }

    /**
     * Test 7: Zero investment (edge case)
     *
     * Verifies that zero investment doesn't cause divide-by-zero error.
     * Should return 0% when investment is zero.
     */
    @Test
    fun testHoldingWithZeroInvestment() {
        // Arrange
        val holding = Holding(
            holdingId = "hold_1",
            portfolioId = "port",
            isin = "INF001",
            fundName = "Free Fund",
            units = BigDecimal("100"),
            currentNAV = BigDecimal("100"),
            investmentAmount = BigDecimal.ZERO,  // No investment
            gainLoss = BigDecimal("1000")
        )

        // Act & Assert
        // When investment is zero, percentage should be 0 (not divide by zero)
        assertEquals(BigDecimal.ZERO, holding.gainLossPercentage)
    }

    /**
     * Test 8: Holding copy method
     *
     * Verifies that copy() creates a new holding with changes.
     */
    @Test
    fun testHoldingCopy() {
        // Arrange
        val original = Holding(
            holdingId = "hold_1",
            portfolioId = "port",
            isin = "INF001",
            fundName = "Original Fund",
            units = BigDecimal("100"),
            currentNAV = BigDecimal("100"),
            investmentAmount = BigDecimal("10000"),
            gainLoss = BigDecimal("1000")
        )

        // Act
        val copied = original.copy(
            currentNAV = BigDecimal("110"),  // NAV increased
            gainLoss = BigDecimal("2000")    // Gain increased
        )

        // Assert
        assertEquals(BigDecimal("110"), copied.currentNAV)
        assertEquals(BigDecimal("2000"), copied.gainLoss)
        // Original unchanged
        assertEquals(BigDecimal("100"), original.currentNAV)
        assertEquals(BigDecimal("1000"), original.gainLoss)
    }

    /**
     * Test 9: Very small percentage return
     *
     * Verifies BigDecimal precision for small percentage calculations.
     * Example: ₹10 gain on ₹10,000 investment = 0.1%
     */
    @Test
    fun testSmallPercentageReturn() {
        // Arrange
        val holding = Holding(
            holdingId = "hold_1",
            portfolioId = "port",
            isin = "INF001",
            fundName = "Stable Fund",
            units = BigDecimal("1000"),
            currentNAV = BigDecimal("10.01"),
            investmentAmount = BigDecimal("10000"),
            gainLoss = BigDecimal("10")  // Small ₹10 profit
        )

        // Act
        val percentage = holding.gainLossPercentage

        // Assert - 10 / 10000 * 100 = 0.1
        assertEquals(BigDecimal("0.1"), percentage)
    }

    /**
     * Test 10: Large percentage return
     *
     * Verifies calculations for high-return funds (200%+ returns).
     */
    @Test
    fun testLargePercentageReturn() {
        // Arrange
        val holding = Holding(
            holdingId = "hold_1",
            portfolioId = "port",
            isin = "INF001",
            fundName = "High-Return Fund",
            units = BigDecimal("100"),
            currentNAV = BigDecimal("300"),  // Tripled in value
            investmentAmount = BigDecimal("10000"),
            gainLoss = BigDecimal("20000")  // ₹20,000 profit (200% return)
        )

        // Act
        val percentage = holding.gainLossPercentage

        // Assert - 20000 / 10000 * 100 = 200
        assertEquals(BigDecimal("200"), percentage)
    }
}
