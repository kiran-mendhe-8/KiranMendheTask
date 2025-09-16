package com.mk.kiranmendhetask.data.remote

import com.mk.kiranmendhetask.domain.model.Holding
import com.mk.kiranmendhetask.domain.model.HoldingDto
import javax.inject.Inject

class HoldingsMapper @Inject constructor() {
    
    fun mapToDomain(dto: HoldingDto): Holding {
        // Calculate total P&L and percentage
        val totalPnL = (dto.ltp - dto.avgPrice) * dto.quantity
        val totalPnLPercent = if (dto.avgPrice > 0) (totalPnL / (dto.avgPrice * dto.quantity)) * 100 else 0.0
        
        return Holding(
            symbol = dto.symbol,
            quantity = dto.quantity,
            ltp = dto.ltp,
            avgPrice = dto.avgPrice,
            close = dto.close,
            totalPnL = totalPnL,
            totalPnLPercent = totalPnLPercent
        )
    }
    
    fun mapToDomain(dtos: List<HoldingDto>): List<Holding> {
        return dtos.map { mapToDomain(it) }
    }
}