package com.tomastewater.deliveryfinance.domain.usecase.balance

import com.tomastewater.deliveryfinance.domain.model.TransactionType
import com.tomastewater.deliveryfinance.domain.repository.FixedExpenseRepository
import com.tomastewater.deliveryfinance.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetAvailableBalanceUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val fixedExpenseRepository: FixedExpenseRepository
) {
    operator fun invoke(): Flow<Double> {
        // Combinamos los flujos de transacciones y gastos fijos en tiempo real
        return combine(
            transactionRepository.getAllTransactions(),
            fixedExpenseRepository.getFixedExpenses()
        ) { transactions, fixedExpenses ->

            val totalIncomes = transactions
                .filter { it.type == TransactionType.INCOME }
                .sumOf { it.amount }

            val totalVariableExpenses = transactions
                .filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amount }

            val totalFixedExpenses = fixedExpenses.sumOf { it.amount }

            totalIncomes - totalVariableExpenses - totalFixedExpenses
        }
    }
}