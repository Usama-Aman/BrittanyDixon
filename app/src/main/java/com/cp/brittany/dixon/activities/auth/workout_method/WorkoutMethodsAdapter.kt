package com.cp.brittany.dixon.activities.auth.workout_method

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.auth.models.Preferences
import com.cp.brittany.dixon.activities.auth.models.PreferencesData
import com.cp.brittany.dixon.databinding.ItemWorkoutMethodsBinding
import com.astritveliu.boom.Boom
import com.bumptech.glide.Glide
import com.cp.brittany.dixon.utils.viewInVisible
import com.cp.brittany.dixon.utils.viewVisible

class WorkoutMethodsAdapter(
    var context: Context, var list: MutableList<Preferences>,
    var listener: WorkoutPreferencesListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemWorkoutMethodsBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_workout_methods,
            parent,
            false
        )
        return MainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MainViewHolder -> holder.onBind(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: MutableList<Preferences>) {
        list = items
        notifyDataSetChanged()
    }

    inner class MainViewHolder(
        var binding: ItemWorkoutMethodsBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(position: Int) {
            Boom(itemView)
            val model = list[position]
            Glide.with(context).load(model.preference_data.image_path).fitCenter()
                .into(binding.ivImage)
            binding.tvTitle.text = model.preference_data.name
            binding.tvDescription.text = model.preference_data.description


            if (model.preference_data.is_selected == 1) {
                binding.main.setBackgroundResource(R.drawable.bg_workout_method_selected)
                binding.ivTick.viewVisible()
            } else {
                binding.main.setBackgroundResource(R.drawable.bg_workout_method_unselected)
                binding.ivTick.viewInVisible()
            }
            binding.main.setOnClickListener {
                listener.onItemClicked(position, model.preference_data)
            }
            binding.tvSeeMore.setOnClickListener {
                listener.onItemSeeMoreClicked(position)
            }
        }


    }
}

interface WorkoutPreferencesListener {
    fun onItemClicked(position: Int, model: PreferencesData)
    fun onItemSeeMoreClicked(position: Int)
}
