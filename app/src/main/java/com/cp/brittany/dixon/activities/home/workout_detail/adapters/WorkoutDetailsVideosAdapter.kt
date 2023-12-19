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
import com.cp.brittany.dixon.activities.home.workout_detail.GetThisWorkoutLesson
import com.cp.brittany.dixon.databinding.ItemWorkoutChildBinding
import com.cp.brittany.dixon.databinding.ItemWorkoutDetailsVideosBinding
import com.cp.brittany.dixon.utils.BrittanyEnums
import com.bumptech.glide.Glide
import com.cp.brittany.dixon.base.getTime
import com.cp.brittany.dixon.utils.AppUtils
import com.cp.brittany.dixon.utils.viewGone
import com.cp.brittany.dixon.utils.viewVisible


private const val SINGLE_TYPE = 0
private const val CHILD_TYPE = 1
private const val SET_TYPE = 2
private const val REST_TYPE = 3

class WorkoutDetailsVideosAdapter(
    val list: MutableList<GetThisWorkoutLesson>,
    val isFree: Int,
    val listener: WorkoutDetailsHeaderAdapter.WorkoutClickInterface,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        if (viewType == SET_TYPE) {
            val inflater = LayoutInflater.from(parent.context)
            val binding: ItemWorkoutChildBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.item_workout_child,
                parent,
                false
            )
            return ViewHolderSET(binding)
        } else {
            val inflater = LayoutInflater.from(parent.context)
            val binding: ItemWorkoutDetailsVideosBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.item_workout_details_videos,
                parent,
                false
            )
            return ViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                holder.onBind(position)
            }
            is ViewHolderSET -> {
                holder.onBind(position)
            }
        }

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
            if (position == list.size - 1 && model.childs.isEmpty()) {
                binding.view.visibility = View.GONE
            } else {
                binding.view.visibility = View.VISIBLE
            }
            when (list[position].type) {
                BrittanyEnums.SINGLE_TYPE.type -> {
                    val layoutParams = binding.mainLayout.layoutParams

                    if (list[position].is_rest == 0) {
                        Glide.with(context).load(list[position].files?.thumbnail_path).centerCrop().into(binding.ivImage)
                        binding.tvTitle.text = model.name
                        binding.tvReps.text = "${model.reps} reps"
                        binding.tvTime.text = "${model.duration} sec"
                        //binding.tvCalories.text = "~${150} Kcal"
                        if(isFree == 0){
                            binding.ivImage.foreground = context.getDrawable(R.drawable.ic_video_mask)
                            binding.ivPremium.viewVisible()
                            binding.ivDownload.viewGone()
                            binding.progress.viewGone()
                        }
                        else {
                            binding.ivImage.foreground = null
                            binding.ivPremium.viewGone()

                            if (model.watch_time != null) {
                                if (model.watch_time.elapsed_time == model.watch_time.total_time || model.watch_time.percentage_completed.toDouble() == 100.00) {
                                    binding.ivDownload.viewVisible()
                                    binding.progress.viewGone()
                                    binding.ivImage.foreground = context.getDrawable(R.drawable.ic_video_mask)
                                } else {
                                    binding.progress.viewVisible()
                                    binding.progress.progress = model.watch_time.percentage_completed.toDouble().toInt()
                                    binding.ivDownload.viewGone()
                                    binding.ivImage.foreground = null
                                }

                            } else {
                                binding.progress.viewGone()
                                binding.ivDownload.viewGone()
                                binding.ivImage.foreground = null
                            }
                        }
                        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    } else {
                        layoutParams.height = 0
                    }
                    binding.mainLayout.layoutParams = layoutParams
                }
            }

            itemView.setOnClickListener {
                AppUtils.preventDoubleClick(itemView)
                listener.onWorkoutClicked(0, position)
            }
        }
    }

    inner class ViewHolderSET(
        var binding: ItemWorkoutChildBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(position: Int) {
            val model = list[position]

            when (list[position].type) {
                BrittanyEnums.SET_TYPE.type -> {
                    if(model.watch_time != null) {
                        binding.tvHeader.text = model.watch_time.elapsed_set.toString() + "/" + model.no_of_set
                    }
                    else{
                        binding.tvHeader.text = "1/" + model.no_of_set
                    }
                    val adapter = WorkoutSetChildsAdapter(
                        model.childs as MutableList<Child>, isFree, object : WorkoutDetailsHeaderAdapter.WorkoutClickInterface {
                            override fun onWorkoutClicked(sectionPosition: Int, lessonPosition: Int, childPosition: Int) {
                                listener.onWorkoutClicked(0, position, childPosition)
                            }
                        }
                    )
                    binding.rvVideos.adapter = adapter

                    itemView.setOnClickListener {
                        listener.onWorkoutClicked(0, position)

                    }
                }
            }

            itemView.setOnClickListener {
                listener.onWorkoutClicked(0, position)

            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position].type) {
            "set" -> {
                SET_TYPE
            }
            "child" -> {
                CHILD_TYPE
            }
            "single" -> {
                SINGLE_TYPE
            }
            else -> {
                REST_TYPE
            }
        }

    }
}
