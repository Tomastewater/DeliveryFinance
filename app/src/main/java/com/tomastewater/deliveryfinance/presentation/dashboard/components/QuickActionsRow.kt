package com.tomastewater.deliveryfinance.presentation.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomastewater.deliveryfinance.ui.theme.*


@Composable
fun QuickActionsRow(
    onAddIncomeClick: () -> Unit,
    onAddExpenseClick: () -> Unit,
    onNavigateToCommitments: () -> Unit,
    onNavigateToBudgets: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionItem(
            title = "Ingresos",
            icon = Icons.Default.ArrowCircleUp,
            onClick = onAddIncomeClick,
            modifier = Modifier.weight(1f)
        )
        QuickActionItem(
            title = "Gastos",
            icon = Icons.Default.ArrowCircleDown,
            onClick = onAddExpenseClick,
            modifier = Modifier.weight(1f)
        )
        QuickActionItem(
            title = "Deudas",
            icon = Icons.Default.AccountBalanceWallet,
            onClick = onNavigateToCommitments,
            modifier = Modifier.weight(1f)
        )
        QuickActionItem(
            title = "Presupuestos",
            icon = Icons.Default.PieChart,
            onClick = onNavigateToBudgets,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickActionItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color = CardSurface) // PrimaryLight con opacidad
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color(0xFF1E3A8A), // PrimaryBlue
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            color = Color(0xFF1E3A8A), // PrimaryBlue
            fontSize = 11.sp, // Tamaño ajustado para evitar saltos de línea
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}