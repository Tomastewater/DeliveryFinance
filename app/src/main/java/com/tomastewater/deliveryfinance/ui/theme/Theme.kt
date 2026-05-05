package com.tomastewater.deliveryfinance.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable



private val DeliveryFinanceColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = CardSurface,
    primaryContainer = PrimaryLight,
    secondary = IncomeGreen,
    secondaryContainer = SecondaryContainer,
    background = BackgroundGray,
    onBackground = TextDark,
    surface = CardSurface,
    onSurface = TextDark,
    error = ExpenseRed,
    errorContainer = WarningBackground,
    onErrorContainer = WarningText
)

@Composable
fun DeliveryFinanceTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DeliveryFinanceColorScheme,
        // typography = Typography, // Descomenta esto si tienes configurado Type.kt
        content = content
    )
}