package com.cp.brittany.dixon.activities.home.workout_detail.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.workout_detail.Child
import com.cp.brittany.dixon.databinding.ItemWorkoutDetailsVideosBinding
import com.cp.brittany.dixon.utils.BrittanyEnums
import com.bumptech.glide.Glide
import com.cp.brittany.dixon.base.getTime
import com.cp.brittany.dixon.utils.AppUtils
import com.cp.brittany.dixon.utils.viewGone
import com.cp.brittany.dixon.utils.viewVisible

class WorkoutSetChildsAdapter(
    val list: MutableList<Child>,
    val isFree: Int,
    val listener: WorkoutDetailsHeaderAdapter.WorkoutClickInterface,
) : RecyclerView.Adapter<WorkoutSetChildsAdapter.ViewHolder>() {

//    interface WorkoutChildInterface {
//        fun onChildClicked(childPosition: Int)
//    }

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context

        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemWorkoutDetailsVideosBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_workout_details_videos,
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
        var binding: ItemWorkoutDetailsVideosBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
        fun onBind(position: Int) {
            val model = list[position]
            binding.ivDownload.visibility = View.GONE
            if (position == list.size - 1) {
                binding.view.visibility = View.GONE
            }
            when (list[position].type) {
                BrittanyEnums.CHILD_TYPE.type -> {
                    val layoutParams = binding.mainLayout.layoutParams

                    if (list[position].is_rest == 0) {
                        Glide.with(context).load(list[position].files?.thumbnail_path).centerCrop().into(binding.ivImage)
                        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                        if(isFree == 0){
                            binding.ivImage.foreground = context.getDrawable(R.drawable.ic_video_mask)
                            binding.ivPremium.viewVisible()
                        }
                        else{
                            binding.ivImage.foreground = null
                            binding.ivPremium.viewGone()
                        }
                    } else {
                        layoutParams.height = 0
                    }

                    binding.progress.viewGone()
                    binding.ivDownload.viewGone()
                    binding.mainLayout.layoutParams = layoutParams
                }
            }

            binding.tvTitle.text = model.name
            binding.tvReps.text = "${model.reps} reps"
            binding.tvTime.text = "${model.duration} sec"
            //binding.tvCalories.text = "~${150} Kcal"
            itemView.setOnClickListener {
                AppUtils.preventDoubleClick(itemView)
                listener.onWorkoutClicked(0, 0, position)
            }
        }

    }
}
//
//interface WorkoutChildListener {
//    fun onItemClicked(position: Int, model: Child)
//}