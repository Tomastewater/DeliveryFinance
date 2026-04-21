package com.tomastewater.deliveryfinance.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tomastewater.deliveryfinance.data.local.dao.FixedExpenseDao
import com.tomastewater.deliveryfinance.data.local.dao.GoalDao
import com.tomastewater.deliveryfinance.data.local.dao.TransactionDao
import com.tomastewater.deliveryfinance.data.local.entity.TransactionEntity
import com.tomastewater.deliveryfinance.data.local.entity.FixedExpenseEntity
import com.tomastewater.deliveryfinance.data.local.entity.GoalEntity

@Database(
    entities = [
        TransactionEntity::class,
        GoalEntity::class,
        FixedExpenseEntity::class], // Aquí agregaremos GoalEntity y ProfileEntity después
    version = 1,
    exportSchema = false
)
abstract class DeliveryFinanceDatabase : RoomDatabase() {

    abstract val transactionDao: TransactionDao
    abstract val goalDao: GoalDao // Asegúrate de haber creado este DAO previamente
    abstract val fixedExpenseDao: FixedExpenseDao


}