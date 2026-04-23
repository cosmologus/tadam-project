package com.example.tadamproject.data

import com.example.tadamproject.data.local.ConversionHistoryDao
import com.example.tadamproject.data.remote.FrankfurterApiService
import com.example.tadamproject.model.ConversionHistory
import kotlinx.coroutines.flow.Flow

open class CurrencyRepository(
    private val apiService: FrankfurterApiService,
    private val dao: ConversionHistoryDao
) {
    open suspend fun convert(from: String, to: String, amount: Double): Double {
        if (from == to) {
            return amount
        }

        val response = apiService.getLatestRates(base = from, symbols = to)
        val rate = response.rates[to] ?: 0.0
        return rate * amount
    }

    open suspend fun saveConversion(conversion: ConversionHistory) {
        dao.insert(conversion)
    }

    open fun getConversionHistory(): Flow<List<ConversionHistory>> {
        return dao.getAll()
    }
}
