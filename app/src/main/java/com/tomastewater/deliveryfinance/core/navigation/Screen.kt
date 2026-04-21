package com.tomastewater.deliveryfinance.core.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard_screen")
    object AddGoal : Screen("add_goal_screen")
    object History : Screen("history_screen")
}