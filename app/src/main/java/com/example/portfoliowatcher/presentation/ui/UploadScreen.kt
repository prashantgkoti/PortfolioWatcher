package com.example.portfoliowatcher.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.portfoliowatcher.data.local.AppDatabase
import com.example.portfoliowatcher.data.remote.MfApiService
import com.example.portfoliowatcher.data.remote.MfSchemeInfo
import com.example.portfoliowatcher.domain.model.Holding
import com.example.portfoliowatcher.presentation.viewmodel.HomeViewModel
import com.example.portfoliowatcher.presentation.viewmodel.UploadViewModel
import java.math.RoundingMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val vm: UploadViewModel = viewModel(
        factory = UploadViewModel.Factory(db, MfApiService.create())
    )
    val homeVm: HomeViewModel = viewModel(
        factory = HomeViewModel.Factory(db)
    )

    val searchState by vm.searchState.collectAsStateWithLifecycle()
    val saveState by vm.saveState.collectAsStateWithLifecycle()
    val pendingHoldings by vm.pendingHoldings.collectAsStateWithLifecycle()

    var portfolioName by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedScheme by remember { mutableStateOf<MfSchemeInfo?>(null) }

    // Navigate back on save success
    LaunchedEffect(saveState) {
        if (saveState is UploadViewModel.SaveState.Success) {
            vm.clearSaveState()
            onBack()
        }
    }

    if (showAddDialog && selectedScheme != null) {
        AddHoldingDialog(
            scheme = selectedScheme!!,
            onConfirm = { units, invested ->
                vm.addHolding(selectedScheme!!, units, invested)
                showAddDialog = false
                searchQuery = ""
            },
            onDismiss = {
                showAddDialog = false
                vm.clearSearchState()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Portfolio") },
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
        LazyColumn(
            modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedTextField(
                    value = portfolioName,
                    onValueChange = { portfolioName = it },
                    label = { Text("Portfolio Name") },
                    placeholder = { Text("e.g. My SIP Portfolio") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                Text("Search & Add Funds", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            }

            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        vm.searchFunds(it)
                    },
                    label = { Text("Search fund by name") },
                    placeholder = { Text("e.g. HDFC Midcap") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = ""; vm.clearSearchState() }) {
                                Icon(Icons.Default.Close, "Clear")
                            }
                        }
                    }
                )
            }

            // Search results
            when (val state = searchState) {
                is UploadViewModel.SearchState.Loading -> item {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
                is UploadViewModel.SearchState.Results -> {
                    if (state.schemes.isEmpty()) {
                        item { Text("No funds found. Try a different name.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    } else {
                        items(state.schemes) { scheme ->
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable {
                                    selectedScheme = scheme
                                    showAddDialog = true
                                }
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(scheme.schemeName, style = MaterialTheme.typography.bodyMedium)
                                    Text(scheme.schemeCategory, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
                is UploadViewModel.SearchState.Error -> item {
                    Text(state.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                else -> {}
            }

            // Added holdings
            if (pendingHoldings.isNotEmpty()) {
                item {
                    HorizontalDivider()
                    Text(
                        "${pendingHoldings.size} fund(s) added",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                items(pendingHoldings) { holding ->
                    PendingHoldingRow(holding = holding, onRemove = { vm.removeHolding(holding.holdingId) })
                }
            }

            // Save button
            item {
                Spacer(Modifier.height(8.dp))
                when (val state = saveState) {
                    is UploadViewModel.SaveState.Saving -> {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is UploadViewModel.SaveState.Error -> {
                        Text(state.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(4.dp))
                        SaveButton(enabled = pendingHoldings.isNotEmpty() && portfolioName.isNotBlank()) {
                            vm.savePortfolio(portfolioName, homeVm)
                        }
                    }
                    else -> {
                        SaveButton(enabled = pendingHoldings.isNotEmpty() && portfolioName.isNotBlank()) {
                            vm.savePortfolio(portfolioName, homeVm)
                        }
                    }
                }
            }

            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Text(
                        "🔒 Your data stays on this device only. Live NAV is fetched from mfapi.in.",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SaveButton(enabled: Boolean, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth(), enabled = enabled) {
        Text("Save Portfolio")
    }
}

@Composable
private fun PendingHoldingRow(holding: Holding, onRemove: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(holding.fundName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                Text(
                    "${holding.units.setScale(3, RoundingMode.HALF_UP)} units • NAV ₹${holding.currentNAV.setScale(2, RoundingMode.HALF_UP)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Close, "Remove", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun AddHoldingDialog(
    scheme: MfSchemeInfo,
    onConfirm: (units: String, invested: String) -> Unit,
    onDismiss: () -> Unit
) {
    var units by remember { mutableStateOf("") }
    var invested by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Holding") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(scheme.schemeName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                OutlinedTextField(
                    value = units,
                    onValueChange = { units = it },
                    label = { Text("Units held") },
                    placeholder = { Text("e.g. 125.50") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = invested,
                    onValueChange = { invested = it },
                    label = { Text("Amount invested (₹)") },
                    placeholder = { Text("e.g. 50000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(units, invested) },
                enabled = units.isNotBlank() && invested.isNotBlank()
            ) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
