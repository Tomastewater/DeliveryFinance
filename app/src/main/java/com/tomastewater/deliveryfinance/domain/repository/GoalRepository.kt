package com.tomastewater.deliveryfinance.domain.repository

import com.tomastewater.deliveryfinance.domain.model.Goal
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    fun getActiveGoals(): Flow<List<Goal>>
    fun getCompletedGoals(): Flow<List<Goal>>
    suspend fun saveGoal(goal: Goal)
    suspend fun deleteGoal(goal: Goal)
    suspend fun updateGoalProgress(goalId: Long, amount: Double)
}