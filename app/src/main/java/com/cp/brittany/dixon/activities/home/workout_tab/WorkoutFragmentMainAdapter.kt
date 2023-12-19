package com.cp.brittany.dixon.activities.home.workout_tab

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.home_models.AllWorkoutCategory
import com.cp.brittany.dixon.activities.home.home_models.WorkoutList
import com.cp.brittany.dixon.databinding.ItemWorkoutFragmentMainBinding
import com.cp.brittany.dixon.databinding.ItemWorkoutPremiumBinding
import com.cp.brittany.dixon.utils.AppUtils
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper

class WorkoutFragmentMainAdapter(
    var context: Context,
    var list: MutableList<AllWorkoutCategory?>,
    var listener: WorkoutListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val ITEM_MAIN = 0
        const val ITEM_PREMIUM = 1
        const val ITEM_LOADER = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == ITEM_MAIN) {
            val binding: ItemWorkoutFragmentMainBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.item_workout_fragment_main,
                parent,
                false
            )
            return ViewHolder(binding, this@WorkoutFragmentMainAdapter, context)
        } else {
            val binding: ItemWorkoutPremiumBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.item_workout_premium,
                parent,
                false
            )
            return PremiumViewHolder(binding)
        }
    }

    override fun getItemViewType(position: Int): Int = if (list[position]?.isPremium == true) ITEM_PREMIUM else ITEM_MAIN

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(item: MutableList<AllWorkoutCategory?>) {
        this.list = item
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PremiumViewHolder -> holder.onBind()
            is ViewHolder -> holder.onBind(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(
        var binding: ItemWorkoutFragmentMainBinding,
        var adapter: WorkoutFragmentMainAdapter,
        var context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(position: Int) {
            val model = list[position]
            binding.tvDescription.text = model?.category_name

            val innerAdapter =
                WorkoutFragmentInnerAdapter(context, model?.workouts as MutableList<WorkoutList>, object : WorkoutListener {
                    override fun onItemClicked(position: Int, workoutId: Int) {
                        listener.onItemClicked(position, workoutId)
                    }

                    override fun onPremiumClicked() {
                        listener.onPremiumClicked()
                    }
                })
            binding.rv.adapter = innerAdapter
            // Horizontal
            OverScrollDecoratorHelper.setUpOverScroll(binding.rv, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL)
        }
    }

    inner class PremiumViewHolder(
        var binding: ItemWorkoutPremiumBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind() {

            binding.cvBuyPremium.setOnClickListener {
                AppUtils.preventDoubleClick(binding.cvBuyPremium)
                listener.onPremiumClicked()
            }
        }
    }
}