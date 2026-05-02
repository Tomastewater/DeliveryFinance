package com.tomastewater.deliveryfinance.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tomastewater.deliveryfinance.presentation.dashboard.DashboardScreen
import com.tomastewater.deliveryfinance.presentation.goal.AddGoalScreen
import com.tomastewater.deliveryfinance.presentation.goal.GoalsScreen
import com.tomastewater.deliveryfinance.presentation.history.HistoryScreen
import com.tomastewater.deliveryfinance.presentation.transaction.AddTransactionScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.tomastewater.deliveryfinance.presentation.goal.GoalDetailScreen

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
                onNavigateToAddTransaction = { type ->
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
            GoalsScreen(
                onNavigateToAddGoal = { navController.navigate(Screen.AddGoal.route) },
                onNavigateToDashboard = {
                    // popUpTo evita que el usuario acumule 100 pantallas de Dashboard al ir y volver
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                },
                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                onAddMoneyClick = { goal ->
                    // TODO: Ticket FIN-404 (Aportar dinero a la meta)
                },
                onNavigateToGoalDetail = { goalId ->
                    navController.navigate("goal_detail_route/$goalId")
                }
            )
        }

        composable(
            route = "goal_detail_route/{goalId}",
            arguments = listOf(navArgument("goalId") { type = NavType.LongType })
        ) {
            GoalDetailScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable(route = Screen.AddTransaction.route) { backStackEntry ->
            AddTransactionScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}