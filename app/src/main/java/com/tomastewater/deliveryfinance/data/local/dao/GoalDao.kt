package com.tomastewater.deliveryfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.tomastewater.deliveryfinance.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity)

    @Query("SELECT * FROM goals WHERE isCompleted = 0 ORDER BY id DESC")
    fun getActiveGoals(): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals WHERE isCompleted = 1 ORDER BY createdAt DESC")
    fun getCompletedGoals(): Flow<List<GoalEntity>>

    @Delete
    suspend fun deleteGoal(goal: GoalEntity)

    @Query("UPDATE goals SET savedAmount = :amount WHERE id = :goalId")
    suspend fun updateProgress(goalId: Long, amount: Double)

    // 1. Le quita el estado 'Principal' a todas las metas
    @Query("UPDATE goals SET isPrincipal = 0")
    suspend fun clearPrincipalGoals()

    // 2. Le pone el estado 'Principal' a una meta específica
    @Query("UPDATE goals SET isPrincipal = 1 WHERE id = :goalId")
    suspend fun setAsPrincipal(goalId: Long)

    // 3. Transacción segura que agrupa ambos pasos
    @Transaction
    suspend fun updatePrincipalGoal(goalId: Long) {
        clearPrincipalGoals()
        setAsPrincipal(goalId)
    }
}