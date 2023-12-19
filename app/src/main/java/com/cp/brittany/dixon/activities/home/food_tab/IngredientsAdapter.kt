package com.cp.brittany.dixon.activities.home.food_tab

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.home_models.Ingredients
import com.cp.brittany.dixon.databinding.ItemIngredientBinding

class IngredientsAdapter(
    var context: Context,
    var list: MutableList<Ingredients>
) :
    RecyclerView.Adapter<IngredientsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemIngredientBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_ingredient,
            parent,
            false
        )
        return ViewHolder(this, context, binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(
        var adapter: IngredientsAdapter,
        var context: Context,
        var binding: ItemIngredientBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(position: Int) {
            val model = list[position]
            binding.tvTitle.text = model.name
            binding.tvQuantity.text = model.pivot.unit
        }
    }
}