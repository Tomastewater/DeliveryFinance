package com.tomastewater.deliveryfinance.presentation.dashboard.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.tomastewater.deliveryfinance.ui.theme.PrimaryBlue
import com.tomastewater.deliveryfinance.ui.theme.TextMuted

// Rutas rápidas para la barra
enum class BottomNavItem(val title: String, val icon: ImageVector, val route: String) {
    Dashboard("Inicio", Icons.Default.Dashboard, "dashboard_route"),
    Movements("Historial", Icons.Default.ReceiptLong, "history_route"),
    Goals("Metas", Icons.Default.Star, "goal_history_route"),
    Stats("Estadísticas", Icons.Default.Analytics, "stats_route")
}

@Composable
fun DeliveryBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        contentColor = PrimaryBlue
    ) {
        BottomNavItem.values().forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryBlue,
                    selectedTextColor = PrimaryBlue,
                    indicatorColor = PrimaryBlue.copy(alpha = 0.1f),
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted
                )
            )
        }
    }
}