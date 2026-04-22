package com.tomastewater.deliveryfinance.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomastewater.deliveryfinance.domain.model.Goal
import com.tomastewater.deliveryfinance.domain.model.Transaction
import com.tomastewater.deliveryfinance.domain.model.TransactionType
import com.tomastewater.deliveryfinance.domain.repository.FixedExpenseRepository
import com.tomastewater.deliveryfinance.domain.usecase.balance.GetAvailableBalanceUseCase
import com.tomastewater.deliveryfinance.domain.usecase.goal.CalculateGoalTimeUseCase
import com.tomastewater.deliveryfinance.domain.usecase.goal.CompleteGoalUseCase
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
    private val fixedExpenseRepository: FixedExpenseRepository,
    private val completeGoalUseCase: CompleteGoalUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardUiState())
    val state: StateFlow<DashboardUiState> = _state.asStateFlow()

    init {
        loadDashboardData() // Solo llamamos a una función unificada
    }

    private fun loadDashboardData() {
        // 1. Escuchar Saldo
        getAvailableBalanceUseCase()
            .onEach { balance -> _state.update { it.copy(totalBalance = balance) } }
            .launchIn(viewModelScope)

        // 2. Escuchar Transacciones (Historial y sumas diarias)
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

        // 3. Escuchar la Meta Activa (ESTO GARANTIZA QUE EL BOTÓN '+' APAREZCA)
        getActiveGoalUseCase()
            .onEach { goal ->
                _state.update { it.copy(activeGoal = goal) }
            }
            .launchIn(viewModelScope)

        // 4. El Algoritmo Predictivo (Matemática pura)
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
                            weeksToGoal = result.weeks,
                            savingsCapacity = result.savingsCapacity
                        )
                        is ProjectionResult.Negative -> currentState.copy(
                            weeksToGoal = -1,
                            savingsCapacity = result.deficit
                        )
                    }
                }
            } else {
                // Si no hay meta, limpiamos la proyección matemática
                _state.update { it.copy(weeksToGoal = null, savingsCapacity = 0.0) }
            }
        }.launchIn(viewModelScope)
    }

    private fun calculateDaily(list: List<Transaction>, type: TransactionType): Double {
        // Por ahora sumamos todo, en el futuro filtraremos por "hoy"
        return list.filter { it.type == type }.sumOf { it.amount }
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

    // --- ACCIONES DE METAS ---
    fun onCompleteGoal(goal: Goal) {
        viewModelScope.launch {
            completeGoalUseCase(goal)
            // Al completarse, el getActiveGoalUseCase emitirá null y la tarjeta volverá a su estado vacío
        }
    }

    fun onDeleteGoal(goal: Goal) {
        viewModelScope.launch {
            // Aquí llamarías a tu deleteGoalUseCase(goal)
        }
    }

}