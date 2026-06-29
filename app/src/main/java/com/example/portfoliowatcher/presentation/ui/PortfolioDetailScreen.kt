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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.portfoliowatcher.domain.model.Holding
import java.math.BigDecimal
import java.math.RoundingMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioDetailScreen(
    portfolioId: String,
    onBack: () -> Unit
) {
    // Placeholder until ViewModel is wired up
    val holdings = remember { emptyList<Holding>() }
    val portfolioName = remember { "Portfolio" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(portfolioName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            item {
                HoldingSummaryCard(holdings = holdings)
            }

            if (holdings.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No holdings in this portfolio",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(holdings) { holding ->
                    HoldingRow(holding = holding)
                }
            }
        }
    }
}

@Composable
private fun HoldingSummaryCard(holdings: List<Holding>) {
    val totalInvested = holdings.fold(BigDecimal.ZERO) { s, h -> s + h.investmentAmount }
    val totalCurrent = holdings.fold(BigDecimal.ZERO) { s, h -> s + h.currentValue }
    val totalGain = totalCurrent - totalInvested
    val pct = if (totalInvested > BigDecimal.ZERO)
        (totalGain / totalInvested * BigDecimal("100")).setScale(2, RoundingMode.HALF_UP)
    else BigDecimal.ZERO

    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(label = "Invested", value = "₹${totalInvested.toPlainString()}")
                StatItem(label = "Current", value = "₹${totalCurrent.toPlainString()}", align = Alignment.CenterHorizontally)
                StatItem(
                    label = "Returns",
                    value = "${if (pct >= BigDecimal.ZERO) "+" else ""}$pct%",
                    valueColor = if (pct >= BigDecimal.ZERO)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error,
                    align = Alignment.End
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSecondaryContainer,
    align: Alignment.Horizontal = Alignment.Start
) {
    Column(horizontalAlignment = align) {
        Text(text = label, style = MaterialTheme.typography.labelSmall)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

@Composable
private fun HoldingRow(holding: Holding) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = holding.fundName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${holding.units.setScale(3, RoundingMode.HALF_UP)} units • NAV ₹${holding.currentNAV.setScale(2, RoundingMode.HALF_UP)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${holding.currentValue.setScale(2, RoundingMode.HALF_UP).toPlainString()}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                val pct = holding.gainLossPercentage.setScale(2, RoundingMode.HALF_UP)
                Text(
                    text = "${if (pct >= BigDecimal.ZERO) "+" else ""}$pct%",
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
