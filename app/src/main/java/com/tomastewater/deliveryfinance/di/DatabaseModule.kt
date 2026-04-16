package com.tomastewater.deliveryfinance.di

import android.app.Application
import androidx.room.Room
import com.tomastewater.deliveryfinance.data.local.DeliveryFinanceDatabase
import com.tomastewater.deliveryfinance.data.local.dao.TransactionDao
import com.tomastewater.deliveryfinance.data.repository.TransactionRepositoryImpl
import com.tomastewater.deliveryfinance.domain.repository.TransactionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): DeliveryFinanceDatabase {
        return Room.databaseBuilder(
                app,
                DeliveryFinanceDatabase::class.java,
                "delivery_finance_db"
            ).fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    @Singleton
    fun provideTransactionDao(db: DeliveryFinanceDatabase): TransactionDao {
        return db.transactionDao
    }

    // --- NUEVO: Proveer el Repositorio ---
    @Provides
    @Singleton
    fun provideTransactionRepository(dao: TransactionDao): TransactionRepository {
        return TransactionRepositoryImpl(dao)
    }
}