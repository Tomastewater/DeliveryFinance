package com.tomastewater.deliveryfinance.domain.model

data class Goal(
    val id: Long = 0,
    val title: String,
    val targetAmount: Double,
    val savedAmount: Double = 0.0,
    val linkUrl: String? = null,
    val isCompleted: Boolean = false
) {
    // Propiedad calculada útil para la UI (US-06: Progreso de meta)
    val progressPercentage: Float
        get() = if (targetAmount > 0) (savedAmount / targetAmount).toFloat() else 0f
}