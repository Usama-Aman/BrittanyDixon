package com.cp.brittany.dixon.activities.home.my_orders

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.databinding.ItemMyOrdersAdapterBinding

class MyOrdersAdapter(var myOrdersData: MutableList<MyOrdersData>, val myOrderInterface: MyOrderInterface) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var mContext: Context

    interface MyOrderInterface {
        fun onItemClicked(productId: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mContext = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemMyOrdersAdapterBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_my_orders_adapter,
            parent,
            false
        )
        return Item(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Item)
            holder.onBind(position)
    }

    override fun getItemCount(): Int = myOrdersData.size

    inner class Item(var binding: ItemMyOrdersAdapterBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun onBind(position: Int) {
            binding.rvOrders.adapter = MyOrderInnerAdapter(myOrdersData[position].order_detail, myOrderInterface)

            binding.tvDeliveryCode.text = "#${myOrdersData[position].order_number}"
            binding.tvEstimatedDeliveryHead.text = "Order Status"

            when (myOrdersData[position].status) {
                "CANCELED" -> {
                    binding.tvInTransit.setTextColor(ContextCompat.getColor(mContext, R.color.blue_a700))
                }
                "PAID" -> {
                    binding.tvInTransit.setTextColor(ContextCompat.getColor(mContext, R.color.quantum_yellow))
                }
            }

            binding.tvInTransit.text = myOrdersData[position].status
            binding.tvEstimatedDelivery.text = ""

        }
    }
}