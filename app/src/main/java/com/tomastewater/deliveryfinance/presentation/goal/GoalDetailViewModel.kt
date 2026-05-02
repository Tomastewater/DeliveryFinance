package com.tomastewater.deliveryfinance.presentation.goal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomastewater.deliveryfinance.domain.model.Goal
import com.tomastewater.deliveryfinance.domain.repository.GoalRepository
import com.tomastewater.deliveryfinance.domain.usecase.balance.GetAvailableBalanceUseCase
import com.tomastewater.deliveryfinance.domain.usecase.goal.AddMoneyToGoalUseCase
import com.tomastewater.deliveryfinance.domain.usecase.goal.GetGoalByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle, // <-- Atrapa el ID de la navegación
    private val getGoalByIdUseCase: GetGoalByIdUseCase,
    private val addMoneyToGoalUseCase: AddMoneyToGoalUseCase,
    private val goalRepository: GoalRepository, // Para guardar las ediciones
    private val getAvailableBalanceUseCase: GetAvailableBalanceUseCase
) : ViewModel() {

    private val _goal = MutableStateFlow<Goal?>(null)
    val goal: StateFlow<Goal?> = _goal.asStateFlow()

    private val _availableBalance = MutableStateFlow(0.0)
    val availableBalance: StateFlow<Double> = _availableBalance.asStateFlow()

    init {
        // Obtenemos el ID de la ruta "goal_detail_route/{goalId}"
        val goalId = savedStateHandle.get<Long>("goalId") ?: 0L
        if (goalId != 0L) {
            getGoalByIdUseCase(goalId).onEach { fetchedGoal ->
                _goal.value = fetchedGoal
            }.launchIn(viewModelScope)
        }

        getAvailableBalanceUseCase().onEach { balance ->
            _availableBalance.value = balance
        }.launchIn(viewModelScope)

    }

    // Guardar los cambios hechos en "Modo Edición"
    fun updateGoal(updatedGoal: Goal) {
        viewModelScope.launch {
            goalRepository.saveGoal(updatedGoal)
        }
    }

    // Aportar dinero directamente desde el detalle
    fun addMoney(amount: Double) {
        val currentGoal = _goal.value ?: return
        viewModelScope.launch {
            addMoneyToGoalUseCase(currentGoal, amount)
        }
    }
}