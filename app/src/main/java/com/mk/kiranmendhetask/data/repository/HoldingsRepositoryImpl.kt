package com.mk.kiranmendhetask.data.repository

import com.mk.kiranmendhetask.data.local.HoldingsDao
import com.mk.kiranmendhetask.data.local.LocalMapper
import com.mk.kiranmendhetask.data.remote.HoldingsApiService
import com.mk.kiranmendhetask.data.remote.HoldingsMapper
import com.mk.kiranmendhetask.domain.model.Holding
import com.mk.kiranmendhetask.domain.model.PortfolioSummary
import com.mk.kiranmendhetask.domain.repository.HoldingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HoldingsRepositoryImpl @Inject constructor(
    private val apiService: HoldingsApiService,
    private val dao: HoldingsDao,
    private val remoteMapper: HoldingsMapper,
    private val localMapper: LocalMapper
) : HoldingsRepository {
    
    override suspend fun getHoldings(): Result<List<Holding>> {
        return try {
            val response = apiService.getHoldings()
            val holdings = remoteMapper.mapToDomain(response.data.userHolding)
            dao.insertHoldings(localMapper.mapToEntity(holdings))
            Result.success(holdings)
        } catch (e: Exception) {
            // Fallback to local data if network fails
            try {
                val localEntities = dao.getAllHoldings().first()
                val localHoldings = localMapper.mapToDomain(localEntities)
                Result.success(localHoldings)
            } catch (localError: Exception) {
                Result.failure(e)
            }
        }
    }
    
    override suspend fun getPortfolioSummary(): Result<PortfolioSummary> {
        return try {
            val holdings = getHoldings().getOrThrow()
            val summary = calculatePortfolioSummary(holdings)
            Result.success(summary)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun observeHoldings(): Flow<List<Holding>> {
        return dao.getAllHoldings().map { entities ->
            localMapper.mapToDomain(entities)
        }
    }
    
    override fun observePortfolioSummary(): Flow<PortfolioSummary> {
        return observeHoldings().map { holdings ->
            calculatePortfolioSummary(holdings)
        }
    }
    
    private fun calculatePortfolioSummary(holdings: List<Holding>): PortfolioSummary {
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