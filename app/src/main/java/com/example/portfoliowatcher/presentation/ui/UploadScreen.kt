package com.example.portfoliowatcher.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(onBack: () -> Unit) {
    var portfolioName by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Portfolio") },
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
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Import Mutual Fund Portfolio",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Enter your mobile number registered with CAMS/KFintech to fetch your mutual fund holdings.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = portfolioName,
                onValueChange = { portfolioName = it },
                label = { Text("Portfolio Name") },
                placeholder = { Text("e.g. My Retirement Fund") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = mobileNumber,
                onValueChange = { if (it.length <= 10) mobileNumber = it },
                label = { Text("Mobile Number") },
                placeholder = { Text("10-digit mobile number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { /* TODO: fetch from MFapi.in */ },
                modifier = Modifier.fillMaxWidth(),
                enabled = portfolioName.isNotBlank() && mobileNumber.length == 10
            ) {
                Text("Fetch Portfolio")
            }

            HorizontalDivider()

            Text(
                text = "Or manually enter holdings",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            OutlinedButton(
                onClick = { /* TODO: manual entry */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Fund Manually")
            }

            Spacer(modifier = Modifier.weight(1f))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Privacy Note",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Your data is stored only on this device. Your mobile number is used only to fetch portfolio data and is never shared.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
