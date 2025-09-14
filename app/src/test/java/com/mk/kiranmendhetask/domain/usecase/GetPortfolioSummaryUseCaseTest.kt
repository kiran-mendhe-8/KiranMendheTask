package com.mk.kiranmendhetask.domain.usecase

import com.mk.kiranmendhetask.domain.model.Holding
import com.mk.kiranmendhetask.domain.model.PortfolioSummary
import com.mk.kiranmendhetask.domain.repository.HoldingsRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetPortfolioSummaryUseCaseTest {

    private lateinit var repository: HoldingsRepository
    private lateinit var useCase: GetPortfolioSummaryUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetPortfolioSummaryUseCase(repository)
    }

    @Test
    fun `invoke should return success when repository returns summary`() = runTest {
        // Given
        val expectedSummary = PortfolioSummary(
            currentValue = 1000.0,
            totalInvestment = 900.0,
            totalPnL = 100.0,
            totalPnLPercent = 11.11,
            todayPnL = 50.0
        )
        coEvery { repository.getPortfolioSummary() } returns Result.success(expectedSummary)

        // When
        val result = useCase()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedSummary, result.getOrNull())
    }

    @Test
    fun `invoke should return failure when repository throws exception`() = runTest {
        // Given
        val exception = Exception("Database error")
        coEvery { repository.getPortfolioSummary() } returns Result.failure(exception)

        // When
        val result = useCase()

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `observe should return flow from repository`() = runTest {
        // Given
        val expectedSummary = PortfolioSummary(
            currentValue = 0.0,
            totalInvestment = 0.0,
            totalPnL = 0.0,
            totalPnLPercent = 0.0,
            todayPnL = 0.0
        )
        every { repository.observePortfolioSummary() } returns flowOf(expectedSummary)

        // When
        val flow = useCase.observe()

        // Then
        flow.collect { summary ->
            assertEquals(expectedSummary, summary)
        }
    }

    @Test
    fun `calculatePortfolioSummary should calculate correct values`() {
        // Given
        val holdings = listOf(
            Holding(
                symbol = "TEST1",
                quantity = 10,
                ltp = 100.0,
                avgPrice = 90.0,
                close = 95.0,
                dayChange = 5.0,
                dayChangePercent = 5.26,
                totalPnL = 100.0,
                totalPnLPercent = 11.11,
                holdingType = "EQ"
            ),
            Holding(
                symbol = "TEST2",
                quantity = 5,
                ltp = 200.0,
                avgPrice = 180.0,
                close = 190.0,
                dayChange = 10.0,
                dayChangePercent = 5.26,
                totalPnL = 100.0,
                totalPnLPercent = 11.11,
                holdingType = "EQ"
            )
        )

        // When
        val result = useCase.calculatePortfolioSummary(holdings)

        // Then
        assertEquals(2000.0, result.currentValue, 0.01) // (10*100) + (5*200)
        assertEquals(1800.0, result.totalInvestment, 0.01) // (10*90) + (5*180)
        assertEquals(200.0, result.totalPnL, 0.01) // 2000 - 1800
        assertEquals(11.111, result.totalPnLPercent, 0.01) // (200/1800) * 100
        assertEquals(-100.0, result.todayPnL, 0.01) // (95-100)*10 + (190-200)*5 = -50 + (-50) = -100
    }
}