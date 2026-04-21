package com.tomastewater.deliveryfinance.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomastewater.deliveryfinance.domain.model.Transaction
import com.tomastewater.deliveryfinance.domain.model.TransactionType
import com.tomastewater.deliveryfinance.domain.usecase.balance.GetAvailableBalanceUseCase
import com.tomastewater.deliveryfinance.domain.usecase.transaction.AddTransactionUseCase
import com.tomastewater.deliveryfinance.domain.usecase.transaction.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val getAvailableBalanceUseCase: GetAvailableBalanceUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardUiState())
    val state: StateFlow<DashboardUiState> = _state.asStateFlow()

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        _state.update { it.copy(isLoading = true) }

        // Observamos el Flow del Caso de Uso
        getTransactionsUseCase()
            .onEach { transactions ->
                _state.update { currentState ->
                    currentState.copy(
                        transactions = transactions,
                        totalBalance = calculateBalance(transactions),
                        dailyIncome = calculateDaily(transactions, TransactionType.INCOME),
                        dailyExpenses = calculateDaily(transactions, TransactionType.EXPENSE),
                        isLoading = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadDashboardData() {
        // Observamos el saldo disponible calculado
        getAvailableBalanceUseCase()
            .onEach { balance ->
                _state.update { it.copy(totalBalance = balance) }
            }
            .launchIn(viewModelScope)

        // ... (carga de transacciones)
    }

    // Función rápida para agregar un ingreso o gasto desde el Dashboard
    fun onAddQuickTransaction(amount: Double, type: TransactionType, category: String) {
        viewModelScope.launch {
            val newTransaction = Transaction(
                amount = amount,
                type = type,
                category = category,
                timestamp = System.currentTimeMillis()
            )
            addTransactionUseCase(newTransaction)
        }
    }

    // Lógica simple de cálculo (esto podría ir en un UseCase si se vuelve complejo)
    private fun calculateBalance(list: List<Transaction>): Double {
        return list.sumOf { if (it.type == TransactionType.INCOME) it.amount else -it.amount }
    }

    private fun calculateDaily(list: List<Transaction>, type: TransactionType): Double {
        // Por ahora sumamos todo, luego filtraremos por la fecha de "hoy"
        return list.filter { it.type == type }.sumOf { it.amount }
    }
}