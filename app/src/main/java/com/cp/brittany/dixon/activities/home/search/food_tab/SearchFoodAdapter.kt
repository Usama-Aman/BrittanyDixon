package com.cp.brittany.dixon.activities.home.search.food_tab

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.models.SearchFoodModel
import com.cp.brittany.dixon.databinding.ItemFindRecipesBinding
import com.bumptech.glide.Glide
import com.cp.brittany.dixon.utils.AppUtils
import com.cp.brittany.dixon.utils.viewGone


class SearchFoodAdapter(var context: Context, val mListener: FoodClickListener) :
    RecyclerView.Adapter<SearchFoodAdapter.SearchViewHolder>() {

    var myFoodSearchFilterList = mutableListOf<SearchFoodModel>()

    inner class SearchViewHolder(
        var binding: ItemFindRecipesBinding,
        val listener: FoodClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(position: Int) {
            val foodItem = myFoodSearchFilterList[position]

            binding.ivBookmark.viewGone()
            Glide.with(context)
                .load(foodItem.image_path)
                .into(binding.ivFoodImage)

            binding.tvFoodName.text = foodItem.name
            binding.tvCalories.text = foodItem.calories
            binding.tvTime.text = foodItem.time + " " + foodItem.unit
            binding.root.setOnClickListener {
                AppUtils.preventDoubleClick(binding.root)
                listener.onItemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemFindRecipesBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_find_recipes,
            parent,
            false
        )
        return SearchViewHolder(binding, mListener)
    }


    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.onBind(position)
    }


    override fun getItemCount(): Int {
        return myFoodSearchFilterList.size
    }

    interface FoodClickListener {
        fun onItemClick(position: Int)
    }
}