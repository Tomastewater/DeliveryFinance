package com.tomastewater.deliveryfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tomastewater.deliveryfinance.data.local.entity.FixedExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FixedExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFixedExpense(expense: FixedExpenseEntity)

    @Query("SELECT * FROM fixed_expenses ORDER BY createdAt DESC")
    fun getAllFixedExpenses(): Flow<List<FixedExpenseEntity>>

    @Delete
    suspend fun deleteFixedExpense(expense: FixedExpenseEntity)
}