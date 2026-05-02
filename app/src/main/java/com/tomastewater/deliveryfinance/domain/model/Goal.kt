package com.tomastewater.deliveryfinance.domain.model

data class Goal(
    val id: Long = 0,
    val title: String,
    val targetAmount: Double,
    val savedAmount: Double = 0.0,
    val iconId: String = "Star",
    val linkUrl: String = "",
    val isCompleted: Boolean = false,
    val isPrincipal: Boolean = false,
    val imageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    val progressPercentage: Float
        get() = if (targetAmount > 0) (savedAmount / targetAmount).toFloat().coerceIn(0f, 1f) else 0f
}