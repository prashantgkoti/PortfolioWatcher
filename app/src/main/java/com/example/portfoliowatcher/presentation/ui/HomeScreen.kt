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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.portfoliowatcher.domain.model.Portfolio
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onPortfolioClick: (String) -> Unit,
    onUploadClick: () -> Unit
) {
    // Placeholder portfolios until ViewModel is wired up
    val portfolios = remember { emptyList<Portfolio>() }

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
                Icon(Icons.Default.Add, contentDescription = "Upload Portfolio")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            // Summary card at top
            SummaryCard(portfolios = portfolios)

            if (portfolios.isEmpty()) {
                EmptyState(onUploadClick = onUploadClick)
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(portfolios) { portfolio ->
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

@Composable
private fun SummaryCard(portfolios: List<Portfolio>) {
    val totalValue = portfolios.fold(BigDecimal.ZERO) { sum, p -> sum + p.totalValue }
    val totalInvested = portfolios.fold(BigDecimal.ZERO) { sum, p -> sum + p.totalInvestment }
    val totalGain = totalValue - totalInvested

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Total Portfolio Value",
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = "₹${totalValue.toPlainString()}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Invested", style = MaterialTheme.typography.labelSmall)
                    Text("₹${totalInvested.toPlainString()}", fontWeight = FontWeight.SemiBold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Gain/Loss", style = MaterialTheme.typography.labelSmall)
                    Text(
                        text = "₹${totalGain.toPlainString()}",
                        fontWeight = FontWeight.SemiBold,
                        color = if (totalGain >= BigDecimal.ZERO)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
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
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = portfolio.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${portfolio.holdingCount} funds",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${portfolio.currentTotalValue.toPlainString()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                val pct = portfolio.totalGainLossPercentage
                Text(
                    text = "${if (pct >= BigDecimal.ZERO) "+" else ""}${pct.setScale(2, java.math.RoundingMode.HALF_UP)}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (pct >= BigDecimal.ZERO)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error
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
        Text(
            text = "No portfolios yet",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap + to upload your mutual fund portfolio",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onUploadClick) {
            Text("Upload Portfolio")
        }
    }
}
