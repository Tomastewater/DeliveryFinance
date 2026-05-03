package com.tomastewater.deliveryfinance.presentation.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomastewater.deliveryfinance.data.local.entity.FixedExpenseEntity
import com.tomastewater.deliveryfinance.domain.model.Goal
import com.tomastewater.deliveryfinance.domain.model.Transaction
import com.tomastewater.deliveryfinance.domain.repository.GoalRepository
import com.tomastewater.deliveryfinance.domain.usecase.balance.GetAvailableBalanceUseCase
import com.tomastewater.deliveryfinance.domain.usecase.expense.GetFixedExpensesUseCase
import com.tomastewater.deliveryfinance.domain.usecase.goal.AddMoneyToGoalUseCase
import com.tomastewater.deliveryfinance.domain.usecase.goal.CalculateGoalTimeUseCase
import com.tomastewater.deliveryfinance.domain.usecase.goal.GetActiveGoalsUseCase
import com.tomastewater.deliveryfinance.domain.usecase.goal.GetCompletedGoalsUseCase
import com.tomastewater.deliveryfinance.domain.usecase.goal.ProjectionResult
import com.tomastewater.deliveryfinance.domain.usecase.transaction.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GoalsUiState(
    val activeGoals: List<Goal> = emptyList(),
    val completedGoals: List<Goal> = emptyList(),
    val isShowingActive: Boolean = true,
    val availableBalance: Double = 0.0
)

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val getActiveGoalsUseCase: GetActiveGoalsUseCase,
    private val getCompletedGoalsUseCase: GetCompletedGoalsUseCase,
    private val addMoneyToGoalUseCase: AddMoneyToGoalUseCase,
    private val goalRepository: GoalRepository,
    private val getAvailableBalanceUseCase: GetAvailableBalanceUseCase,
    private val calculateGoalTimeUseCase: CalculateGoalTimeUseCase,
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val getFixedExpensesUseCase: GetFixedExpensesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GoalsUiState())
    val state: StateFlow<GoalsUiState> = _state.asStateFlow()

    // Estado para la prediccion
    private val _principalGoalProjection = MutableStateFlow<ProjectionResult?>(null)
    val principalGoalProjection = _principalGoalProjection.asStateFlow()

    init {
        combine(
            getActiveGoalsUseCase(),
            getCompletedGoalsUseCase(),
            getAvailableBalanceUseCase()
        ) { active, completed, balance ->
            _state.update {
                it.copy(
                    activeGoals = active,
                    completedGoals = completed,
                    availableBalance = balance
                )
            }
        }.launchIn(viewModelScope)

        combine(
            getActiveGoalsUseCase(),
            getTransactionsUseCase(),
            getFixedExpensesUseCase()
        ) { activeGoals, transactions, fixedExpenses ->

            val principalGoal = activeGoals.find { it.isPrincipal }

            if (principalGoal != null) {
                calculateProjection(principalGoal, transactions, fixedExpenses)
            } else {
                _principalGoalProjection.value = null
            }

        }.launchIn(viewModelScope)

    }

    fun setFilter(showActive: Boolean) {
        _state.update { it.copy(isShowingActive = showActive) }
    }

    // 1. Aportar dinero (usa el UseCase que descuenta del saldo)
    fun addMoneyToGoal(goal: Goal, amount: Double) {
        viewModelScope.launch {
            addMoneyToGoalUseCase(goal, amount)
            // Si la meta llega a su objetivo, se marcará como completada automáticamente en la DB
        }
    }

    // 2. Eliminar Meta
    fun deleteGoal(goal: Goal) {
        viewModelScope.launch {
            goalRepository.deleteGoal(goal)
        }
    }

    // 3. Hacer Principal
    fun makePrincipal(goal: Goal) {
        viewModelScope.launch {
            goalRepository.setPrincipalGoal(goal.id) // La transacción que creamos antes
        }
    }

    // Completa la meta rellenando el monto sin generar transacción de gasto
    fun completeGoalManually(goal: Goal) {
        viewModelScope.launch {
            val completedGoal = goal.copy(
                savedAmount = goal.targetAmount, // La llenamos al 100%
                isCompleted = true
            )
            goalRepository.saveGoal(completedGoal) // Guarda la actualización en DB
        }
    }

    private fun calculateProjection(
        principalGoal: Goal,
        transactions: List<Transaction>,
        fixedExpenses: List<FixedExpenseEntity>
    ) {
        val projection = calculateGoalTimeUseCase(principalGoal, transactions, fixedExpenses)
        _principalGoalProjection.value = projection
    }

}