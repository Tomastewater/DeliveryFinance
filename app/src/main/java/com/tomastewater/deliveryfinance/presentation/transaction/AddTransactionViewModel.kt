package com.tomastewater.deliveryfinance.presentation.transaction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomastewater.deliveryfinance.domain.model.Transaction
import com.tomastewater.deliveryfinance.domain.model.TransactionType
import com.tomastewater.deliveryfinance.domain.usecase.transaction.AddTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddTransactionUiState(
    val amountStr: String = "0",
    val category: String = "General",
    val note: String = "",
    val location: String = "",
    val paymentMethod: String = "Efectivo",
    val isDetailView: Boolean = false, // Controla si vemos la calculadora o los detalles
    val transactionType: TransactionType = TransactionType.EXPENSE
)

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(AddTransactionUiState())
    val state: StateFlow<AddTransactionUiState> = _state.asStateFlow()

    init {
        // Obtenemos si es INGRESO o GASTO desde la navegación
        val typeStr = savedStateHandle.get<String>("transactionType") ?: "EXPENSE"
        val type = if (typeStr == "INCOME") TransactionType.INCOME else TransactionType.EXPENSE
        _state.update { it.copy(transactionType = type) }
    }

    // Funciones del teclado numérico
    fun onNumberPress(number: String) {
        _state.update {
            val newAmount = if (it.amountStr == "0") number else it.amountStr + number
            it.copy(amountStr = newAmount)
        }
    }

    fun onBackspace() {
        _state.update {
            val newAmount = if (it.amountStr.length > 1) it.amountStr.dropLast(1) else "0"
            it.copy(amountStr = newAmount)
        }
    }

    // Actualizar campos
    fun onCategoryChange(category: String) { _state.update { it.copy(category = category) } }
    fun onNoteChange(note: String) { _state.update { it.copy(note = note) } }
    fun onLocationChange(loc: String) { _state.update { it.copy(location = loc) } }
    fun onPaymentMethodChange(method: String) { _state.update { it.copy(paymentMethod = method) } }

    // Cambiar de vista (Flecha derecha)
    fun toggleDetailView() { _state.update { it.copy(isDetailView = !it.isDetailView) } }

    // Guardar en Base de Datos
    fun onSaveTransaction(onSuccess: () -> Unit) {
        val currentAmount = _state.value.amountStr.toDoubleOrNull() ?: 0.0
        if (currentAmount > 0) {
            viewModelScope.launch {
                val transaction = Transaction(
                    amount = currentAmount,
                    type = _state.value.transactionType,
                    category = _state.value.category,
                    timestamp = System.currentTimeMillis()
                    // Nota: Si agregas note y location al modelo Transaction, pásalos aquí
                )
                addTransactionUseCase(transaction)
                onSuccess()
            }
        }
    }
}