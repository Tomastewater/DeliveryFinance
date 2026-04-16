package com.tomastewater.deliveryfinance.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.darkColorScheme

import androidx.compose.ui.graphics.Color

private val ZenColorScheme = darkColorScheme(
    primary = MutedOlive,
    secondary = SteelSlate,
    background = DarkBackground,
    surface = SurfaceGrey,
    onPrimary = Color.White,
    onBackground = SageGreyText,
    onSurface = Color.White,
    // Puedes agregar más roles si lo deseas
    primaryContainer = MutedOlive.copy(alpha = 0.2f),
    secondaryContainer = SteelSlate.copy(alpha = 0.2f)
)

@Composable
fun DeliveryFinanceTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ZenColorScheme,
        typography = Typography, // Asegúrate de tener definido el objeto Typography
        content = content
    )
}