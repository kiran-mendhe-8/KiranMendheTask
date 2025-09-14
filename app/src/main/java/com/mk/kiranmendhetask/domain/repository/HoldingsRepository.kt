package com.mk.kiranmendhetask.domain.repository

import com.mk.kiranmendhetask.domain.model.Holding
import com.mk.kiranmendhetask.domain.model.PortfolioSummary
import kotlinx.coroutines.flow.Flow

interface HoldingsRepository {
    suspend fun getHoldings(): Result<List<Holding>>
    suspend fun getPortfolioSummary(): Result<PortfolioSummary>
    fun observeHoldings(): Flow<List<Holding>>
    fun observePortfolioSummary(): Flow<PortfolioSummary>
}