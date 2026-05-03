package com.tomastewater.deliveryfinance.presentation.goal.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 1. Creamos una estructura de datos para separar el título del consejo
data class SavingsTip(val title: String, val description: String)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SavingsTipsCarousel(modifier: Modifier = Modifier) {
    // 2. La lista ahora usa la nueva estructura (mucho más limpio)
    val allTips = listOf(
        SavingsTip("Regla 50/30/20", "Destina 50% a necesidades, 30% a gustos y 20% a ahorro e inversión."),
        SavingsTip("Mantenimiento preventivo", "Cuidar tu vehículo o moto de trabajo hoy, te ahorra miles mañana."),
        SavingsTip("Audita tus suscripciones", "Revisa mensualmente tus pagos digitales y cancela las apps premium que ya no uses."),
        SavingsTip("Gastos hormiga", "Ese café diario o snacks en la calle suman una gran cantidad al final del mes."),
        SavingsTip("Diversifica tus ahorros", "Utilizar activos digitales o stablecoins te ayuda a proteger el valor de tu dinero."),
        SavingsTip("Págate a ti mismo primero", "Apenas cobres, separa tu porcentaje de ahorro antes de empezar a gastar."),
        SavingsTip("Fondo de emergencia", "Intenta ahorrar de a poco el equivalente a 3 meses de tus gastos fijos."),
        SavingsTip("Regla de las 24 horas", "Si quieres comprar algo que no es urgente, espera un día. Evitarás compras impulsivas."),
        SavingsTip("Compara antes de comprar", "Dedicar unos minutos a buscar precios online puede darte un ahorro significativo."),
        SavingsTip("Invierte en ti", "El mejor retorno de inversión siempre será educarte y aprender nuevas herramientas.")
    )

    val dailyTips = remember { allTips.shuffled().take(3) }
    val lazyListState = rememberLazyListState()

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Tips para ti",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E3A8A), // PrimaryBlue
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        )

        LazyRow(
            state = lazyListState,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(dailyTips) { tip ->
                TipCard(tip)
            }
        }
    }
}

@Composable
fun TipCard(tip: SavingsTip) {
    Card(
        modifier = Modifier
            .width(300.dp) // Un poco más ancha para mayor legibilidad
            .wrapContentHeight(), // <-- MAGIA: Crece según el tamaño del texto
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp) // Padding interno uniforme
        ) {
            // Fila superior: Ícono + Título
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFFEF3C7), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Tip",
                        tint = Color(0xFFD97706),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = tip.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF92400E) // Ámbar oscuro para el título
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Texto descriptivo ocupando todo el ancho
            Text(
                text = tip.description,
                fontSize = 14.sp,
                color = Color(0xFF4B5563),
                lineHeight = 20.sp // Interlineado para mejor lectura
            )
        }
    }
}