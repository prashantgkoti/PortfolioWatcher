package com.example.portfoliowatcher.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.portfoliowatcher.data.local.AppDatabase
import com.example.portfoliowatcher.domain.model.Portfolio
import com.example.portfoliowatcher.presentation.viewmodel.HomeViewModel
import java.math.BigDecimal
import java.math.RoundingMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onPortfolioClick: (String) -> Unit,
    onUploadClick: () -> Unit
) {
    val context = LocalContext.current
    val vm: HomeViewModel = viewModel(
        factory = HomeViewModel.Factory(AppDatabase.getInstance(context))
    )
    val state by vm.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Portfolio Watcher") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onUploadClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Portfolio")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val s = state) {
                is HomeViewModel.UiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is HomeViewModel.UiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(s.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is HomeViewModel.UiState.Empty -> {
                    SummaryCard(portfolios = emptyList())
                    EmptyState(onUploadClick = onUploadClick)
                }
                is HomeViewModel.UiState.Success -> {
                    SummaryCard(portfolios = s.portfolios)
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(s.portfolios) { portfolio ->
                            PortfolioCard(
                                portfolio = portfolio,
                                onClick = { onPortfolioClick(portfolio.portfolioId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(portfolios: List<Portfolio>) {
    val totalValue = portfolios.fold(BigDecimal.ZERO) { s, p -> s + p.currentTotalValue }
    val totalInvested = portfolios.fold(BigDecimal.ZERO) { s, p -> s + p.totalInvestment }
    val totalGain = totalValue - totalInvested
    val pct = if (totalInvested > BigDecimal.ZERO)
        (totalGain / totalInvested * BigDecimal("100")).setScale(2, RoundingMode.HALF_UP)
    else BigDecimal.ZERO

    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Total Portfolio Value", style = MaterialTheme.typography.labelMedium)
            Text(
                "₹${totalValue.setScale(2, RoundingMode.HALF_UP).toPlainString()}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Invested", style = MaterialTheme.typography.labelSmall)
                    Text("₹${totalInvested.setScale(2, RoundingMode.HALF_UP).toPlainString()}", fontWeight = FontWeight.SemiBold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Returns", style = MaterialTheme.typography.labelSmall)
                    Text(
                        "${if (pct >= BigDecimal.ZERO) "+" else ""}$pct%",
                        fontWeight = FontWeight.SemiBold,
                        color = if (pct >= BigDecimal.ZERO) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun PortfolioCard(portfolio: Portfolio, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(portfolio.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("${portfolio.holdingCount} funds", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "₹${portfolio.currentTotalValue.setScale(2, RoundingMode.HALF_UP).toPlainString()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                val pct = portfolio.totalGainLossPercentage.setScale(2, RoundingMode.HALF_UP)
                Text(
                    "${if (pct >= BigDecimal.ZERO) "+" else ""}$pct%",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (pct >= BigDecimal.ZERO) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun EmptyState(onUploadClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("No portfolios yet", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(8.dp))
        Text("Tap + to add your first portfolio", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(24.dp))
        Button(onClick = onUploadClick) { Text("Add Portfolio") }
    }
}
