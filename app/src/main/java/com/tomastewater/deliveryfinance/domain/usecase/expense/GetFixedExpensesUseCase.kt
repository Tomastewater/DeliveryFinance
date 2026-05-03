package com.tomastewater.deliveryfinance.domain.usecase.expense

import com.tomastewater.deliveryfinance.data.local.entity.FixedExpenseEntity
import com.tomastewater.deliveryfinance.domain.repository.FixedExpenseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFixedExpensesUseCase @Inject constructor(
    private val repository: FixedExpenseRepository
) {
    operator fun invoke(): Flow<List<FixedExpenseEntity>> {
        return repository.getFixedExpenses()
    }
}