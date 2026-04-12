package com.tomastewater.deliveryfinance.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tomastewater.deliveryfinance.data.local.dao.TransactionDao
import com.tomastewater.deliveryfinance.data.local.entity.TransactionEntity

@Database(
    entities = [TransactionEntity::class], // Aquí agregaremos GoalEntity y ProfileEntity después
    version = 1,
    exportSchema = false
)
abstract class DeliveryFinanceDatabase : RoomDatabase() {

    abstract val transactionDao: TransactionDao

    // Nota: No necesitamos companion object ni getInstance() porque Hilt se encargará de crearla.
}