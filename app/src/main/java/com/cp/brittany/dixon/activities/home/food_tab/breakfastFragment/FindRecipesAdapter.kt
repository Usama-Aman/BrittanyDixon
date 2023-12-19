package com.cp.brittany.dixon.activities.home.food_tab.breakfastFragment

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.home_models.FoodsList
import com.cp.brittany.dixon.databinding.ItemFindRecipesBinding
import com.bumptech.glide.Glide
import com.cp.brittany.dixon.utils.AppUtils

class FindRecipesAdapter(var context: Context, var list: MutableList<FoodsList>, var listener: RecipeClickListener) :
    RecyclerView.Adapter<FindRecipesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemFindRecipesBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_find_recipes,
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
        var adapter: FindRecipesAdapter,
        var context: Context,
        var binding: ItemFindRecipesBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(position: Int) {
            val model = list[position]
            Glide.with(context).load(model.image_path).centerCrop().into(binding.ivFoodImage)
            binding.tvFoodType.text = model.type
            binding.tvTime.text = "${model.time} min"

            if (model.calories == "1")
                binding.tvCalories.text = "${model.calories} Calorie"
            else
                binding.tvCalories.text = "${model.calories} Calories"

            binding.tvCalories.text = "${model.calories} Calories"
            binding.tvFoodName.text = model.name

            if(model.is_bookmarked == 0){
                binding.ivBookmark.setImageResource(R.drawable.ic_heart)
            }
            else
                binding.ivBookmark.setImageResource(R.drawable.ic_heart_selected)
            binding.ivBookmark.setOnClickListener {
                AppUtils.preventDoubleClick(binding.ivBookmark)
                listener.onBookmarkClicked(position)
            }
            itemView.setOnClickListener {
                AppUtils.preventDoubleClick(itemView)
                val p1 = Pair.create<View,String>((binding.ivFoodImage as View?)!!, "transitionImage")
                val p2 = Pair.create<View,String>((binding.tvFoodName as View?)!!, "title")
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (context as AppCompatActivity),
                    p1,p2
                )
                listener.onItemClicked(position, model.id, options)
            }
        }
    }
}

interface RecipeClickListener {
    fun onItemClicked(position: Int, food_id: Int, option: ActivityOptionsCompat)
    fun onBookmarkClicked(position: Int)
}