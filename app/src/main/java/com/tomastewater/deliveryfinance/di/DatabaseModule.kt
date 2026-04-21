package com.tomastewater.deliveryfinance.di

import android.app.Application
import androidx.room.Room
import com.tomastewater.deliveryfinance.data.local.DeliveryFinanceDatabase
import com.tomastewater.deliveryfinance.data.local.dao.FixedExpenseDao
import com.tomastewater.deliveryfinance.data.local.dao.GoalDao
import com.tomastewater.deliveryfinance.data.local.dao.TransactionDao
import com.tomastewater.deliveryfinance.data.repository.FixedExpenseRepositoryImpl
import com.tomastewater.deliveryfinance.data.repository.GoalRepositoryImpl
import com.tomastewater.deliveryfinance.data.repository.TransactionRepositoryImpl
import com.tomastewater.deliveryfinance.domain.repository.FixedExpenseRepository
import com.tomastewater.deliveryfinance.domain.repository.GoalRepository
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
            ).fallbackToDestructiveMigration(true) // Por ahora hay que dejarla en True
            .build()
    }

    @Provides
    @Singleton
    fun provideTransactionDao(db: DeliveryFinanceDatabase): TransactionDao {
        return db.transactionDao
    }

    @Provides
    @Singleton
    fun provideTransactionRepository(dao: TransactionDao): TransactionRepository {
        return TransactionRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideFixedExpenseDao(db: DeliveryFinanceDatabase): FixedExpenseDao {
        return db.fixedExpenseDao
    }

    @Provides
    @Singleton
    fun provideFixedExpenseRepository(dao: FixedExpenseDao): FixedExpenseRepository {
        return FixedExpenseRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideGoalDao(db: DeliveryFinanceDatabase): GoalDao {
        return db.goalDao
    }

    @Provides
    @Singleton
    fun provideGoalRepository(dao: GoalDao): GoalRepository {
        return GoalRepositoryImpl(dao)
    }

}