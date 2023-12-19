package com.cp.brittany.dixon.activities.auth.workout_method

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.auth.models.ProfilesDataList
import com.cp.brittany.dixon.databinding.ItemWorkoutPopupInnerBinding

class WorkoutPopupInnerAdapter(var profileDataList: List<ProfilesDataList>) :
    RecyclerView.Adapter<WorkoutPopupInnerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemWorkoutPopupInnerBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_workout_popup_inner,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int = profileDataList.size

    inner class ViewHolder(var binding: ItemWorkoutPopupInnerBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun onBind(position: Int) {
            binding.details.text = profileDataList[position].name
        }
    }
}