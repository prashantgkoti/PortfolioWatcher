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
import java.time.LocalDateTime
import java.util.UUID

class HomeViewModel(private val db: AppDatabase, private val context: android.content.Context? = null) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Success(val portfolios: List<Portfolio>) : UiState()
        object Empty : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Hardcoded userId until Firebase Auth is added in Week 3
    private val userId = "local_user"

    init {
        ensureLocalUserExists()
        loadPortfolios()
        refreshNavPrices()
    }

    private fun ensureLocalUserExists() {
        viewModelScope.launch {
            val exists = db.userDao().userExists(userId)
            if (exists == 0) {
                db.userDao().insertUser(
                    com.example.portfoliowatcher.data.local.entity.UserEntity(
                        userId = userId,
                        email = "local@device.com"
                    )
                )
            }
        }
    }

    private fun refreshNavPrices() {
        if (context == null) return
        viewModelScope.launch {
            com.example.portfoliowatcher.data.remote.NavRefreshService.refreshIfStale(context)
            loadPortfolios()  // reload after refresh to show updated values
        }
    }

    fun loadPortfolios() {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                val entities = db.portfolioDao().getPortfoliosByUserId(userId)
                if (entities.isEmpty()) {
                    _uiState.value = UiState.Empty
                } else {
                    val portfolios = entities.map { entity ->
                        val holdingEntities = db.holdingDao().getHoldingsByPortfolioId(entity.portfolioId)
                        entity.toDomain(holdingEntities)
                    }
                    _uiState.value = UiState.Success(portfolios)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to load portfolios")
            }
        }
    }

    fun savePortfolio(name: String, holdings: List<Holding>) {
        viewModelScope.launch {
            try {
                val portfolioId = UUID.randomUUID().toString()
                val totalValue = holdings.fold(BigDecimal.ZERO) { s, h -> s + h.currentValue }

                db.portfolioDao().insertPortfolio(
                    PortfolioEntity(
                        portfolioId = portfolioId,
                        userId = userId,
                        name = name,
                        totalValue = totalValue
                    )
                )
                val holdingEntities = holdings.map { it.toEntity(portfolioId) }
                db.holdingDao().insertHoldings(holdingEntities)
                loadPortfolios()
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to save portfolio")
            }
        }
    }

    fun deletePortfolio(portfolioId: String) {
        viewModelScope.launch {
            db.portfolioDao().deletePortfolioById(portfolioId)
            loadPortfolios()
        }
    }

    class Factory(private val db: AppDatabase, private val context: android.content.Context? = null) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            HomeViewModel(db, context) as T
    }
}

// Extension functions to map between Entity and Domain

private fun PortfolioEntity.toDomain(holdingEntities: List<HoldingEntity>): Portfolio {
    return Portfolio(
        portfolioId = portfolioId,
        userId = userId,
        name = name,
        holdings = holdingEntities.map { it.toDomain() },
        totalValue = totalValue,
        createdAt = createdAt
    )
}

private fun HoldingEntity.toDomain(): Holding {
    return Holding(
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
}

private fun Holding.toEntity(portfolioId: String): HoldingEntity {
    return HoldingEntity(
        holdingId = holdingId.ifBlank { UUID.randomUUID().toString() },
        portfolioId = portfolioId,
        isin = isin,
        fundName = fundName,
        units = units,
        currentNAV = currentNAV,
        investmentAmount = investmentAmount,
        gainLoss = gainLoss,
        lastUpdated = LocalDateTime.now()
    )
}
