package com.mk.kiranmendhetask.presentation.ui

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mk.kiranmendhetask.R
import com.mk.kiranmendhetask.databinding.ActivityMainBinding
import com.mk.kiranmendhetask.presentation.adapter.HoldingsAdapter
import com.mk.kiranmendhetask.presentation.viewmodel.PortfolioViewModel
import com.mk.kiranmendhetask.utils.formatCurrency
import com.mk.kiranmendhetask.utils.formatPercentage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: PortfolioViewModel by viewModels()
    private lateinit var holdingsAdapter: HoldingsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
        }

        setupUI()
        setupObservers()
        setupClickListeners()
    }

    private fun setupUI() {
        holdingsAdapter = HoldingsAdapter()
        binding.rvHoldings.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = holdingsAdapter
        }

        // Set HOLDINGS tab as selected by default
        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(1))
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                handleUiState(state)
            }
        }

        lifecycleScope.launch {
            viewModel.isSummaryExpanded.collect { isExpanded ->
                handleSummaryExpansion(isExpanded)
            }
        }
    }

    private fun handleUiState(state: com.mk.kiranmendhetask.presentation.viewmodel.PortfolioUiState) {
        when {
            state.isLoading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.rvHoldings.visibility = View.GONE
                binding.tvError.visibility = View.GONE
            }

            state.error != null -> {
                binding.progressBar.visibility = View.GONE
                binding.rvHoldings.visibility = View.GONE
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = state.error
            }

            else -> {
                binding.progressBar.visibility = View.GONE
                binding.rvHoldings.visibility = View.VISIBLE
                binding.tvError.visibility = View.GONE

                holdingsAdapter.submitList(state.holdings)

                state.portfolioSummary?.let { summary ->
                    updatePortfolioSummary(summary)
                }
            }
        }
    }

    private fun updatePortfolioSummary(summary: com.mk.kiranmendhetask.domain.model.PortfolioSummary) {
        // Update collapsed state
        val totalPnLText =
            "${summary.totalPnL.formatCurrency()} (${summary.totalPnLPercent.formatPercentage()})"
        binding.tvTotalPnLCollapsed.text = totalPnLText
        binding.tvTotalPnLCollapsed.setTextColor(
            ContextCompat.getColor(
                this,
                if (summary.isProfit) R.color.profit_green else R.color.loss_red
            )
        )

        // Update expanded state
        binding.tvCurrentValue.text = summary.currentValue.formatCurrency()
        binding.tvTotalInvestment.text = summary.totalInvestment.formatCurrency()
        binding.tvTodayPnL.text = summary.todayPnL.formatCurrency()
        binding.tvTodayPnL.setTextColor(
            ContextCompat.getColor(
                this,
                if (summary.isTodayProfit) R.color.profit_green else R.color.loss_red
            )
        )

        binding.tvTotalPnLExpanded.text = totalPnLText
        binding.tvTotalPnLExpanded.setTextColor(
            ContextCompat.getColor(
                this,
                if (summary.isProfit) R.color.profit_green else R.color.loss_red
            )
        )
    }

    private fun handleSummaryExpansion(isExpanded: Boolean) {
        val slideDown = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
        val slideUp = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right)

        if (isExpanded) {
            binding.expandedSummary.visibility = View.VISIBLE
            binding.collapsedSummary.visibility = View.GONE
            binding.ivExpandCollapseExpanded.setImageResource(R.drawable.ic_expand_more)
            binding.expandedSummary.startAnimation(slideDown)
        } else {
            binding.expandedSummary.visibility = View.GONE
            binding.collapsedSummary.visibility = View.VISIBLE
            binding.ivExpandCollapse.setImageResource(R.drawable.ic_expand_less)
            binding.collapsedSummary.startAnimation(slideUp)
        }
    }

    private fun setupClickListeners() {
        binding.portfolioSummaryCard.setOnClickListener {
            viewModel.toggleSummaryExpansion()
        }

        binding.tvError.setOnClickListener {
            viewModel.refresh()
        }
    }
}