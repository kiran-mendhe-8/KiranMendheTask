package com.mk.kiranmendhetask.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Holding(
    val symbol: String,
    val quantity: Int,
    val ltp: Double, // Last Traded Price
    val avgPrice: Double, // Average Price
    val close: Double, // Previous Close Price
    val totalPnL: Double, // Total Profit & Loss
    val totalPnLPercent: Double, // Total Profit & Loss Percentage
) : Parcelable {
    
    val currentValue: Double
        get() = ltp * quantity
    
    val totalInvestment: Double
        get() = avgPrice * quantity
    
    val todayPnL: Double
        get() = (close - ltp) * quantity
}