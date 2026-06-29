package com.example.portfoliowatcher.data.remote

import android.content.Context
import com.example.portfoliowatcher.data.local.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

// NavRefreshService - Refreshes NAV prices for all holdings from MFapi.in
// Called on app startup if last refresh was more than 8 hours ago
object NavRefreshService {

    private const val PREFS_NAME = "nav_refresh_prefs"
    private const val KEY_LAST_REFRESH = "last_refresh_epoch"
    private const val REFRESH_INTERVAL_HOURS = 8L

    suspend fun refreshIfStale(context: Context) {
        if (!isRefreshNeeded(context)) return
        refresh(context)
    }

    suspend fun refresh(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                val db = AppDatabase.getInstance(context)
                val api = MfApiService.create()

                // Get all unique ISINs (scheme codes) across all holdings
                val allPortfolios = db.portfolioDao().getPortfoliosByUserId(getUserId(context))
                val allHoldings = allPortfolios.flatMap { portfolio ->
                    db.holdingDao().getHoldingsByPortfolioId(portfolio.portfolioId)
                }

                val uniqueIsins = allHoldings.map { it.isin }.distinct()

                uniqueIsins.forEach { isin ->
                    try {
                        val schemeCode = isin.toIntOrNull() ?: return@forEach
                        val latest = api.getLatestNav(schemeCode)
                        val newNav = latest.data.firstOrNull()?.nav?.let { BigDecimal(it) }
                            ?: return@forEach

                        db.holdingDao().updateNAVByIsin(isin, newNav, LocalDateTime.now())
                    } catch (e: Exception) {
                        // Skip this fund if NAV fetch fails, continue with others
                    }
                }

                saveLastRefreshTime(context)
            } catch (e: Exception) {
                // Silent fail - app still works with stale NAV data
            }
        }
    }

    private fun isRefreshNeeded(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastRefreshEpoch = prefs.getLong(KEY_LAST_REFRESH, 0L)
        if (lastRefreshEpoch == 0L) return true
        val lastRefresh = LocalDateTime.ofEpochSecond(lastRefreshEpoch, 0, java.time.ZoneOffset.UTC)
        val hoursSinceRefresh = ChronoUnit.HOURS.between(lastRefresh, LocalDateTime.now())
        return hoursSinceRefresh >= REFRESH_INTERVAL_HOURS
    }

    private fun saveLastRefreshTime(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putLong(KEY_LAST_REFRESH, LocalDateTime.now().toEpochSecond(java.time.ZoneOffset.UTC)).apply()
    }

    private fun getUserId(context: Context): String = "local_user"
}
