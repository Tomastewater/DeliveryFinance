package com.tomastewater.deliveryfinance.presentation.dashboard.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomastewater.deliveryfinance.domain.model.Goal
import com.tomastewater.deliveryfinance.domain.model.Transaction
import com.tomastewater.deliveryfinance.domain.model.TransactionType
import com.tomastewater.deliveryfinance.ui.theme.*

// --- 1. TARJETA DE RESUMEN DE SALDO (Nueva) ---
@Composable
fun BalanceSummaryCard(
    totalBalance: Double,
    dailyIncome: Double,
    dailyExpenses: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Saldo disponible", color = TextMuted, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = "$${totalBalance.toInt()}",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = if (totalBalance < 0) ExpenseRed else TextDark
                    )
                }
                if (totalBalance < 0) {
                    Box(
                        modifier = Modifier
                            .background(WarningBackground, RoundedCornerShape(16.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Atención: Negativo", color = WarningText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = BackgroundGray)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Ingresos", color = TextMuted, fontSize = 12.sp)
                    Text("$${dailyIncome.toInt()}", color = IncomeGreen, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Egresos", color = TextMuted, fontSize = 12.sp)
                    Text("$${dailyExpenses.toInt()}", color = ExpenseRed, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
        }
    }
}

// --- 2. TARJETAS BENTO (Distribución y Metas) ---
@Composable
fun DistributionBentoCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(180.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Distribución", color = PrimaryBlue, fontWeight = FontWeight.Bold)
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Gráfico", color = TextMuted)
            }
        }
    }
}

@Composable
fun ActiveGoalBentoCard(
    goal: Goal?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(180.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (goal != null) {
                Column {
                    Text("Meta activa", color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(goal.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 2)
                }
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("$${goal.savedAmount.toInt()}", color = IncomeGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("$${goal.targetAmount.toInt()}", color = TextMuted, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { goal.progressPercentage },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = IncomeGreen,
                        trackColor = BackgroundGray
                    )
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("+ Crear Meta", color = TextMuted, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// --- 3. COMPONENTES DE LISTAS (Transacciones, Leyendas, etc.) ---
@Composable
fun TransactionItem(transaction: Transaction) {
    val isIncome = transaction.type == TransactionType.INCOME
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(transaction.category, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = if (isIncome) "Ingreso" else "Gasto",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = "${if (isIncome) "+" else "-"} $${transaction.amount.toInt()}",
                style = MaterialTheme.typography.titleMedium,
                color = if (isIncome) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp).background(color, shape = CircleShape))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}