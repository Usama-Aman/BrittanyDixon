package com.cp.brittany.dixon.activities.home.shops_tab.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.models.HistoryProductsItem
import com.cp.brittany.dixon.databinding.ItemHistoryBinding
import com.bumptech.glide.Glide
import com.cp.brittany.dixon.utils.viewGone


class HistoryAdapter(private var list: MutableList<HistoryProductsItem> = ArrayList(), private val mlistener: HistoryProductInterface) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemHistoryBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_history,
            parent,
            false
        )
        return ViewHolder(binding, mlistener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(
        var binding: ItemHistoryBinding,
        val listener: HistoryProductInterface
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(position: Int) {
            Glide.with(context)
                .load(list[position].product_data.first_image.file_path)
                .into(binding.imgMain)
            binding.tvName.text = list[position].product_data.product_name
            //binding.tvCategory.text = list[position].product_data.categories.name
            if (list[position].product_data.sub_category != null) {
                binding.tvCategory.text = list[position].product_data.sub_category.name
            } else {
                binding.tvCategory.text = list[position].product_data.categories.name
            }
            if (list[position].product_data.minimum_price.compare_at_price.toDouble() == 0.0) {
                binding.tvDiscountedPrice.text = "$${list[position].product_data.minimum_price.price}"
                binding.tvActualPrice.viewGone()
            } else {
                binding.tvActualPrice.text = "$${list[position].product_data.minimum_price.price}"
                binding.tvDiscountedPrice.text = "$${list[position].product_data.minimum_price.compare_at_price}"
            }
            binding.root.setOnClickListener {
                listener.onItemClicked(position)
            }
        }
    }

    interface HistoryProductInterface {
        fun onItemClicked(position: Int)
    }
}