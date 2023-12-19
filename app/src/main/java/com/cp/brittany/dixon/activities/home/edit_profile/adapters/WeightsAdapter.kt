package com.cp.brittany.dixon.activities.home.edit_profile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ItemLeftRightViewBinding
import com.cp.brittany.dixon.databinding.ItemWeightBinding

class WeightsAdapter(
    var context: Context,
    var list: ArrayList<WeightsModel?>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val LEFT_RIGHT_ITEM = 0
        const val ITEM_TYPE_MAIN = 1
    }

    var parentRecycler: RecyclerView? = null
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        parentRecycler = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == ITEM_TYPE_MAIN) {
            val binding: ItemWeightBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.item_weight,
                parent,
                false
            )
            return MainViewHolder(binding)
        } else {
            val binding: ItemLeftRightViewBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.item_left_right_view,
                parent,
                false
            )
            return LeftRightItemViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MainViewHolder -> holder.onBind(position)
            is LeftRightItemViewHolder -> holder.onBind(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> LEFT_RIGHT_ITEM
            list.size -1 -> LEFT_RIGHT_ITEM
            else -> ITEM_TYPE_MAIN
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MainViewHolder(
        var binding: ItemWeightBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(position: Int) {
            val model = list[position]
            binding.tvName.text = model?.weight

            if (model?.isSelected == true) {
                binding.tvName.textSize = 24f
                binding.tvName.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.black
                    )
                )
            } else {
                binding.tvName.textSize = 18f
                binding.tvName.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.black_of_10
                    )
                )
            }
        }
    }

    inner class LeftRightItemViewHolder(var binding: ItemLeftRightViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(position: Int) {
            val params = binding.leftItem.layoutParams
            params.width = (context as BaseActivity).getWidth() / 2
            binding.leftItem.layoutParams = params
        }
    }
}

interface WeightClickListener {
    fun onWeightClick(position: Int, model: WeightsModel)
}

data class WeightsModel(var weight: String, var isSelected: Boolean = false)