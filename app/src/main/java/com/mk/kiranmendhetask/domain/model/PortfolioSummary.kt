package com.mk.kiranmendhetask.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PortfolioSummary(
    val currentValue: Double,
    val totalInvestment: Double,
    val totalPnL: Double,
    val totalPnLPercent: Double,
    val todayPnL: Double
) : Parcelable {
    
    val isProfit: Boolean
        get() = totalPnL >= 0
    
    val isTodayProfit: Boolean
        get() = todayPnL >= 0
}