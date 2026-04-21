package com.tomastewater.deliveryfinance.data.repository

import com.tomastewater.deliveryfinance.data.local.dao.GoalDao
import com.tomastewater.deliveryfinance.data.local.entity.GoalEntity
import com.tomastewater.deliveryfinance.domain.model.Goal
import com.tomastewater.deliveryfinance.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GoalRepositoryImpl(
    private val dao: GoalDao
) : GoalRepository {

    override fun getActiveGoal(): Flow<Goal?> {
        return dao.getActiveGoal().map { entity ->
            entity?.toDomain()
        }
    }

    override fun getCompletedGoals(): Flow<List<Goal>> {
        return dao.getCompletedGoals().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveGoal(goal: Goal) {
        dao.insertGoal(goal.toEntity())
    }

    override suspend fun deleteGoal(goal: Goal) {
        dao.insertGoal(goal.toEntity().copy(isCompleted = true))
    }

    override suspend fun updateGoalProgress(goalId: Long, amount: Double) {
        dao.updateProgress(goalId, amount)
    }
}

// --- Mappers ---
fun GoalEntity.toDomain(): Goal {
    return Goal(
        id = id,
        title = title,
        targetAmount = targetPrice,
        savedAmount = savedAmount,
        linkUrl = linkUrl,
        isCompleted = isCompleted
    )
}

fun Goal.toEntity(): GoalEntity {
    return GoalEntity(
        id = id,
        title = title,
        targetPrice = targetAmount,
        savedAmount = savedAmount,
        linkUrl = linkUrl,
        isCompleted = isCompleted
    )
}