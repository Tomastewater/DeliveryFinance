package com.tomastewater.deliveryfinance.domain.usecase.goal

import android.util.Log
import com.tomastewater.deliveryfinance.domain.model.Goal
import com.tomastewater.deliveryfinance.domain.model.Transaction
import com.tomastewater.deliveryfinance.domain.model.TransactionType
import com.tomastewater.deliveryfinance.domain.repository.GoalRepository
import com.tomastewater.deliveryfinance.domain.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddMoneyToGoalUseCase @Inject constructor(
    private val goalRepository: GoalRepository,
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(goal: Goal, amountToAdd: Double) {
        withContext(Dispatchers.IO) {
            try {
                // 1. SUMA MATEMÁTICA (Resuelve el problema de que se reemplazaba el monto)
                val newTotalSaved = goal.savedAmount + amountToAdd

                // 2. VERIFICACIÓN DE FINALIZADA
                val isNowCompleted = newTotalSaved >= goal.targetAmount

                // 3. GUARDAMOS LA META CON EL MONTO SUMADO
                val updatedGoal = goal.copy(
                    savedAmount = newTotalSaved,
                    isCompleted = isNowCompleted
                )
                goalRepository.saveGoal(updatedGoal)

                // 4. CREAMOS LA TRANSACCIÓN
                val transaction = Transaction(
                    id = 0,
                    amount = amountToAdd,
                    type = TransactionType.EXPENSE,
                    category = "Ahorro: ${goal.title}",
                    timestamp = System.currentTimeMillis(),
                    note = "Aporte a meta"
                )

                transactionRepository.saveTransaction(transaction)

            } catch (e: Exception) {
                Log.e("AddMoneyUseCase", "Error: ${e.message}")
            }
        }
    }
}