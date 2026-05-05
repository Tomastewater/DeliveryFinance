package com.tomastewater.deliveryfinance.presentation.commitments.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomastewater.deliveryfinance.presentation.commitments.Commitment
import com.tomastewater.deliveryfinance.presentation.commitments.CommitmentType
import com.tomastewater.deliveryfinance.ui.theme.*

@Composable
fun CommitmentCard(commitment: Commitment) {
    // Definimos colores e iconos según el estado y tipo
    val isCompleted = commitment.isCompleted
    val isToPay = commitment.type == CommitmentType.TO_PAY

    val amountColor = if (isCompleted) TextMuted else if (isToPay) ExpenseRed else IncomeGreen
    val textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None

    val icon = if (isCompleted) Icons.Default.CheckCircle
    else if (isToPay) Icons.Default.ArrowDownward
    else Icons.Default.ArrowUpward

    val iconTint = if (isCompleted) TextMuted else if (isToPay) ExpenseRed else IncomeGreen
    val iconBg = if (isCompleted) Color.Transparent else iconTint.copy(alpha = 0.1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Ícono circular
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Textos
                Column {
                    Text(
                        text = commitment.title,
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) TextMuted else TextDark,
                        textDecoration = textDecoration,
                        fontSize = 16.sp
                    )
                    Text(
                        text = commitment.date,
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }

            // Monto
            Text(
                text = "$${commitment.amount.toInt()}",
                fontWeight = FontWeight.Black,
                color = amountColor,
                textDecoration = textDecoration,
                fontSize = 16.sp
            )
        }
    }
}