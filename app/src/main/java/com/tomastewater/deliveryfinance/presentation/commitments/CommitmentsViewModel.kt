package com.tomastewater.deliveryfinance.presentation.commitments

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

// Modelo temporal (Luego lo moveremos a domain/model)
enum class CommitmentType { TO_PAY, TO_COLLECT }

data class Commitment(
    val id: Long,
    val title: String,
    val amount: Double,
    val type: CommitmentType,
    val isCompleted: Boolean,
    val date: String
)

@HiltViewModel
class CommitmentsViewModel @Inject constructor() : ViewModel() {

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    // Datos "quemados" para armar la UI
    private val _mockData = listOf(
        Commitment(1, "Mecánico (Frenos)", 25000.0, CommitmentType.TO_PAY, false, "Vence hoy"),
        Commitment(2, "Juan (Préstamo)", 10000.0, CommitmentType.TO_COLLECT, false, "Vence en 3 días"),
        Commitment(3, "Cuota de la moto", 45000.0, CommitmentType.TO_PAY, false, "Vence el 15/05"),
        Commitment(4, "Venta casco viejo", 15000.0, CommitmentType.TO_COLLECT, true, "Cobrado el 02/05"),
        Commitment(5, "Seguro mensual", 8000.0, CommitmentType.TO_PAY, true, "Pagado el 05/05")
    )

    fun setTab(index: Int) {
        _selectedTab.value = index
    }

    // Filtros para la UI
    fun getToPayList() = _mockData.filter { !it.isCompleted && it.type == CommitmentType.TO_PAY }
    fun getToCollectList() = _mockData.filter { !it.isCompleted && it.type == CommitmentType.TO_COLLECT }
    fun getHistoryList() = _mockData.filter { it.isCompleted }
}