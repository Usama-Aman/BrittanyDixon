package com.cp.brittany.dixon.activities.home.shops_tab.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.models.ProductSectionsData
import com.cp.brittany.dixon.databinding.ItemHistoryBinding
import com.bumptech.glide.Glide
import com.cp.brittany.dixon.utils.viewGone

class CollectionAdapter(private var list: MutableList<ProductSectionsData> = ArrayList(), private val mlistener: CollectionInterface) :
    RecyclerView.Adapter<CollectionAdapter.ViewHolder>() {
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
        val listener: CollectionInterface
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(position: Int) {
            Glide.with(context)
                .load(list[position].image_path)
                .into(binding.imgMain)
            binding.tvName.text = list[position].name
            binding.tvCategory.text = list[position].description
            binding.tvActualPrice.viewGone()
            binding.tvDiscountedPrice.viewGone()
            binding.root.setOnClickListener {
                listener.onItemClicked(position)
            }
        }
    }

    interface CollectionInterface {
        fun onItemClicked(position: Int)
    }
}