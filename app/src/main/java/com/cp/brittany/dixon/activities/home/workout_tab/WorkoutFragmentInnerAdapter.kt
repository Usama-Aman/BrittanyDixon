package com.cp.brittany.dixon.activities.home.workout_tab

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.home_models.WorkoutList
import com.cp.brittany.dixon.activities.home.workout_detail.WorkoutDetailActivity
import com.cp.brittany.dixon.databinding.ItemWorkoutFragmentInnerBinding
import com.bumptech.glide.Glide
import com.cp.brittany.dixon.utils.AppUtils
import com.cp.brittany.dixon.utils.viewGone
import com.cp.brittany.dixon.utils.viewVisible

class WorkoutFragmentInnerAdapter(var context: Context, var list: MutableList<WorkoutList>, var listener: WorkoutListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mContext = parent.context
        val inflater = LayoutInflater.from(parent.context)

        val binding: ItemWorkoutFragmentInnerBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_workout_fragment_inner,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> holder.onBind(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(
        var binding: ItemWorkoutFragmentInnerBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(position: Int) {
            val model = list[position]
            Glide.with(context).load(model.image_path).centerCrop().into(binding.ivWorkoutImage)
            binding.tvDescription.text = model.name
            binding.tvLevel.text = "Level ${model.level}"
            binding.tvWeeks.text = "${model.duration} ${model.unit}"
            binding.tvViews.text = model.total_views.toString()
            binding.ivPremium.setOnClickListener {
                listener.onPremiumClicked()
            }

            if (list[position].is_free == 0)
                binding.ivPremium.viewVisible()
            else
                binding.ivPremium.viewGone()

            itemView.setOnClickListener {
                AppUtils.preventDoubleClick(itemView)
                val intent = Intent(mContext, WorkoutDetailActivity::class.java)
                intent.putExtra("workoutId", model.id.toString())
                intent.putExtra("workoutImage", model.image_path)
                intent.putExtra("title", model.name)
                intent.putExtra("details", model.description)
                intent.putExtra("weight", model.weight_status)
                intent.putExtra("level", model.level)
                intent.putExtra("percentageCompleted", model.percentage_completed)
                intent.putExtra("isBookmarked", model.is_bookmarked)
                intent.putExtra("time", model.duration)
                intent.putExtra("unit", model.unit)
                intent.putExtra("is_free", model.is_free)
                intent.putExtra("calories", model.cal_gain_reduce)
                val p1 = Pair.create<View, String>((binding.ivWorkoutImage as View?)!!, "transitionImage")
                val p2 = Pair.create<View, String>((binding.tvDescription as View?)!!, "title")
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (mContext as AppCompatActivity),
                    p1, p2
                )
                mContext.startActivity(intent, options.toBundle())
            }
        }
    }
}

interface WorkoutListener {
    fun onItemClicked(position: Int, workoutId: Int)
    fun onPremiumClicked()
}