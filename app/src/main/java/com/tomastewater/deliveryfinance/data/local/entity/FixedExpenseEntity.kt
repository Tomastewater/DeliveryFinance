package com.tomastewater.deliveryfinance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fixed_expenses")
data class FixedExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val amount: Double,
    val frequency: String, // "WEEKLY" o "MONTHLY"
    val createdAt: Long = System.currentTimeMillis()
)