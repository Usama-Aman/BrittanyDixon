package com.cp.brittany.dixon.activities.home.shops_tab.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.models.NewArrivalsProductsItem
import com.cp.brittany.dixon.databinding.ItemNewArrivalDetailsBinding
import com.bumptech.glide.Glide
import com.cp.brittany.dixon.utils.viewGone

class NewArrivalAdapter(private val list: MutableList<NewArrivalsProductsItem>, private val mListener: NewArrivalProductInterface) :
    RecyclerView.Adapter<NewArrivalAdapter.ViewHolder>() {
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        val binding: ItemNewArrivalDetailsBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_new_arrival_details,
            parent,
            false
        )
        return ViewHolder(binding, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(
        var binding: ItemNewArrivalDetailsBinding,
        val listener: NewArrivalProductInterface
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(position: Int) {
            Glide.with(context)
                .load(list[position].first_image)
                .into(binding.imgMain)
            if (list[position].sub_category != null) {
                binding.tvCategory.text = list[position].sub_category.name
            } else {
                binding.tvCategory.text = list[position].categories.name
            }
            if (list[position].minimum_price.compare_at_price.toDouble() == 0.0) {
                binding.tvDiscountedPrice.text = "$${list[position].minimum_price.price}"
                binding.tvActualPrice.viewGone()
            } else {
                binding.tvActualPrice.text = "$${list[position].minimum_price.price}"
                binding.tvDiscountedPrice.text = "$${list[position].minimum_price.compare_at_price}"
            }
            binding.root.setOnClickListener {
                listener.onItemClicked(position)
            }
        }
    }

    interface NewArrivalProductInterface {
        fun onItemClicked(position: Int)
    }
}