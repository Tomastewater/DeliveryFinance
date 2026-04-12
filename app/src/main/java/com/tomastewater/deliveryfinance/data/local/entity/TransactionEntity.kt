package com.tomastewater.deliveryfinance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val amount: Double,
    val type: String, // Guardaremos "INCOME" (Ingreso) o "EXPENSE" (Gasto)
    val category: String, // Ej: "Delivery", "Nafta", "Repuestos"
    val timestamp: Long, // Guardaremos la fecha en milisegundos para ordenar fácil
    val note: String? = null // Opcional: "Carga en YPF", "Propina extra"
)