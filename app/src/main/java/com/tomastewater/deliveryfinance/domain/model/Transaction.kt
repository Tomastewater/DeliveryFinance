package com.tomastewater.deliveryfinance.domain.model

data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val timestamp: Long,
    val note: String? = null
)

enum class TransactionType {
    INCOME, EXPENSE
}