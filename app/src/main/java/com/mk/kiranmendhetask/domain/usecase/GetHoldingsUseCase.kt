package com.mk.kiranmendhetask.domain.usecase

import com.mk.kiranmendhetask.domain.model.Holding
import com.mk.kiranmendhetask.domain.repository.HoldingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHoldingsUseCase @Inject constructor(
    private val repository: HoldingsRepository
) {
    suspend operator fun invoke(): Result<List<Holding>> {
        return repository.getHoldings()
    }
    
    fun observe(): Flow<List<Holding>> {
        return repository.observeHoldings()
    }
}