package com.cp.brittany.dixon.activities.home.premium

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.SkuDetails
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.base.getAmount
import com.cp.brittany.dixon.databinding.ItemPremiumBinding
import com.cp.brittany.dixon.utils.AppUtils

class SubscriptionAdapter(private val list: ArrayList<SkuDetails>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<SubscriptionAdapter.ViewHolder>() {
    private lateinit var context: Context

    inner class ViewHolder(val binding: ItemPremiumBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(position: Int) {
            binding.tvDetails.text = list[position].description
//            binding.tvPrice.text = String.format("%.2f",list[position].price.split("\\s".toRegex())[1].toDouble())
            binding.tvPrice.text = String.format("%.2f",list[position].price.getAmount().toDouble())
            binding.tvUnit.text = when {
                list[position].subscriptionPeriod.contains("W") -> "/w"
                list[position].subscriptionPeriod.contains("M") -> "/m"
                else -> "/y"
            }
            binding.currency.text = list[position].priceCurrencyCode
            binding.tvTimePeriod.text = list[position].title.replace("(Brittany Dixon Fitness App)","").trim()

            itemView.setOnClickListener {
                AppUtils.preventDoubleClick(itemView)
                listener.onItemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionAdapter.ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemPremiumBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_premium,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubscriptionAdapter.ViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int = list.size

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}