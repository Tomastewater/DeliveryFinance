package com.tomastewater.deliveryfinance.presentation.goal
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomastewater.deliveryfinance.domain.model.Goal
import com.tomastewater.deliveryfinance.domain.repository.GoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// 1. Agrupamos toda la información de la pantalla en un solo estado
data class AddGoalUiState(
    val title: String = "",
    val targetAmount: String = "",
    val linkUrl: String = "",
    val iconId: String = "Star",
    val isPrincipal: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class AddGoalViewModel @Inject constructor(
    private val goalRepository: GoalRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddGoalUiState())
    val state: StateFlow<AddGoalUiState> = _state.asStateFlow()

    // --- ACCIONES DE LA INTERFAZ ---
    fun onTitleChange(title: String) { _state.update { it.copy(title = title) } }
    fun onTargetAmountChange(amount: String) { _state.update { it.copy(targetAmount = amount) } }
    fun onLinkUrlChange(url: String) { _state.update { it.copy(linkUrl = url) } }
    fun onIconChange(iconId: String) { _state.update { it.copy(iconId = iconId) } }
    fun onPrincipalChange(isPrincipal: Boolean) { _state.update { it.copy(isPrincipal = isPrincipal) } }

    // --- GUARDADO ---
    fun saveGoal(onSuccess: () -> Unit) {
        val currentAmount = _state.value.targetAmount.toDoubleOrNull() ?: 0.0
        val currentState = _state.value

        if (currentState.title.isNotBlank() && currentAmount > 0) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }

                // Paso clave: Si el usuario marcó que esta será la nueva meta principal,
                // debemos avisarle a la base de datos que "destrone" a la anterior
                if (currentState.isPrincipal) {
                    // Nota: Si no tienes esta función en el repo, agrégala como vimos en el paso anterior
                    // goalRepository.clearPrincipalGoals()
                }

                val newGoal = Goal(
                    title = currentState.title,
                    targetAmount = currentAmount,
                    savedAmount = 0.0,
                    linkUrl = currentState.linkUrl,
                    iconId = currentState.iconId,
                    isPrincipal = currentState.isPrincipal
                )

                goalRepository.saveGoal(newGoal)

                // Finalizamos y volvemos a la pantalla anterior
                _state.update { it.copy(isLoading = false) }
                onSuccess()
            }
        }
    }
}