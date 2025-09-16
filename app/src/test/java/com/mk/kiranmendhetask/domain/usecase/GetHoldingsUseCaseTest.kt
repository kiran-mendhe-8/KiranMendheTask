package com.mk.kiranmendhetask.domain.usecase

import com.mk.kiranmendhetask.domain.model.Holding
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

class GetHoldingsUseCaseTest {

    private lateinit var repository: HoldingsRepository
    private lateinit var useCase: GetHoldingsUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetHoldingsUseCase(repository)
    }

    @Test
    fun `invoke should return success when repository returns holdings`() = runTest {
        // Given
        val expectedHoldings = listOf(
            Holding(
                symbol = "TEST",
                quantity = 10,
                ltp = 100.0,
                avgPrice = 90.0,
                close = 95.0,
                totalPnL = 100.0,
                totalPnLPercent = 11.11
            )
        )
        coEvery { repository.getHoldings() } returns Result.success(expectedHoldings)

        // When
        val result = useCase()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedHoldings, result.getOrNull())
    }

    @Test
    fun `invoke should return failure when repository throws exception`() = runTest {
        // Given
        val exception = Exception("Network error")
        coEvery { repository.getHoldings() } returns Result.failure(exception)

        // When
        val result = useCase()

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `observe should return flow from repository`() = runTest {
        // Given
        val expectedHoldings = listOf<Holding>()
        every { repository.observeHoldings() } returns flowOf(expectedHoldings)

        // When
        val flow = useCase.observe()

        // Then
        val result = flow.collect { holdings ->
            assertEquals(expectedHoldings, holdings)
        }
    }
}