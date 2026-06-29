package com.example.portfoliowatcher.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.portfoliowatcher.domain.model.Holding
import com.example.portfoliowatcher.domain.model.Portfolio
import com.example.portfoliowatcher.presentation.viewmodel.PortfolioDetailViewModel
import java.math.BigDecimal
import java.math.RoundingMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioDetailScreen(portfolioId: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val vm: PortfolioDetailViewModel = viewModel(
        factory = PortfolioDetailViewModel.Factory(AppDatabase.getInstance(context))
    )
    val state by vm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(portfolioId) { vm.load(portfolioId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (state is PortfolioDetailViewModel.UiState.Success)
                            (state as PortfolioDetailViewModel.UiState.Success).portfolio.name
                        else "Portfolio"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        when (val s = state) {
            is PortfolioDetailViewModel.UiState.Loading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is PortfolioDetailViewModel.UiState.Error -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text(s.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is PortfolioDetailViewModel.UiState.Success -> {
                PortfolioContent(portfolio = s.portfolio, modifier = Modifier.padding(padding))
            }
        }
    }
}

@Composable
private fun PortfolioContent(portfolio: Portfolio, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        item { SummaryCard(portfolio = portfolio) }

        if (portfolio.holdings.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No holdings", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(portfolio.holdings) { holding ->
                HoldingRow(holding = holding)
            }
        }
    }
}

@Composable
private fun SummaryCard(portfolio: Portfolio) {
    val pct = portfolio.totalGainLossPercentage.setScale(2, RoundingMode.HALF_UP)

    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "₹${portfolio.currentTotalValue.setScale(2, RoundingMode.HALF_UP).toPlainString()}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                "${if (pct >= BigDecimal.ZERO) "+" else ""}$pct% overall returns",
                style = MaterialTheme.typography.bodyMedium,
                color = if (pct >= BigDecimal.ZERO) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
            HorizontalDivider()
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatColumn("Invested", "₹${portfolio.totalInvestment.setScale(2, RoundingMode.HALF_UP).toPlainString()}")
                StatColumn("Gain/Loss", "₹${portfolio.totalGainLoss.setScale(2, RoundingMode.HALF_UP).toPlainString()}")
                StatColumn("Funds", "${portfolio.holdingCount}")
            }
        }
    }
}

@Composable
private fun StatColumn(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun HoldingRow(holding: Holding) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(holding.fundName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    "${holding.units.setScale(3, RoundingMode.HALF_UP)} units  •  NAV ₹${holding.currentNAV.setScale(2, RoundingMode.HALF_UP)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "₹${holding.currentValue.setScale(2, RoundingMode.HALF_UP).toPlainString()}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                val pct = holding.gainLossPercentage.setScale(2, RoundingMode.HALF_UP)
                Text(
                    "${if (pct >= BigDecimal.ZERO) "+" else ""}$pct%",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (pct >= BigDecimal.ZERO) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
