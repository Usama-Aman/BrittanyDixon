package com.cp.brittany.dixon.activities.auth.workout_method

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.auth.models.ProfilesData
import com.cp.brittany.dixon.databinding.ItemWorkoutPopupOuterBinding

class WorkoutPopupAdapter(var profilesData: List<ProfilesData>) :
    RecyclerView.Adapter<WorkoutPopupAdapter.ViewHolder>() {

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemWorkoutPopupOuterBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_workout_popup_outer,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int = profilesData.size

    inner class ViewHolder(var binding: ItemWorkoutPopupOuterBinding) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(position: Int) {
            binding.tvName.text = profilesData[position].name
            binding.recyclerView.layoutManager = LinearLayoutManager(mContext)
            binding.recyclerView.adapter = WorkoutPopupInnerAdapter(profilesData[position].profiles_data)
        }
    }
}