package com.tomastewater.deliveryfinance.domain.repository

import com.tomastewater.deliveryfinance.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
    suspend fun insertTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transaction: Transaction)
    suspend fun updateTransaction(transaction: Transaction)

    suspend fun saveTransaction(transaction: Transaction)
}