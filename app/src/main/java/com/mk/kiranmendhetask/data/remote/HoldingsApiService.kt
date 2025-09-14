package com.mk.kiranmendhetask.data.remote

import com.mk.kiranmendhetask.domain.model.HoldingsResponse
import retrofit2.http.GET

interface HoldingsApiService {
    @GET(".")
    suspend fun getHoldings(): HoldingsResponse
}