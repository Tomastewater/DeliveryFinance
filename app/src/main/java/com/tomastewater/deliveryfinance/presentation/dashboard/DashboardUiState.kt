package com.tomastewater.deliveryfinance.presentation.dashboard

import com.tomastewater.deliveryfinance.domain.model.Goal
import com.tomastewater.deliveryfinance.domain.model.Transaction

data class DashboardUiState(
    val transactions: List<Transaction> = emptyList(),
    val activeGoal: Goal? = null,
    val totalBalance: Double = 0.0,
    val dailyIncome: Double = 0.0,
    val dailyExpenses: Double = 0.0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

