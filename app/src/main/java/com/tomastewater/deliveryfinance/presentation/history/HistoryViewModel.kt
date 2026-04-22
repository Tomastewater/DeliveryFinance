package com.tomastewater.deliveryfinance.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomastewater.deliveryfinance.domain.model.Transaction
import com.tomastewater.deliveryfinance.domain.usecase.goal.GetCompletedGoalsUseCase
import com.tomastewater.deliveryfinance.domain.usecase.transaction.DeleteTransactionUseCase
import com.tomastewater.deliveryfinance.domain.usecase.transaction.GetTransactionsUseCase
import com.tomastewater.deliveryfinance.domain.usecase.transaction.UpdateTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val updateTransactionUseCase: UpdateTransactionUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryUiState())
    val state: StateFlow<HistoryUiState> = _state.asStateFlow()

    init {
        observeTransactions()
    }

    private fun observeTransactions() {
        _state.update { it.copy(isLoading = true) }

        // Combinamos la base de datos con los filtros del estado local
        combine(
            getTransactionsUseCase(),
            _state
        ) { transactions, currentState ->
            transactions.filter { tx ->
                val matchesSearch = tx.category.contains(currentState.searchQuery, ignoreCase = true)
                val matchesCategory = currentState.selectedCategory == null || tx.category == currentState.selectedCategory
                matchesSearch && matchesCategory
            }
        }.onEach { filteredList ->
            _state.update { it.copy(transactions = filteredList, isLoading = false) }
        }.launchIn(viewModelScope)
    }

    // --- ACCIONES DE USUARIO ---

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    fun onCategoryFilterChange(category: String?) {
        _state.update { it.copy(selectedCategory = category) }
    }

    // Lógica del "Doble Check" para eliminar
    fun onDeleteRequest(transaction: Transaction) {
        _state.update { it.copy(transactionToDelete = transaction) }
    }

    fun onConfirmDelete() {
        _state.value.transactionToDelete?.let { transaction ->
            viewModelScope.launch {
                deleteTransactionUseCase(transaction)
                _state.update { it.copy(transactionToDelete = null) }
            }
        }
    }

    fun onDismissDelete() {
        _state.update { it.copy(transactionToDelete = null) }
    }

    // Lógica para modificar (Edición)
    fun onEditRequest(transaction: Transaction) {
        _state.update { it.copy(transactionToEdit = transaction) }
    }

    fun onDismissEdit() {
        _state.update { it.copy(transactionToEdit = null) }
    }

    fun onUpdateTransaction(updatedTransaction: Transaction) {
        viewModelScope.launch {
            updateTransactionUseCase(updatedTransaction)
            _state.update { it.copy(transactionToEdit = null) }
        }
    }



}