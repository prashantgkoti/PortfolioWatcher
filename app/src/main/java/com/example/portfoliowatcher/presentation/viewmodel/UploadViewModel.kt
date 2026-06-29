package com.example.portfoliowatcher.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.portfoliowatcher.data.local.AppDatabase
import com.example.portfoliowatcher.data.remote.MfApiService
import com.example.portfoliowatcher.data.remote.MfSchemeInfo
import com.example.portfoliowatcher.domain.model.Holding
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.UUID

class UploadViewModel(
    private val db: AppDatabase,
    private val api: MfApiService
) : ViewModel() {

    sealed class SearchState {
        object Idle : SearchState()
        object Loading : SearchState()
        data class Results(val schemes: List<MfSchemeInfo>) : SearchState()
        data class Error(val message: String) : SearchState()
    }

    sealed class SaveState {
        object Idle : SaveState()
        object Saving : SaveState()
        object Success : SaveState()
        data class Error(val message: String) : SaveState()
    }

    private val _searchState = MutableStateFlow<SearchState>(SearchState.Idle)
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

    // Holdings the user is building up before saving
    private val _pendingHoldings = MutableStateFlow<List<Holding>>(emptyList())
    val pendingHoldings: StateFlow<List<Holding>> = _pendingHoldings.asStateFlow()

    private var allSchemes: List<MfSchemeInfo> = emptyList()

    init {
        loadSchemeList()
    }

    private fun loadSchemeList() {
        viewModelScope.launch {
            try {
                allSchemes = api.getAllSchemes()
            } catch (e: Exception) {
                // Silent fail - search will show error if list is empty
            }
        }
    }

    fun searchFunds(query: String) {
        if (query.length < 3) {
            _searchState.value = SearchState.Idle
            return
        }
        viewModelScope.launch {
            _searchState.value = SearchState.Loading
            try {
                val results = if (allSchemes.isNotEmpty()) {
                    allSchemes.filter {
                        it.schemeName.contains(query, ignoreCase = true)
                    }.take(20)
                } else {
                    // Fallback: fetch from API directly if local list not loaded
                    api.getAllSchemes()
                        .filter { it.schemeName.contains(query, ignoreCase = true) }
                        .take(20)
                }
                _searchState.value = if (results.isEmpty())
                    SearchState.Results(emptyList())
                else
                    SearchState.Results(results)
            } catch (e: Exception) {
                _searchState.value = SearchState.Error(e.message ?: "Search failed")
            }
        }
    }

    // Fetch latest NAV for a selected scheme and add to pending holdings
    fun addHolding(scheme: MfSchemeInfo, units: String, investmentAmount: String) {
        viewModelScope.launch {
            try {
                val latestNav = api.getLatestNav(scheme.schemeCode)
                val nav = BigDecimal(latestNav.data.firstOrNull()?.nav ?: "0")
                val unitsDecimal = BigDecimal(units.ifBlank { "0" })
                val investedDecimal = BigDecimal(investmentAmount.ifBlank { "0" })
                val currentValue = unitsDecimal * nav
                val gainLoss = currentValue - investedDecimal

                val holding = Holding(
                    holdingId = UUID.randomUUID().toString(),
                    portfolioId = "",  // assigned on save
                    isin = scheme.schemeCode.toString(),
                    fundName = scheme.schemeName,
                    units = unitsDecimal,
                    currentNAV = nav,
                    investmentAmount = investedDecimal,
                    gainLoss = gainLoss
                )
                _pendingHoldings.value = _pendingHoldings.value + holding
                _searchState.value = SearchState.Idle
            } catch (e: Exception) {
                _searchState.value = SearchState.Error("Failed to fetch NAV: ${e.message}")
            }
        }
    }

    fun removeHolding(holdingId: String) {
        _pendingHoldings.value = _pendingHoldings.value.filter { it.holdingId != holdingId }
    }

    fun savePortfolio(portfolioName: String, homeViewModel: HomeViewModel) {
        val holdings = _pendingHoldings.value
        if (portfolioName.isBlank() || holdings.isEmpty()) {
            _saveState.value = SaveState.Error("Add at least one fund before saving")
            return
        }
        viewModelScope.launch {
            _saveState.value = SaveState.Saving
            try {
                homeViewModel.savePortfolio(portfolioName, holdings)
                _pendingHoldings.value = emptyList()
                _saveState.value = SaveState.Success
            } catch (e: Exception) {
                _saveState.value = SaveState.Error(e.message ?: "Save failed")
            }
        }
    }

    fun clearSaveState() { _saveState.value = SaveState.Idle }
    fun clearSearchState() { _searchState.value = SearchState.Idle }

    class Factory(private val db: AppDatabase, private val api: MfApiService) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            UploadViewModel(db, api) as T
    }
}
