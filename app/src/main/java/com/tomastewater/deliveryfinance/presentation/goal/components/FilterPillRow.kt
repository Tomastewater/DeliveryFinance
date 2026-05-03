package com.tomastewater.deliveryfinance.presentation.goal.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FilterPillRow(isShowingActive: Boolean, onFilterChanged: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(24.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(if (isShowingActive) Color(0xFF1E3A8A) else Color.Transparent)
                .clickable { onFilterChanged(true) }
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("En curso", color = if (isShowingActive) Color.White else Color(0xFF757682), fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(if (!isShowingActive) Color(0xFF1E3A8A) else Color.Transparent)
                .clickable { onFilterChanged(false) }
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Finalizadas", color = if (!isShowingActive) Color.White else Color(0xFF757682), fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}