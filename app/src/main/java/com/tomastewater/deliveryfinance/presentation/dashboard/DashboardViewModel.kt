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
import com.tomastewater.deliveryfinance.domain.usecase.goal.GetActiveGoalsUseCase
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
    private val getActiveGoalsUseCase: GetActiveGoalsUseCase,
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
                _state.update {
                    it.copy(
                        transactions = list,
                        dailyIncome = calculateDaily(list, TransactionType.INCOME),
                        dailyExpenses = calculateDaily(list, TransactionType.EXPENSE),
                        isLoading = false
                    )
                }
            }
            .launchIn(viewModelScope)

        // 3. Escuchar la Meta Activa (ESTO GARANTIZA QUE EL BOTÓN '+' APAREZCA)
        getActiveGoalsUseCase()
            .onEach { goals ->
                _state.update { it.copy(activeGoals = goals) }
            }
            .launchIn(viewModelScope)

        // 4. El Algoritmo Predictivo (Matemática pura)
        combine(
            getActiveGoalsUseCase(),
            getTransactionsUseCase(),
            fixedExpenseRepository.getFixedExpenses()
        ) { goals, txs, fixed ->
            // Si no hay metas, limpiamos las predicciones
            if (goals.isEmpty()) {
                _state.update { it.copy(goalPredictions = emptyMap(), savingsCapacity = 0.0) }
                return@combine
            }

            // Si hay metas, calculamos cuánto falta para CADA UNA
            val predictionsMap = mutableMapOf<Long, Int>()
            var generalSavingsCapacity = 0.0

            goals.forEach { goal ->
                val result = calculateGoalTimeUseCase(goal, txs, fixed)
                when (result) {
                    is ProjectionResult.Success -> {
                        predictionsMap[goal.id] = result.weeks
                        generalSavingsCapacity =
                            result.savingsCapacity // Capacidad de ahorro general
                    }

                    is ProjectionResult.Negative -> {
                        predictionsMap[goal.id] = -1 // Código para "Ritmo insuficiente"
                        generalSavingsCapacity = result.deficit
                    }

                    else -> {}
                }
            }

            _state.update { currentState ->
                currentState.copy(
                    goalPredictions = predictionsMap,
                    savingsCapacity = generalSavingsCapacity
                )
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