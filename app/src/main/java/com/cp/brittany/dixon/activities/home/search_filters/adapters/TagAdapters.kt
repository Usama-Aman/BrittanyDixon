package com.cp.brittany.dixon.activities.home.search_filters.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.search_filters.FoodFilterActivity
import com.cp.brittany.dixon.databinding.ItemTagsBinding

class TagAdapters(var list: List<String>, val isFood: Boolean, val tagAdapterInterface: TagAdapterInterface) :
    RecyclerView.Adapter<TagAdapters.ViewHolder>() {

    lateinit var context: Context

    interface TagAdapterInterface {
        fun onItemClicked(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemTagsBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_tags,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(
        var binding: ItemTagsBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(position: Int) {
            binding.tvText.text = list[position]

            if (isFood) {
                if (list[position] == FoodFilterActivity.filterData?.foodType) {
                    binding.tagConstraint.setBackgroundResource(R.drawable.bg_rounded_tv_selected)
                    binding.tvText.setTextColor(context.resources.getColor(R.color.black))

                } else {
                    binding.tagConstraint.setBackgroundResource(R.drawable.bg_textview_blue)
                    binding.tvText.setTextColor(context.resources.getColor(R.color.white))
                }
            } else {
                if (list[position] == FoodFilterActivity.filterData?.weight) {
                    binding.tagConstraint.setBackgroundResource(R.drawable.bg_rounded_tv_selected)
                    binding.tvText.setTextColor(context.resources.getColor(R.color.black))

                } else {
                    binding.tagConstraint.setBackgroundResource(R.drawable.bg_textview_blue)
                    binding.tvText.setTextColor(context.resources.getColor(R.color.white))
                }
            }


            itemView.setOnClickListener {
                tagAdapterInterface.onItemClicked(position)
            }


        }
    }
}