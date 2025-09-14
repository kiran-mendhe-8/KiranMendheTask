package com.mk.kiranmendhetask.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HoldingsResponse(
    val data: Data
) : Parcelable

@Parcelize
data class Data(
    val userHolding: List<HoldingDto>
) : Parcelable

@Parcelize
data class HoldingDto(
    val symbol: String,
    val quantity: Int,
    val ltp: Double,
    val avgPrice: Double,
    val close: Double
) : Parcelable