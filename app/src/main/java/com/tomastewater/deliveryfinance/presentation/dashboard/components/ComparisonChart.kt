package com.tomastewater.deliveryfinance.presentation.dashboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun ComparisonChart(
    income: Double,
    expenses: Double,
    modifier: Modifier = Modifier
) {
    val total = income + expenses
    // Calculamos el ángulo proporcional para los gastos (360 grados base)
    val expenseAngle = if (total > 0) (expenses.toFloat() / total.toFloat()) * 360f else 0f
    val incomeAngle = 360f - expenseAngle

    // Colores del Design System
    val incomeColor = MaterialTheme.colorScheme.primary
    val expenseColor = MaterialTheme.colorScheme.secondary

    Box(
        modifier = modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 25.dp.toPx()

            // Arco de Gastos
            drawArc(
                color = expenseColor,
                startAngle = -90f,
                sweepAngle = expenseAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Arco de Ingresos
            drawArc(
                color = incomeColor,
                startAngle = -90f + expenseAngle,
                sweepAngle = incomeAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        // Texto central: Eficiencia (Ahorro / Ingresos)
        val savings = income - expenses
        val efficiency = if (income > 0) (savings / income * 100).toInt() else 0

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$efficiency%",
                style = MaterialTheme.typography.headlineMedium,
                color = incomeColor
            )
            Text(
                text = "Libre",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}