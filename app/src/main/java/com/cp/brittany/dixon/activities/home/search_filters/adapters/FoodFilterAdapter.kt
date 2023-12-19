package com.cp.brittany.dixon.activities.home.search_filters.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.databinding.ItemIngredientsBinding


class FoodFilterAdapter(var context: Context) :
    RecyclerView.Adapter<FoodFilterAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemIngredientsBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_ingredients,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind()
    }

    override fun getItemCount(): Int = 10

    inner class ViewHolder(
        var binding: ItemIngredientsBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind() {
            if (adapterPosition == 0)
                binding.checkBox.background = context.resources.getDrawable(R.drawable.circle)
            else {
                binding.checkBox.background = context.resources.getDrawable(R.drawable.circle_unselect)
                binding.checkBox.setImageDrawable(null)
            }

        }
    }
}