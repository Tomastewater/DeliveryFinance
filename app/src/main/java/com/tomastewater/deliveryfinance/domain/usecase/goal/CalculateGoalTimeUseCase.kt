package com.tomastewater.deliveryfinance.domain.usecase.goal

import com.tomastewater.deliveryfinance.domain.model.Goal
import com.tomastewater.deliveryfinance.domain.model.Transaction
import com.tomastewater.deliveryfinance.domain.model.TransactionType
import com.tomastewater.deliveryfinance.data.local.entity.FixedExpenseEntity
import javax.inject.Inject

class CalculateGoalTimeUseCase @Inject constructor() {

    operator fun invoke(
        activeGoal: Goal,
        transactions: List<Transaction>,
        fixedExpenses: List<FixedExpenseEntity>
    ): ProjectionResult {

        // 1. Calcular ingreso semanal promedio (simplificado para el MVP)
        val weeklyIncome = transactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }

        // 2. Calcular gastos variables semanales
        val weeklyVariableExpenses = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

        // 3. Sumar gastos fijos (ajustados a semana)
        val weeklyFixedExpenses = fixedExpenses.sumOf { expense ->
            if (expense.frequency == "MONTHLY") expense.amount / 4 else expense.amount
        }

        // 4. Capacidad de ahorro real
        val weeklySavingsCapacity = weeklyIncome - weeklyVariableExpenses - weeklyFixedExpenses

        // 5. Cálculo de tiempo
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
}