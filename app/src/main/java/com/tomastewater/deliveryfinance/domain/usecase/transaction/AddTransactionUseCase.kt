package com.tomastewater.deliveryfinance.domain.usecase.transaction

import com.tomastewater.deliveryfinance.domain.model.Transaction
import com.tomastewater.deliveryfinance.domain.repository.TransactionRepository
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction) {
        // Aquí podrías agregar validaciones en el futuro
        // Ej: if (transaction.amount <= 0) throw Exception("Monto inválido")
        repository.insertTransaction(transaction)
    }
}