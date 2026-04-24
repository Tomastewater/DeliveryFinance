package com.tomastewater.deliveryfinance.data.repository

import com.tomastewater.deliveryfinance.data.local.dao.TransactionDao
import com.tomastewater.deliveryfinance.data.local.entity.TransactionEntity
import com.tomastewater.deliveryfinance.domain.model.Transaction
import com.tomastewater.deliveryfinance.domain.model.TransactionType
import com.tomastewater.deliveryfinance.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepositoryImpl(
    private val dao: TransactionDao
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return dao.getAllTransactions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertTransaction(transaction: Transaction) {
        dao.insertTransaction(transaction.toEntity())
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        dao.deleteTransaction(transaction.toEntity())
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        dao.updateTransaction(transaction.toEntity())
    }

    override suspend fun saveTransaction(transaction: Transaction) {
        TODO("Not yet implemented")
    }
}

// --- Funciones de Mapeo (Mappers) ---
// Normalmente se ponen en un archivo separado en `data/mapper`,
// pero las dejo aquí para que veas la lógica completa.

fun TransactionEntity.toDomain(): Transaction {
    return Transaction(
        id = id,
        amount = amount,
        type = TransactionType.valueOf(type),
        category = category,
        timestamp = timestamp,
        note = note
    )
}

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        amount = amount,
        type = type.name, // Guarda "INCOME" o "EXPENSE" como String en la BD
        category = category,
        timestamp = timestamp,
        note = note
    )
}