package com.tomastewater.deliveryfinance.domain.usecase.goal

import com.tomastewater.deliveryfinance.domain.model.Goal
import com.tomastewater.deliveryfinance.domain.model.Transaction
import com.tomastewater.deliveryfinance.domain.model.TransactionType
import com.tomastewater.deliveryfinance.data.local.entity.FixedExpenseEntity
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CalculateGoalTimeUseCase @Inject constructor() {

    operator fun invoke(
        activeGoal: Goal,
        transactions: List<Transaction>,
        fixedExpenses: List<FixedExpenseEntity>
    ): ProjectionResult {

        // 1. Verificación de Datos Insuficientes
        if (transactions.isEmpty()) {
            return ProjectionResult.InsufficientData
        }

        // 2. Calcular cuántas semanas de historia tenemos
        val oldestTransactionTime = transactions.minOf { it.timestamp }
        val currentTime = System.currentTimeMillis()
        val daysActive = TimeUnit.MILLISECONDS.toDays(currentTime - oldestTransactionTime)

        // Si tenemos menos de 7 días de uso en la app, no podemos calcular un promedio semanal real
        if (daysActive < 7) {
            return ProjectionResult.InsufficientData
        }

        val weeksActive = daysActive / 7.0

        // 3. Promedios REALES divididos por el tiempo de uso
        val totalIncome = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val totalVariableExpenses = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

        val weeklyIncome = totalIncome / weeksActive
        val weeklyVariableExpenses = totalVariableExpenses / weeksActive

        // 4. Sumar gastos fijos (Dividimos entre 4.33 porque un mes tiene en promedio 4.33 semanas, no 4)
        val weeklyFixedExpenses = fixedExpenses.sumOf { expense ->
            if (expense.frequency == "MONTHLY") expense.amount / 4.33 else expense.amount
        }

        // 5. Capacidad de ahorro real
        val weeklySavingsCapacity = weeklyIncome - weeklyVariableExpenses - weeklyFixedExpenses

        // 6. Cálculo de tiempo
        val remainingAmount = activeGoal.targetAmount - activeGoal.savedAmount

        return if (weeklySavingsCapacity <= 0) {
            ProjectionResult.Negative(weeklySavingsCapacity)
        } else {
            val weeksNeeded = Math.ceil(remainingAmount / weeklySavingsCapacity).toInt()
            ProjectionResult.Success(weeksNeeded, weeklySavingsCapacity)
        }
    }
}

sealed class ProjectionResult {
    data class Success(val weeks: Int, val savingsCapacity: Double) : ProjectionResult()
    data class Negative(val deficit: Double) : ProjectionResult()
    object InsufficientData : ProjectionResult() // <-- TU NUEVO ESTADO
}