package com.tomastewater.deliveryfinance.domain.usecase.transaction

import com.tomastewater.deliveryfinance.domain.model.Transaction
import com.tomastewater.deliveryfinance.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    // El operador 'invoke' permite llamar a la clase como si fuera una función
    operator fun invoke(): Flow<List<Transaction>> {
        return repository.getAllTransactions()
    }
}