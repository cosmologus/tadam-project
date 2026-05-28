package com.example.tadamproject.ui.converter

import com.example.tadamproject.data.CurrencyRepository
import com.example.tadamproject.data.local.ConversionHistoryDao
import com.example.tadamproject.data.remote.ExchangeRatesResponse
import com.example.tadamproject.data.remote.FrankfurterApiService
import com.example.tadamproject.model.ConversionHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ConverterViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun convert_updatesResultState() = runTest {
        val viewModel = ConverterViewModel(FakeCurrencyRepository())

        viewModel.updateAmount("10")
        viewModel.convert()
        advanceUntilIdle()

        assertEquals("20.0", viewModel.uiState.value.result)
    }

    @Test
    fun convert_savesToHistory() = runTest {
        val repository = FakeCurrencyRepository()
        val viewModel = ConverterViewModel(repository)

        viewModel.updateAmount("10")
        viewModel.convert()
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.history.size)
        assertEquals("USD", viewModel.uiState.value.history.first().fromCurrency)
        assertEquals("EUR", viewModel.uiState.value.history.first().toCurrency)
    }

    @Test
    fun convert_withError_updatesErrorState() = runTest {
        val viewModel = ConverterViewModel(FakeCurrencyRepository(shouldThrow = true))

        viewModel.updateAmount("10")
        viewModel.convert()
        advanceUntilIdle()

        assertEquals("Error", viewModel.uiState.value.error)
        assertTrue(viewModel.uiState.value.result.isEmpty())
    }

    @Test
    fun swapCurrencies_exchangesFromAndToAndClearsResult() = runTest {
        val viewModel = ConverterViewModel(FakeCurrencyRepository())

        viewModel.updateAmount("10")
        viewModel.convert()
        advanceUntilIdle()
        viewModel.swapCurrencies()

        assertEquals("EUR", viewModel.uiState.value.fromCurrency)
        assertEquals("USD", viewModel.uiState.value.toCurrency)
        assertTrue(viewModel.uiState.value.result.isEmpty())
    }
}

private class FakeCurrencyRepository(
    private val shouldThrow: Boolean = false
) : CurrencyRepository(FakeFrankfurterApiService(), FakeConversionHistoryDao()) {
    private val history = MutableStateFlow<List<ConversionHistory>>(emptyList())

    override suspend fun convert(from: String, to: String, amount: Double): Double {
        if (shouldThrow) {
            throw IllegalStateException("Boom")
        }

        return amount * 2
    }

    override suspend fun saveConversion(conversion: ConversionHistory) {
        history.value = listOf(conversion) + history.value
    }

    override fun getConversionHistory(): Flow<List<ConversionHistory>> {
        return history
    }
}

private class FakeFrankfurterApiService : FrankfurterApiService {
    override suspend fun getLatestRates(base: String, symbols: String): ExchangeRatesResponse {
        return ExchangeRatesResponse(
            amount = 1.0,
            base = base,
            date = "2025-01-01",
            rates = mapOf(symbols to 2.0)
        )
    }
}

private class FakeConversionHistoryDao : ConversionHistoryDao {
    override suspend fun insert(conversion: ConversionHistory) {
    }

    override fun getAll(): Flow<List<ConversionHistory>> {
        return MutableStateFlow(emptyList())
    }
}
