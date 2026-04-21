package com.tomastewater.deliveryfinance.presentation.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomastewater.deliveryfinance.domain.model.Goal
import com.tomastewater.deliveryfinance.domain.usecase.goal.AddGoalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddGoalViewModel @Inject constructor(
    private val addGoalUseCase: AddGoalUseCase
) : ViewModel() {

    fun saveGoal(title: String, targetAmount: Double, linkUrl: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val newGoal = Goal(
                title = title,
                targetAmount = targetAmount,
                linkUrl = linkUrl.takeIf { it.isNotBlank() }
            )
            addGoalUseCase(newGoal)
            onSuccess() // Para navegar hacia atrás después de guardar
        }
    }
}