package com.example.portfoliowatcher.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.portfoliowatcher.presentation.ui.HomeScreen
import com.example.portfoliowatcher.presentation.ui.PortfolioDetailScreen
import com.example.portfoliowatcher.presentation.ui.UploadScreen

object Routes {
    const val HOME = "home"
    const val PORTFOLIO_DETAIL = "portfolio_detail/{portfolioId}"
    const val UPLOAD = "upload"

    fun portfolioDetail(portfolioId: String) = "portfolio_detail/$portfolioId"
}

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            HomeScreen(
                onPortfolioClick = { portfolioId ->
                    navController.navigate(Routes.portfolioDetail(portfolioId))
                },
                onUploadClick = {
                    navController.navigate(Routes.UPLOAD)
                }
            )
        }

        composable(Routes.PORTFOLIO_DETAIL) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId") ?: ""
            PortfolioDetailScreen(
                portfolioId = portfolioId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.UPLOAD) {
            UploadScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
