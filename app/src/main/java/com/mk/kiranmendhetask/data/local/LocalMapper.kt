package com.mk.kiranmendhetask.data.local

import com.mk.kiranmendhetask.domain.model.Holding
import javax.inject.Inject

class LocalMapper @Inject constructor() {
    
    fun mapToDomain(entity: HoldingEntity): Holding {
        return Holding(
            symbol = entity.symbol,
            quantity = entity.quantity,
            ltp = entity.ltp,
            avgPrice = entity.avgPrice,
            close = entity.close,
            dayChange = entity.dayChange,
            dayChangePercent = entity.dayChangePercent,
            totalPnL = entity.totalPnL,
            totalPnLPercent = entity.totalPnLPercent,
            holdingType = entity.holdingType
        )
    }
    
    fun mapToEntity(domain: Holding): HoldingEntity {
        return HoldingEntity(
            symbol = domain.symbol,
            quantity = domain.quantity,
            ltp = domain.ltp,
            avgPrice = domain.avgPrice,
            close = domain.close,
            dayChange = domain.dayChange,
            dayChangePercent = domain.dayChangePercent,
            totalPnL = domain.totalPnL,
            totalPnLPercent = domain.totalPnLPercent,
            holdingType = domain.holdingType
        )
    }
    
    fun mapToDomain(entities: List<HoldingEntity>): List<Holding> {
        return entities.map { mapToDomain(it) }
    }
    
    fun mapToEntity(domains: List<Holding>): List<HoldingEntity> {
        return domains.map { mapToEntity(it) }
    }
}