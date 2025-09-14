package com.mk.kiranmendhetask.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "holdings")
data class HoldingEntity(
    @PrimaryKey
    val symbol: String,
    val quantity: Int,
    val ltp: Double, // Last Traded Price
    val avgPrice: Double, // Average Price
    val close: Double, // Previous Close Price
    val dayChange: Double, // Day Change
    val dayChangePercent: Double, // Day Change Percentage
    val totalPnL: Double, // Total Profit & Loss
    val totalPnLPercent: Double, // Total Profit & Loss Percentage
    val holdingType: String? = null // T1 Holding, etc.
)