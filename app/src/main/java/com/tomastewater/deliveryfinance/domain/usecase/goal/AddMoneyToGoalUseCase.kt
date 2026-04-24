package com.tomastewater.deliveryfinance.domain.usecase.goal

import com.tomastewater.deliveryfinance.domain.model.Goal
import com.tomastewater.deliveryfinance.domain.model.Transaction
import com.tomastewater.deliveryfinance.domain.model.TransactionType
import com.tomastewater.deliveryfinance.domain.repository.GoalRepository
import com.tomastewater.deliveryfinance.domain.repository.TransactionRepository
import javax.inject.Inject

class AddMoneyToGoalUseCase @Inject constructor(
    private val goalRepository: GoalRepository,
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(goal: Goal, amountToAdd: Double) {
        // 1. Sumar el progreso a la meta
        goalRepository.updateGoalProgress(goal.id, amountToAdd)

        // 2. Crear una transacción tipo GASTO para que se descuente del Saldo Disponible
        // y quede registrado en el Historial tal como pediste.
        val transaction = Transaction(
            amount = amountToAdd,
            type = TransactionType.EXPENSE,
            category = "Ahorro: ${goal.title}",
            timestamp = System.currentTimeMillis()
        )
        transactionRepository.saveTransaction(transaction)
    }
}