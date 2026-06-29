/**
 * PortfolioViewModel.kt - ViewModel for Portfolio Management
 *
 * ViewModels are lifecycle-aware containers for UI state.
 * They survive configuration changes (like device rotation) but are destroyed when activity is destroyed.
 *
 * Key responsibilities:
 * - Hold and manage UI state (loading, portfolios, errors)
 * - Communicate with repositories and use cases
 * - NOT aware of UI specifics (Views, Activities, Fragments)
 * - Survive configuration changes
 *
 * This follows MVVM (Model-View-ViewModel) architecture pattern.
 */

package com.example.portfoliowatcher.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.portfoliowatcher.domain.model.Portfolio
import com.example.portfoliowatcher.domain.repository.PortfolioRepository
import java.math.BigDecimal

/**
 * PortfolioViewModel - Manages portfolio data and user interactions
 *
 * @property repository - PortfolioRepository for accessing portfolio data
 *
 * ViewModels should not directly hold references to Views or Activities
 * They communicate through StateFlow<UiState> that UI observes
 */
class PortfolioViewModel(
    private val repository: PortfolioRepository
) : ViewModel() {

    // ==================== STATE MANAGEMENT ====================

    /**
     * Represents different states of the portfolio screen
     *
     * This sealed class pattern enforces type-safe state management
     * UI can check current state and render accordingly
     */
    sealed class UiState {
        // Initial state - screen is loading
        object Loading : UiState()

        // Success state - contains portfolio data
        data class Success(val portfolios: List<Portfolio>) : UiState()

        // Error state - contains error message to display
        data class Error(val message: String) : UiState()

        // Empty state - no portfolios yet (user hasn't uploaded any)
        object Empty : UiState()
    }

    /**
     * Internal MutableStateFlow - mutable within ViewModel
     * Used to update state from ViewModel functions
     */
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)

    /**
     * Public StateFlow - immutable for UI/observers
     * UI collects from this read-only flow, can't modify it
     * This prevents accidental state mutations from UI layer
     */
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    /**
     * Holds error messages
     * Separate from main state for error handling/dismissal
     */
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * Loading indicator for showing progress/spinners
     */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Portfolio that user is currently viewing in detail
     */
    private val _selectedPortfolio = MutableStateFlow<Portfolio?>(null)
    val selectedPortfolio: StateFlow<Portfolio?> = _selectedPortfolio.asStateFlow()

    // ==================== BUSINESS LOGIC FUNCTIONS ====================

    /**
     * Load all portfolios for current user
     *
     * This function:
     * 1. Sets loading state to true (show spinner)
     * 2. Calls repository to fetch portfolios (database operation)
     * 3. Updates UI state with results or error
     * 4. Sets loading state to false (hide spinner)
     *
     * viewModelScope.launch - runs in ViewModel's coroutine scope
     * Automatically cancels when ViewModel is destroyed (prevents memory leaks)
     */
    fun loadPortfolios(userId: String) {
        viewModelScope.launch {
            try {
                // Set loading state
                _isLoading.value = true
                _uiState.value = UiState.Loading

                // Fetch portfolios from repository (non-blocking)
                val portfolios = repository.getPortfoliosByUserId(userId)

                // Update state based on result
                _uiState.value = if (portfolios.isEmpty()) {
                    // No portfolios yet - show empty state
                    UiState.Empty
                } else {
                    // Have portfolios - show them
                    UiState.Success(portfolios)
                }

                // Clear any previous errors
                _errorMessage.value = null

            } catch (e: Exception) {
                // Handle error
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
                _errorMessage.value = "Failed to load portfolios: ${e.message}"
            } finally {
                // Always stop loading, even if error
                _isLoading.value = false
            }
        }
    }

    /**
     * Handle portfolio selection for detail view
     *
     * @param portfolio - Portfolio user clicked on
     *
     * Updates selectedPortfolio so detail screen can display it
     * Called when user taps on a portfolio in the list
     */
    fun selectPortfolio(portfolio: Portfolio) {
        _selectedPortfolio.value = portfolio
    }

    /**
     * Clear selected portfolio (go back from detail view)
     *
     * Called when user closes portfolio detail view
     * Allows garbage collection of large portfolio object
     */
    fun clearSelectedPortfolio() {
        _selectedPortfolio.value = null
    }

    /**
     * Refresh NAV prices for all holdings in a portfolio
     *
     * In production, this would:
     * 1. Fetch latest NAV from MFapi.in
     * 2. Call repository.updateNAV()
     * 3. Reload portfolio to show new values
     *
     * @param portfolioId - Portfolio to refresh
     */
    fun refreshPortfolioNAV(portfolioId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // TODO: Fetch latest NAV from MFapi.in API
                // val navUpdates = mfApiService.getNAVUpdates(...)
                // repository.updateNAV(portfolioId, navUpdates)

                // Then reload portfolios to show updated values
                // This triggers UI refresh automatically

            } catch (e: Exception) {
                _errorMessage.value = "Failed to refresh NAV: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Delete a portfolio
     *
     * @param portfolioId - Portfolio to delete
     *
     * Calls repository to delete from database
     * Then reloads portfolio list to reflect deletion
     */
    fun deletePortfolio(portfolioId: String, userId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // Delete from repository (database)
                repository.deletePortfolio(portfolioId)

                // Reload portfolio list to show deletion
                loadPortfolios(userId)

                // Show confirmation message
                _errorMessage.value = "Portfolio deleted successfully"

            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete portfolio: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Dismiss error message
     *
     * Called when user closes error notification
     * Clears the error message
     */
    fun dismissError() {
        _errorMessage.value = null
    }
}
