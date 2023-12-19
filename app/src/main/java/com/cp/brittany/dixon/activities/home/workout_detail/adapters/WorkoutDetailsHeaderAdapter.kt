package com.cp.brittany.dixon.activities.home.workout_detail.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.workout_detail.WorkoutSection
import com.cp.brittany.dixon.databinding.ItemWorkoutDetailsHeaderBinding

class WorkoutDetailsHeaderAdapter(
    var context: Context,
    var list: MutableList<WorkoutSection>,
    val isFree: Int,
    val listener: WorkoutClickInterface
) : RecyclerView.Adapter<WorkoutDetailsHeaderAdapter.ViewHolder>() {


    interface WorkoutClickInterface {
        fun onWorkoutClicked(sectionPosition: Int, lessonPosition: Int, childPosition: Int = 0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemWorkoutDetailsHeaderBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_workout_details_header,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class ViewHolder(
        var binding: ItemWorkoutDetailsHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(position: Int) {
            val model = list[position]
            if (position == list.size - 1) {
                binding.view.visibility = View.GONE
            }
            binding.tvHeader.text = model.name
            val adapter = WorkoutDetailsVideosAdapter(
                model.get_this_workout_lessons, isFree, object : WorkoutClickInterface {
                    override fun onWorkoutClicked(sectionPosition: Int, lessonPosition: Int, childPosition: Int) {
                        listener.onWorkoutClicked(position, lessonPosition, childPosition)
                    }
                }
            )
            binding.rvVideos.adapter = adapter
        }
    }
}