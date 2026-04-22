package com.tomastewater.deliveryfinance.presentation.history

import com.tomastewater.deliveryfinance.domain.model.Goal
import com.tomastewater.deliveryfinance.domain.model.Transaction

data class HistoryUiState(
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    // Control para el "Doble Check"
    val transactionToDelete: Transaction? = null,
    val transactionToEdit: Transaction? = null,
)