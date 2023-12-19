package com.cp.brittany.dixon.activities.home.premium

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.databinding.ItemSubscriptionDetailsBinding

class SubscriptionDetailsAdapter() : RecyclerView.Adapter<SubscriptionDetailsAdapter.ViewHolder>() {
    private lateinit var context: Context

    inner class ViewHolder(val binding: ItemSubscriptionDetailsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(position: Int) {
            when (position) {
                0 -> binding.tvDetail.text = "An The full context of your organization’s message history at your fingertips."
                1 -> binding.tvDetail.text = "An Timely info and actions in one place with unlimited integrations pair."
                2 -> binding.tvDetail.text = "Face-to-face communication."
                3 -> binding.tvDetail.text = "Secure collaboration with outside organizans or guests from within Slack."
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemSubscriptionDetailsBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_subscription_details,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int = 4


}