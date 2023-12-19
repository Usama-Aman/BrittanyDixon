package com.cp.brittany.dixon.activities.home.my_orders

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.databinding.ItemMyOrdersCompletedBinding
import com.bumptech.glide.Glide

class MyOrderInnerAdapter(var orderDetailList: List<OrderDetail>, val myOrderInterface: MyOrdersAdapter.MyOrderInterface) :
    RecyclerView.Adapter<MyOrderInnerAdapter.ViewHolder>() {

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemMyOrdersCompletedBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_my_orders_completed,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return orderDetailList.size
    }

    inner class ViewHolder(var binding: ItemMyOrdersCompletedBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun onBind(position: Int) {
            Glide.with(mContext)
                .load(orderDetailList[position].product.first_image)
                .into(binding.imgMain)
            binding.tvItemName.text = orderDetailList[position].product_name
            binding.tvItemType.text = orderDetailList[position].product.category_name.name
            binding.tvSize.text = "Size: ${orderDetailList[position].size}"
            binding.tvColor.text = "Color: ${orderDetailList[position].color}"
            binding.tvQuantity.text = "Quantity: ${orderDetailList[position].quantity.toString()}"
            binding.tvPrice.text = "$${orderDetailList[position].price}"

            itemView.setOnClickListener {
                myOrderInterface.onItemClicked(orderDetailList[position].product_id)
            }
        }
    }
}