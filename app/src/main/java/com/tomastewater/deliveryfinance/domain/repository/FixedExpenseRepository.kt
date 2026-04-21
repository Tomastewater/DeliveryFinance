package com.tomastewater.deliveryfinance.domain.repository

import com.tomastewater.deliveryfinance.data.local.entity.FixedExpenseEntity
import kotlinx.coroutines.flow.Flow

interface FixedExpenseRepository {
    fun getFixedExpenses(): Flow<List<FixedExpenseEntity>>
    suspend fun saveFixedExpense(expense: FixedExpenseEntity)
    suspend fun removeFixedExpense(expense: FixedExpenseEntity)
}