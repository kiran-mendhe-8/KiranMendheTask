package com.mk.kiranmendhetask.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mk.kiranmendhetask.R
import com.mk.kiranmendhetask.databinding.ItemHoldingBinding
import com.mk.kiranmendhetask.domain.model.Holding
import com.mk.kiranmendhetask.utils.formatCurrency
import com.mk.kiranmendhetask.utils.formatQuantity

class HoldingsAdapter : ListAdapter<Holding, HoldingsAdapter.HoldingViewHolder>(HoldingDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoldingViewHolder {
        val binding = ItemHoldingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HoldingViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: HoldingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class HoldingViewHolder(
        private val binding: ItemHoldingBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(holding: Holding) {
            binding.apply {
                tvStock.text = holding.symbol
                
                // NET QTY
                tvNetQtyValue.text = holding.quantity.formatQuantity()
                
                // LTP
                tvLtpValue.text = holding.ltp.formatCurrency()
                
                // P&L - Label stays normal, only value gets color
                tvPnLValue.text = holding.totalPnL.formatCurrency()
                
                // Set color based on profit/loss for P&L value only
                val pnlColor = if (holding.totalPnL >= 0) {
                    ContextCompat.getColor(root.context, R.color.profit_green)
                } else {
                    ContextCompat.getColor(root.context, R.color.loss_red)
                }
                tvPnLValue.setTextColor(pnlColor)
            }
        }
    }
    
    class HoldingDiffCallback : DiffUtil.ItemCallback<Holding>() {
        override fun areItemsTheSame(oldItem: Holding, newItem: Holding): Boolean {
            return oldItem.symbol == newItem.symbol
        }
        
        override fun areContentsTheSame(oldItem: Holding, newItem: Holding): Boolean {
            return oldItem == newItem
        }
    }
}