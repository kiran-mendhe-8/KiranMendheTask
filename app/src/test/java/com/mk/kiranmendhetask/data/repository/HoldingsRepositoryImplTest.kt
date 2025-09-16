package com.mk.kiranmendhetask.data.repository

import com.mk.kiranmendhetask.data.local.HoldingEntity
import com.mk.kiranmendhetask.data.local.HoldingsDao
import com.mk.kiranmendhetask.data.local.LocalMapper
import com.mk.kiranmendhetask.data.remote.HoldingsApiService
import com.mk.kiranmendhetask.data.remote.HoldingsMapper
import com.mk.kiranmendhetask.domain.model.Holding
import com.mk.kiranmendhetask.domain.model.HoldingsResponse
import com.mk.kiranmendhetask.domain.model.Data
import com.mk.kiranmendhetask.domain.model.HoldingDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HoldingsRepositoryImplTest {

    private lateinit var apiService: HoldingsApiService
    private lateinit var dao: HoldingsDao
    private lateinit var remoteMapper: HoldingsMapper
    private lateinit var localMapper: LocalMapper
    private lateinit var repository: HoldingsRepositoryImpl

    @Before
    fun setup() {
        apiService = mockk()
        dao = mockk()
        remoteMapper = HoldingsMapper()
        localMapper = LocalMapper()
        repository = HoldingsRepositoryImpl(apiService, dao, remoteMapper, localMapper)
    }

    @Test
    fun `getHoldings should return success when API call succeeds`() = runTest {
        // Given
        val dto = HoldingDto(
            symbol = "TEST",
            quantity = 10,
            ltp = 100.0,
            avgPrice = 90.0,
            close = 95.0
        )
        val response = HoldingsResponse(Data(listOf(dto)))
        coEvery { apiService.getHoldings() } returns response
        coEvery { dao.insertHoldings(any()) } returns Unit

        // When
        val result = repository.getHoldings()

        // Then
        assertTrue(result.isSuccess)
        val holdings = result.getOrNull()!!
        assertEquals(1, holdings.size)
        assertEquals("TEST", holdings[0].symbol)
        assertEquals(10, holdings[0].quantity)
        coVerify { dao.insertHoldings(any()) }
    }

    @Test
    fun `getHoldings should return local data when API fails`() = runTest {
        // Given
        val localEntity = HoldingEntity(
            symbol = "TEST",
            quantity = 10,
            ltp = 100.0,
            avgPrice = 90.0,
            close = 95.0,
            totalPnL = 100.0,
            totalPnLPercent = 11.11
        )
        coEvery { apiService.getHoldings() } throws Exception("Network error")
        every { dao.getAllHoldings() } returns flowOf(listOf(localEntity))

        // When
        val result = repository.getHoldings()

        // Then
        assertTrue(result.isSuccess)
        val holdings = result.getOrNull()!!
        assertEquals(1, holdings.size)
        assertEquals("TEST", holdings[0].symbol)
    }

    @Test
    fun `getPortfolioSummary should return success when holdings are available`() = runTest {
        // Given
        val dto = HoldingDto(
            symbol = "TEST",
            quantity = 10,
            ltp = 100.0,
            avgPrice = 90.0,
            close = 95.0
        )
        val response = HoldingsResponse(Data(listOf(dto)))
        coEvery { apiService.getHoldings() } returns response
        coEvery { dao.insertHoldings(any()) } returns Unit

        // When
        val result = repository.getPortfolioSummary()

        // Then
        assertTrue(result.isSuccess)
        val summary = result.getOrNull()!!
        assertEquals(1000.0, summary.currentValue, 0.01) // 10 * 100
        assertEquals(900.0, summary.totalInvestment, 0.01) // 10 * 90
        assertEquals(100.0, summary.totalPnL, 0.01) // 1000 - 900
    }

    @Test
    fun `observeHoldings should return flow from local data`() = runTest {
        // Given
        val localEntity = HoldingEntity(
            symbol = "TEST",
            quantity = 10,
            ltp = 100.0,
            avgPrice = 90.0,
            close = 95.0,
            totalPnL = 100.0,
            totalPnLPercent = 11.11
        )
        every { dao.getAllHoldings() } returns flowOf(listOf(localEntity))

        // When
        val flow = repository.observeHoldings()

        // Then
        flow.collect { holdings ->
            assertEquals(1, holdings.size)
            assertEquals("TEST", holdings[0].symbol)
        }
    }
}
