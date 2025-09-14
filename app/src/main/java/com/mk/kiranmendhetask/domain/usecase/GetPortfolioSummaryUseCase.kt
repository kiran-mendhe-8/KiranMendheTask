package com.mk.kiranmendhetask.domain.usecase

import com.mk.kiranmendhetask.domain.model.Holding
import com.mk.kiranmendhetask.domain.model.PortfolioSummary
import com.mk.kiranmendhetask.domain.repository.HoldingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPortfolioSummaryUseCase @Inject constructor(
    private val repository: HoldingsRepository
) {
    suspend operator fun invoke(): Result<PortfolioSummary> {
        return repository.getPortfolioSummary()
    }
    
    fun observe(): Flow<PortfolioSummary> {
        return repository.observePortfolioSummary()
    }
    
    fun calculatePortfolioSummary(holdings: List<Holding>): PortfolioSummary {
        val currentValue = holdings.sumOf { it.currentValue }
        val totalInvestment = holdings.sumOf { it.totalInvestment }
        val totalPnL = currentValue - totalInvestment
        val totalPnLPercent = if (totalInvestment > 0) (totalPnL / totalInvestment) * 100 else 0.0
        val todayPnL = holdings.sumOf { it.todayPnL }
        
        return PortfolioSummary(
            currentValue = currentValue,
            totalInvestment = totalInvestment,
            totalPnL = totalPnL,
            totalPnLPercent = totalPnLPercent,
            todayPnL = todayPnL
        )
    }
}