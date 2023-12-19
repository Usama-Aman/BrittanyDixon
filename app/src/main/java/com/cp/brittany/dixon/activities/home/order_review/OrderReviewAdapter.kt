package com.cp.brittany.dixon.activities.home.order_review

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.databinding.ItemOrderReviewBinding

class OrderReviewAdapter(var context: Context) :
    RecyclerView.Adapter<OrderReviewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemOrderReviewBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_order_review,
            parent,
            false
        )
        return ViewHolder(this, context, binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind()
    }

    override fun getItemCount(): Int {
        return 3
    }

    class ViewHolder(
        var adapter: OrderReviewAdapter,
        var context: Context,
        var binding: ItemOrderReviewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind() {

        }
    }
}