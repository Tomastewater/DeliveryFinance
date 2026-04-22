package com.tomastewater.deliveryfinance.presentation.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomastewater.deliveryfinance.domain.model.Goal
import com.tomastewater.deliveryfinance.domain.usecase.goal.GetCompletedGoalsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class GoalHistoryViewModel @Inject constructor(
    private val getCompletedGoalsUseCase: GetCompletedGoalsUseCase
) : ViewModel() {

    private val _completedGoals = MutableStateFlow<List<Goal>>(emptyList())
    val completedGoals: StateFlow<List<Goal>> = _completedGoals.asStateFlow()

    init {
        getCompletedGoalsUseCase()
            .onEach { goals -> _completedGoals.value = goals }
            .launchIn(viewModelScope)
    }
}