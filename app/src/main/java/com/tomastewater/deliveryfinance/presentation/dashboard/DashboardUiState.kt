package com.tomastewater.deliveryfinance.presentation.dashboard

import com.tomastewater.deliveryfinance.domain.model.Goal
import com.tomastewater.deliveryfinance.domain.model.Transaction

data class DashboardUiState(
    val transactions: List<Transaction> = emptyList(),
    val totalBalance: Double = 0.0,
    val dailyIncome: Double = 0.0,
    val dailyExpenses: Double = 0.0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val savingsCapacity: Double = 0.0,
    val activeGoals: List<Goal> = emptyList(),
    val goalPredictions: Map<Long, Int> = emptyMap() // Un diccionario que une el ID de la meta con las semanas que faltan
)

