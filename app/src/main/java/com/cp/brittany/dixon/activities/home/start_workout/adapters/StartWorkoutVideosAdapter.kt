package com.cp.brittany.dixon.activities.home.start_workout.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.workout_detail.GetThisWorkoutLesson
import com.cp.brittany.dixon.databinding.ItemStartWorkoutVideosBinding

class StartWorkoutVideosAdapter(
    var context: Context,
    val list: MutableList<GetThisWorkoutLesson>,
    val startWorkOutVideoInterface: StartWorkOutVideoInterface
) : RecyclerView.Adapter<StartWorkoutVideosAdapter.ViewHolder>() {

    interface StartWorkOutVideoInterface {
        fun onVideoClicked(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemStartWorkoutVideosBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_start_workout_videos,
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
        var binding: ItemStartWorkoutVideosBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(position: Int) {
            val model = list[position]
//            Glide.with(context).load(model.thumbnail_path).centerCrop().into(binding.ivImage)
            binding.tvTitle.text = model.name
            binding.tvReps.text = "${model.reps} reps"
            binding.tvTime.text = "${model.duration} min"

            itemView.setOnClickListener {
                startWorkOutVideoInterface.onVideoClicked(adapterPosition)
            }

        }
    }
}