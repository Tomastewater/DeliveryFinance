package com.tomastewater.deliveryfinance.domain.usecase.goal

import com.tomastewater.deliveryfinance.domain.repository.GoalRepository
import jakarta.inject.Inject

class GetCompletedGoalsUseCase @Inject constructor(
    private val repository: GoalRepository
) {
    operator fun invoke() = repository.getCompletedGoals()
}