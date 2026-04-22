package com.tomastewater.deliveryfinance.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tomastewater.deliveryfinance.presentation.dashboard.DashboardScreen
import com.tomastewater.deliveryfinance.presentation.goal.AddGoalScreen
import com.tomastewater.deliveryfinance.presentation.goal.GoalHistoryScreen
import com.tomastewater.deliveryfinance.presentation.history.HistoryScreen
import com.tomastewater.deliveryfinance.presentation.transaction.AddTransactionScreen

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
                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                onNavigateToGoalHistory = { navController.navigate(Screen.GoalHistory.route) },
                onNavigateToAddTransaction = { type -> // <-- NUEVO
                    navController.navigate(Screen.AddTransaction.createRoute(type))
                }
            )
        }

        composable(route = Screen.AddGoal.route) {
            AddGoalScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = Screen.History.route) {
            HistoryScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = Screen.GoalHistory.route) {
            GoalHistoryScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = Screen.AddTransaction.route) { backStackEntry ->
            // Le pasamos el tipo de transacción al ViewModel
            AddTransactionScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}