package com.tomastewater.deliveryfinance.di

import android.app.Application
import androidx.room.Room
import com.tomastewater.deliveryfinance.data.local.DeliveryFinanceDatabase
import com.tomastewater.deliveryfinance.data.local.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Le dice a Hilt que esto vivirá durante todo el ciclo de la app
object DatabaseModule {

    @Provides
    @Singleton // Asegura que solo exista UNA instancia de la base de datos
    fun provideDatabase(app: Application): DeliveryFinanceDatabase {
        return Room.databaseBuilder(
            app,
            DeliveryFinanceDatabase::class.java,
            "delivery_finance_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideTransactionDao(db: DeliveryFinanceDatabase): TransactionDao {
        return db.transactionDao
    }
}