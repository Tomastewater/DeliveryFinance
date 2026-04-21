package com.tomastewater.deliveryfinance.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tomastewater.deliveryfinance.presentation.dashboard.DashboardScreen
import com.tomastewater.deliveryfinance.presentation.goal.AddGoalScreen
import com.tomastewater.deliveryfinance.presentation.history.HistoryScreen

@Composable
fun DeliveryFinanceNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(route = Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToAddGoal = { navController.navigate(Screen.AddGoal.route) },
                onNavigateToHistory = { navController.navigate(Screen.History.route) } // Nueva acción
            )
        }

        composable(route = Screen.AddGoal.route) {
            AddGoalScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = Screen.History.route) {
            HistoryScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}