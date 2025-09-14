package com.mk.kiranmendhetask.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mk.kiranmendhetask.domain.model.Holding
import com.mk.kiranmendhetask.domain.model.PortfolioSummary
import com.mk.kiranmendhetask.domain.usecase.GetHoldingsUseCase
import com.mk.kiranmendhetask.domain.usecase.GetPortfolioSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    private val getHoldingsUseCase: GetHoldingsUseCase,
    private val getPortfolioSummaryUseCase: GetPortfolioSummaryUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PortfolioUiState())
    val uiState: StateFlow<PortfolioUiState> = _uiState.asStateFlow()
    
    private val _isSummaryExpanded = MutableStateFlow(false)
    val isSummaryExpanded: StateFlow<Boolean> = _isSummaryExpanded.asStateFlow()
    
    init {
        loadHoldings()
        observeHoldings()
        observePortfolioSummary()
    }
    
    private fun loadHoldings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            getHoldingsUseCase().fold(
                onSuccess = { holdings ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        holdings = holdings,
                        error = null
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Unknown error occurred"
                    )
                }
            )
        }
    }
    
    private fun observeHoldings() {
        viewModelScope.launch {
            getHoldingsUseCase.observe().collect { holdings ->
                _uiState.value = _uiState.value.copy(holdings = holdings)
            }
        }
    }
    
    private fun observePortfolioSummary() {
        viewModelScope.launch {
            getPortfolioSummaryUseCase.observe().collect { summary ->
                _uiState.value = _uiState.value.copy(portfolioSummary = summary)
            }
        }
    }
    
    fun toggleSummaryExpansion() {
        _isSummaryExpanded.value = !_isSummaryExpanded.value
    }
    
    fun refresh() {
        loadHoldings()
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class PortfolioUiState(
    val isLoading: Boolean = false,
    val holdings: List<Holding> = emptyList(),
    val portfolioSummary: PortfolioSummary? = null,
    val error: String? = null
)