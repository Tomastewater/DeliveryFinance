package com.tomastewater.deliveryfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tomastewater.deliveryfinance.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity)

    @Query("SELECT * FROM goals WHERE isCompleted = 0 LIMIT 1")
    fun getActiveGoal(): Flow<GoalEntity?>

    @Query("SELECT * FROM goals WHERE isCompleted = 1 ORDER BY createdAt DESC")
    fun getCompletedGoals(): Flow<List<GoalEntity>>

    @Delete
    suspend fun deleteGoal(goal: GoalEntity)

    @Query("UPDATE goals SET savedAmount = :amount WHERE id = :goalId")
    suspend fun updateProgress(goalId: Long, amount: Double)
}