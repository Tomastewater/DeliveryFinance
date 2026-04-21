package com.tomastewater.deliveryfinance.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomastewater.deliveryfinance.domain.model.Transaction
import com.tomastewater.deliveryfinance.domain.model.TransactionType
import com.tomastewater.deliveryfinance.domain.repository.FixedExpenseRepository
import com.tomastewater.deliveryfinance.domain.usecase.balance.GetAvailableBalanceUseCase
import com.tomastewater.deliveryfinance.domain.usecase.goal.CalculateGoalTimeUseCase
import com.tomastewater.deliveryfinance.domain.usecase.goal.GetActiveGoalUseCase
import com.tomastewater.deliveryfinance.domain.usecase.goal.ProjectionResult
import com.tomastewater.deliveryfinance.domain.usecase.transaction.AddTransactionUseCase
import com.tomastewater.deliveryfinance.domain.usecase.transaction.GetTransactionsUseCase
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
class DashboardViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val getAvailableBalanceUseCase: GetAvailableBalanceUseCase,
    private val getActiveGoalUseCase: GetActiveGoalUseCase,
    private val calculateGoalTimeUseCase: CalculateGoalTimeUseCase,
    private val fixedExpenseRepository: FixedExpenseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardUiState())
    val state: StateFlow<DashboardUiState> = _state.asStateFlow()

    init {
        loadDashboardData() // Solo llamamos a una función unificada
    }

    private fun loadDashboardData() {
        // 1. Escuchar Saldo (RF-04)
        getAvailableBalanceUseCase()
            .onEach { balance -> _state.update { it.copy(totalBalance = balance) } }
            .launchIn(viewModelScope)

        // 2. Escuchar Transacciones y actualizar métricas diarias
        getTransactionsUseCase()
            .onEach { list ->
                _state.update { it.copy(
                    transactions = list,
                    dailyIncome = calculateDaily(list, TransactionType.INCOME),
                    dailyExpenses = calculateDaily(list, TransactionType.EXPENSE),
                    isLoading = false
                ) }
            }
            .launchIn(viewModelScope)

        // 3. El Algoritmo Predictivo: Combina Meta + Transacciones + Gastos Fijos
        combine(
            getActiveGoalUseCase(),
            getTransactionsUseCase(),
            fixedExpenseRepository.getFixedExpenses()
        ) { goal, txs, fixed ->
            if (goal != null) {
                val result = calculateGoalTimeUseCase(goal, txs, fixed)
                _state.update { currentState ->
                    when (result) {
                        is ProjectionResult.Success -> currentState.copy(
                            activeGoal = goal,
                            weeksToGoal = result.weeks,
                            savingsCapacity = result.savingsCapacity
                        )
                        is ProjectionResult.Negative -> currentState.copy(
                            activeGoal = goal,
                            weeksToGoal = -1, // -1 indica ritmo insuficiente
                            savingsCapacity = result.deficit
                        )
                    }
                }
            } else {
                _state.update { it.copy(activeGoal = null, weeksToGoal = null, savingsCapacity = 0.0) }
            }
        }.launchIn(viewModelScope)
    }

    // Función rápida para agregar un ingreso o gasto
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

    private fun calculateDaily(list: List<Transaction>, type: TransactionType): Double {
        // Por ahora sumamos todo, en el futuro filtraremos por "hoy"
        return list.filter { it.type == type }.sumOf { it.amount }
    }
}