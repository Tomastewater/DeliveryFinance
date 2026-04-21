package com.tomastewater.deliveryfinance.data.repository

import com.tomastewater.deliveryfinance.data.local.dao.FixedExpenseDao
import com.tomastewater.deliveryfinance.data.local.entity.FixedExpenseEntity
import com.tomastewater.deliveryfinance.domain.repository.FixedExpenseRepository

class FixedExpenseRepositoryImpl(
    private val dao: FixedExpenseDao
) : FixedExpenseRepository {
    override fun getFixedExpenses() = dao.getAllFixedExpenses()
    override suspend fun saveFixedExpense(expense: FixedExpenseEntity) = dao.insertFixedExpense(expense)
    override suspend fun removeFixedExpense(expense: FixedExpenseEntity) = dao.deleteFixedExpense(expense)
}