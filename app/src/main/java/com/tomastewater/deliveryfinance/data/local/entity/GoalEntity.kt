package com.tomastewater.deliveryfinance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,          // Ej: "Cubierta nueva traseras"
    val targetPrice: Double,    // Cuánto cuesta
    val savedAmount: Double = 0.0, // Cuánto llevas separado específicamente para esto
    val linkUrl: String? = null,   // El link de Mercado Libre
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)