package com.example.tadamproject.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface FrankfurterApiService {
    @GET("v1/latest")
    suspend fun getLatestRates(
        @Query("base") base: String,
        @Query("symbols") symbols: String
    ): ExchangeRatesResponse
}

data class ExchangeRatesResponse(
    val amount: Double,
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)
