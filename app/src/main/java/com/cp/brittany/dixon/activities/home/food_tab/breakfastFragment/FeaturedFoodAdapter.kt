package com.cp.brittany.dixon.activities.home.food_tab.breakfastFragment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.databinding.ItemFeaturedFoodBinding

class FeaturedFoodAdapter(var context: Context) :
    RecyclerView.Adapter<FeaturedFoodAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemFeaturedFoodBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_featured_food,
            parent,
            false
        )
        return ViewHolder(this, context, binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind()
    }

    override fun getItemCount(): Int {
        return 10
    }

    class ViewHolder(
        var adapter: FeaturedFoodAdapter,
        var context: Context,
        var binding: ItemFeaturedFoodBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind() {

        }
    }
}