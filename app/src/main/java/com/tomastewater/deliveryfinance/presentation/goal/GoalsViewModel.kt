package com.tomastewater.deliveryfinance.presentation.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomastewater.deliveryfinance.domain.model.Goal
import com.tomastewater.deliveryfinance.domain.usecase.goal.GetActiveGoalsUseCase
import com.tomastewater.deliveryfinance.domain.usecase.goal.GetCompletedGoalsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class GoalsUiState(
    val activeGoals: List<Goal> = emptyList(),
    val completedGoals: List<Goal> = emptyList(),
    val isShowingActive: Boolean = true // true = Pestaña Activas, false = Pestaña Finalizadas
)

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val getActiveGoalsUseCase: GetActiveGoalsUseCase,
    private val getCompletedGoalsUseCase: GetCompletedGoalsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GoalsUiState())
    val state: StateFlow<GoalsUiState> = _state.asStateFlow()

    init {
        // Escuchamos ambas listas al mismo tiempo
        combine(
            getActiveGoalsUseCase(),
            getCompletedGoalsUseCase()
        ) { active, completed ->
            _state.update {
                it.copy(
                    activeGoals = active,
                    completedGoals = completed
                )
            }
        }.launchIn(viewModelScope)
    }

    // Función para cambiar de pestaña
    fun setFilter(showActive: Boolean) {
        _state.update { it.copy(isShowingActive = showActive) }
    }
}