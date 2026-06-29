package com.example.portfoliowatcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.portfoliowatcher.presentation.navigation.AppNavigation
import com.example.portfoliowatcher.ui.theme.PortfolioWatcherTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PortfolioWatcherTheme {
                AppNavigation()
            }
        }
    }
}
