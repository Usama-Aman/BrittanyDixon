package com.cp.brittany.dixon.activities.home.workout_detail.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.workout_detail.GetThisWorkoutLesson
import com.cp.brittany.dixon.databinding.ItemWorkoutDetailsHeaderBinding
import com.cp.brittany.dixon.databinding.ItemWorkoutDetailsVideosBinding
import com.bumptech.glide.Glide

private const val TYPE_HEADER = 0
private const val TYPE_VIDEO = 1

class WorkoutDetailsAdapter (
val list: MutableList<GetThisWorkoutLesson>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        if(viewType == TYPE_VIDEO) {
            val binding: ItemWorkoutDetailsVideosBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.item_workout_details_videos,
                parent,
                false
            )

            return VideoViewHolder(binding)
        }
        else{
            val binding: ItemWorkoutDetailsHeaderBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.item_workout_details_header,
                parent,
                false
            )
            return HeaderViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is VideoViewHolder -> holder.onBind(position)
            is HeaderViewHolder -> holder.onBind(position)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class VideoViewHolder(
        var binding: ItemWorkoutDetailsVideosBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(position: Int) {
            val model = list[position]
            //Glide.with(context).load(model.thumbnail_path).centerCrop().into(binding.ivImage)
            Glide.with(context).load(R.drawable.img_workout_detail).centerCrop().into(binding.ivImage)
            binding.tvTitle.text = model.name
            binding.tvReps.text = "${model.reps} reps"
            binding.tvTime.text = "${model.duration} min"

        }
    }

    inner class HeaderViewHolder(
        var binding: ItemWorkoutDetailsHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(position: Int) {
            val model = list[position]
            binding.tvHeader.text = model.name
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(list[position].type == "section"){
            TYPE_HEADER
        } else{
            TYPE_VIDEO
        }
    }

}

