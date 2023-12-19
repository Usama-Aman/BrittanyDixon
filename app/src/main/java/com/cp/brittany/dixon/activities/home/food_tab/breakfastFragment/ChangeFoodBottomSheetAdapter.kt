package com.cp.brittany.dixon.activities.home.food_tab.breakfastFragment

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.home_models.FoodsList
import com.cp.brittany.dixon.databinding.ItemBottomSheetBinding
import com.bumptech.glide.Glide
import com.cp.brittany.dixon.utils.viewGone
import com.cp.brittany.dixon.utils.viewVisible

class ChangeFoodBottomSheetAdapter(
    private val topFoodsList: MutableList<FoodsList>,
    val changeFoodBottomInterface: ChangeFoodBottomInterface
) : RecyclerView.Adapter<ChangeFoodBottomSheetAdapter.ViewHolder>(), Filterable {

    private lateinit var mContext: Context
    private var filteredFoodsList = topFoodsList

    interface ChangeFoodBottomInterface {
        fun onItemClicked(position: Int)
        fun onItemRemovedClicked(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        return ViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_bottom_sheet, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int = filteredFoodsList.size

    inner class ViewHolder(val binding: ItemBottomSheetBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun onBind(position: Int) {
            Glide.with(mContext)
                .load(filteredFoodsList[position].image_path)
                .into(binding.image)

            if(filteredFoodsList[position].isAddedToUserFoods){
                binding.btnAddFood.viewGone()
                binding.btnRemoveFood.viewVisible()
            }
            else{
                binding.btnAddFood.viewVisible()
                binding.btnRemoveFood.viewGone()
            }
            binding.recipeName.text = filteredFoodsList[position].name
            binding.time.text = "${filteredFoodsList[position].time} ${filteredFoodsList[position].unit}"
            if (filteredFoodsList[position].calories == "1")
                binding.calories.text = "${filteredFoodsList[position].calories} Calorie"
            else
                binding.calories.text = "${filteredFoodsList[position].calories} Calories"

            binding.btnAddFood.setOnClickListener {
                changeFoodBottomInterface.onItemClicked(position)
            }
            binding.btnRemoveFood.setOnClickListener {
                changeFoodBottomInterface.onItemRemovedClicked(position)
            }
        }

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults? {
                val charString = charSequence.toString()
                val filteredList: ArrayList<FoodsList?> = ArrayList()
                if (charString.isEmpty()) {
                    filteredList.addAll(topFoodsList)
                } else {
                    for (row in topFoodsList) {
                        if (row.name.contains(charString, true)) {
                            filteredList.add(row)
                        }
                    }
                }
                try {
                    val filterResults = FilterResults()
                    filterResults.values = filteredList
                    return filterResults
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return null
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(
                charSequence: CharSequence,
                filterResults: FilterResults
            ) {
                if (filterResults != null) {
                    filteredFoodsList = filterResults.values as MutableList<FoodsList>
                    notifyDataSetChanged()
                }
            }
        }
    }
}