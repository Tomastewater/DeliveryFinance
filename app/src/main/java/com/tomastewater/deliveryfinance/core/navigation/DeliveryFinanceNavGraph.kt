package com.tomastewater.deliveryfinance.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tomastewater.deliveryfinance.presentation.dashboard.DashboardScreen
import com.tomastewater.deliveryfinance.presentation.goal.AddGoalScreen

@Composable
fun DeliveryFinanceNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(route = Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToAddGoal = {
                    navController.navigate(Screen.AddGoal.route)
                }
            )
        }

        composable(route = Screen.AddGoal.route) {
            AddGoalScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}