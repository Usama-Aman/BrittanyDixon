package com.cp.brittany.dixon.activities.home.search.workout_tab

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.models.WorkoutSearchFilterData
import com.cp.brittany.dixon.databinding.ItemBookmarksWorkoutBinding
import com.bumptech.glide.Glide
import com.cp.brittany.dixon.utils.AppUtils
import java.util.*

class SearchWorkoutAdapter(var context: Context, val workoutSearchInterface: WorkoutSearchInterface) :
    RecyclerView.Adapter<SearchWorkoutAdapter.SearchViewHolder>() {

    interface WorkoutSearchInterface {
        fun onItemClicked(position: Int)
    }

    var myWorkoutSearchFilterList = mutableListOf<WorkoutSearchFilterData>()
    var myFilteredList = mutableListOf<WorkoutSearchFilterData>()

    inner class SearchViewHolder(
        var binding: ItemBookmarksWorkoutBinding,
        val listener: WorkoutSearchInterface
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(position: Int) {
            val workoutItem = myWorkoutSearchFilterList[position]

            Glide.with(context)
                .load(workoutItem.image_path)
                .into(binding.ivImage)

            binding.tvTitle.text = workoutItem.name
            binding.tvLevel.text = workoutItem.level.toString()
            binding.tvWeeks.text = workoutItem.unit
            binding.root.setOnClickListener {
                AppUtils.preventDoubleClick(binding.root)
                listener.onItemClicked(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemBookmarksWorkoutBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_bookmarks_workout,
            parent,
            false
        )
        return SearchViewHolder(binding, workoutSearchInterface)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return myWorkoutSearchFilterList.size
    }

}