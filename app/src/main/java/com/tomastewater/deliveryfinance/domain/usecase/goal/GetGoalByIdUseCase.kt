package com.tomastewater.deliveryfinance.domain.usecase.goal

import com.tomastewater.deliveryfinance.domain.model.Goal
import com.tomastewater.deliveryfinance.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGoalByIdUseCase @Inject constructor(
    private val goalRepository: GoalRepository
) {
    // Retorna un Flow para que si aportas dinero, la pantalla de detalle se actualice sola
    operator fun invoke(id: Long): Flow<Goal?> {
        return goalRepository.getGoalById(id)
    }
}