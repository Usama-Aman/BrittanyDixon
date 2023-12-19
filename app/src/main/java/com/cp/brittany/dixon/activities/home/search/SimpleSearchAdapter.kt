package com.cp.brittany.dixon.activities.home.search

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.home_models.FoodsList
import com.cp.brittany.dixon.activities.home.models.InsightsRecommendationData
import com.cp.brittany.dixon.databinding.ItemFindRecipesBinding
import com.cp.brittany.dixon.databinding.ItemInsightMainBinding
import com.bumptech.glide.Glide
import com.cp.brittany.dixon.utils.AppUtils
import com.cp.brittany.dixon.utils.viewGone

private const val FOOD_TYPE = 0
private const val INSIGHT_Type = 1

class SimpleSearchAdapter(var mListener: RecipeClickListener, var type: Int) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var foodList: MutableList<FoodsList> = ArrayList()
    private var insightRecommendations: MutableList<InsightsRecommendationData> = ArrayList()
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        if (viewType == FOOD_TYPE) {
            val binding: ItemFindRecipesBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.item_find_recipes,
                parent,
                false
            )
            return ViewHolder(mListener, context, binding)
        } else {
            val inflater = LayoutInflater.from(parent.context)
            val binding: ItemInsightMainBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.item_insight_main,
                parent,
                false
            )
            return ViewHolderInsight(mListener, context, binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                holder.onBind(position)
            }
            is ViewHolderInsight -> {
                holder.onBind(position)
            }
        }
    }

    override fun getItemCount(): Int {
        if (type == FOOD_TYPE)
            return foodList.size
        else
            return insightRecommendations.size
    }

    inner class ViewHolder(
        val listener: RecipeClickListener,
        var context: Context,
        var binding: ItemFindRecipesBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(position: Int) {
            val model = foodList[position]
            Glide.with(context).load(model.image_path).centerCrop().into(binding.ivFoodImage)
            binding.tvFoodType.text = model.type
            binding.tvTime.text = "${model.time} min"
            binding.ivBookmark.viewGone()

            if (model.calories == "1")
                binding.tvCalories.text = "${model.calories} Calorie"
            else
                binding.tvCalories.text = "${model.calories} Calories"

            binding.tvCalories.text = "${model.calories} Calories"
            binding.tvFoodName.text = model.name

            itemView.setOnClickListener {
                AppUtils.preventDoubleClick(itemView)
                listener.onItemClicked(position, type)
            }
        }
    }

    inner class ViewHolderInsight(
        val listener: RecipeClickListener,
        var context: Context,
        var binding: ItemInsightMainBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(position: Int) {
            binding.tvDescription.text = insightRecommendations[position].name
            binding.tvTime.text = insightRecommendations[position].duration
            binding.ivBookmark.viewGone()

            Glide.with(context)
                .load(insightRecommendations[position].image_path)
                .into(binding.ivRecommendation)

            itemView.setOnClickListener {
                AppUtils.preventDoubleClick(itemView)
                listener.onItemClicked(position, type)
            }
        }
    }

    fun setFoodList(food: MutableList<FoodsList>) {
        foodList.clear()
        foodList.addAll(food)
    }

    fun setInsightList(insight: MutableList<InsightsRecommendationData>) {
        insightRecommendations.clear()
        insightRecommendations.addAll(insight)
    }

    override fun getItemViewType(position: Int): Int {
        return if (type == FOOD_TYPE) {
            FOOD_TYPE
        } else {
            INSIGHT_Type
        }
    }

    interface RecipeClickListener {
        fun onItemClicked(position: Int, type: Int)
    }
}

