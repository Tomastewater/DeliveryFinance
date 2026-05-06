package com.tomastewater.deliveryfinance.presentation.commitments.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.tomastewater.deliveryfinance.ui.theme.PrimaryBlue
import com.tomastewater.deliveryfinance.ui.theme.SecondaryGreen

@Composable
fun DynamicCommitmentFab(
    selectedTab: Int,
    onClick: () -> Unit
) {
    // 1. Lógica de animación para mostrar/ocultar el FAB (se oculta en Historial que es el tab 2)
    AnimatedVisibility(
        visible = selectedTab != 2,
        enter = scaleIn(animationSpec = tween(300)),
        exit = scaleOut(animationSpec = tween(300))
    ) {
        // 2. Transición suave de color entre el tab 0 y el tab 1
        val targetColor = if (selectedTab == 0) PrimaryBlue else SecondaryGreen
        val animatedColor by animateColorAsState(
            targetValue = targetColor,
            animationSpec = tween(300),
            label = "fabColor"
        )

        // 3. Texto dinámico
        val text = if (selectedTab == 0) "Nueva Deuda" else "Nuevo Cobro"

        ExtendedFloatingActionButton(
            onClick = onClick,
            containerColor = animatedColor,
            contentColor = Color.White,
            icon = { Icon(Icons.Default.Add, contentDescription = text) },
            text = { Text(text) }
        )
    }
}