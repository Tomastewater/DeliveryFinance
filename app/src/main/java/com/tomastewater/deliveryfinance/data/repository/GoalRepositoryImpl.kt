package com.tomastewater.deliveryfinance.data.repository

import com.tomastewater.deliveryfinance.data.local.dao.GoalDao
import com.tomastewater.deliveryfinance.data.local.entity.GoalEntity
import com.tomastewater.deliveryfinance.domain.model.Goal
import com.tomastewater.deliveryfinance.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GoalRepositoryImpl @Inject constructor(
    private val dao: GoalDao
) : GoalRepository {

    override fun getActiveGoals(): Flow<List<Goal>> {
        return dao.getActiveGoals().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getCompletedGoals(): Flow<List<Goal>> {
        return dao.getCompletedGoals().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    // --- NUEVO: Obtener una meta específica en tiempo real ---
    override fun getGoalById(id: Long): Flow<Goal?> {
        return dao.getGoalById(id).map { entity ->
            entity?.toDomain() // Retorna la meta si la encuentra, o null si no existe
        }
    }

    override suspend fun saveGoal(goal: Goal) {
        dao.insertGoal(goal.toEntity())
    }

    override suspend fun deleteGoal(goal: Goal) {
        dao.deleteGoal(goal.toEntity())
    }

    override suspend fun updateGoalProgress(goalId: Long, amount: Double) {
        dao.updateProgress(goalId, amount)
    }

    override suspend fun setPrincipalGoal(goalId: Long) {
        dao.updatePrincipalGoal(goalId)
    }

}

// --- Mappers Actualizados ---
fun GoalEntity.toDomain(): Goal {
    return Goal(
        id = id,
        title = title,
        targetAmount = targetPrice,
        savedAmount = savedAmount,
        linkUrl = linkUrl ?: "",
        isCompleted = isCompleted,
        iconId = iconId,
        isPrincipal = isPrincipal,
        imageUrl = imageUrl,
        createdAt = createdAt
    )
}

fun Goal.toEntity(): GoalEntity {
    return GoalEntity(
        id = id,
        title = title,
        targetPrice = targetAmount,
        savedAmount = savedAmount,
        linkUrl = linkUrl,
        isCompleted = isCompleted,
        iconId = iconId,
        isPrincipal = isPrincipal,
        imageUrl = imageUrl,
        createdAt = createdAt
    )
}