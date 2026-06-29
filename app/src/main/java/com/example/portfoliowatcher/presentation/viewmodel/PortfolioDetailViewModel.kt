package com.example.portfoliowatcher.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.portfoliowatcher.data.local.AppDatabase
import com.example.portfoliowatcher.data.local.entity.HoldingEntity
import com.example.portfoliowatcher.data.local.entity.PortfolioEntity
import com.example.portfoliowatcher.domain.model.Holding
import com.example.portfoliowatcher.domain.model.Portfolio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

class PortfolioDetailViewModel(private val db: AppDatabase) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Success(val portfolio: Portfolio) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun load(portfolioId: String) {
        viewModelScope.launch {
            try {
                val entity = db.portfolioDao().getPortfolioById(portfolioId)
                    ?: run { _uiState.value = UiState.Error("Portfolio not found"); return@launch }
                val holdingEntities = db.holdingDao().getHoldingsByPortfolioId(portfolioId)
                _uiState.value = UiState.Success(entity.toDomain(holdingEntities))
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to load")
            }
        }
    }

    class Factory(private val db: AppDatabase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            PortfolioDetailViewModel(db) as T
    }
}

private fun PortfolioEntity.toDomain(holdingEntities: List<HoldingEntity>) = Portfolio(
    portfolioId = portfolioId,
    userId = userId,
    name = name,
    holdings = holdingEntities.map { it.toDomain() },
    totalValue = totalValue,
    createdAt = createdAt
)

private fun HoldingEntity.toDomain() = Holding(
    holdingId = holdingId,
    portfolioId = portfolioId,
    isin = isin,
    fundName = fundName,
    units = units,
    currentNAV = currentNAV,
    investmentAmount = investmentAmount,
    gainLoss = gainLoss,
    lastUpdated = lastUpdated
)
