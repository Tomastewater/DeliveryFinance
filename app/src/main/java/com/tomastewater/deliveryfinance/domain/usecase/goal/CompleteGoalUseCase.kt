package com.tomastewater.deliveryfinance.domain.usecase.goal

import com.tomastewater.deliveryfinance.domain.model.Goal
import com.tomastewater.deliveryfinance.domain.repository.GoalRepository
import javax.inject.Inject

class CompleteGoalUseCase @Inject constructor(
    private val repository: GoalRepository
) {
    suspend operator fun invoke(goal: Goal) {
        // Marcamos la meta como completada e igualamos lo ahorrado al objetivo
        val completedGoal = goal.copy(
            isCompleted = true,
            savedAmount = goal.targetAmount
        )
        // El OnConflictStrategy.REPLACE en el DAO hará que se actualice
        repository.saveGoal(completedGoal)
    }
}