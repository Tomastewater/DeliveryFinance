package com.tomastewater.deliveryfinance.domain.usecase.goal

import com.tomastewater.deliveryfinance.domain.model.Goal
import com.tomastewater.deliveryfinance.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetActiveGoalUseCase @Inject constructor(
    private val repository: GoalRepository
) {
    operator fun invoke(): Flow<Goal?> {
        return repository.getActiveGoal()
    }
}