package com.cp.brittany.dixon.activities.home.edit_profile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ItemHeightRulerBigBinding
import com.cp.brittany.dixon.databinding.ItemHeightRulerSmallBinding
import com.cp.brittany.dixon.databinding.ItemLeftRightViewBinding

class HeightRulerAdapter(
    var context: Context,
    var list: MutableList<HeightModel?>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val LEFT_RIGHT_ITEM = 0
        const val ITEM_TYPE_BIG = 1
        const val ITEM_TYPE_SMALL = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            ITEM_TYPE_BIG -> {
                val binding: ItemHeightRulerBigBinding = DataBindingUtil.inflate(
                    inflater,
                    R.layout.item_height_ruler_big,
                    parent,
                    false
                )
                return BigItemViewHolder(binding)
            }
            ITEM_TYPE_SMALL -> {
                val binding: ItemHeightRulerSmallBinding = DataBindingUtil.inflate(
                    inflater,
                    R.layout.item_height_ruler_small,
                    parent,
                    false
                )
                return SmallItemViewHolder(binding)
            }
            else -> {
                val binding: ItemLeftRightViewBinding = DataBindingUtil.inflate(
                    inflater,
                    R.layout.item_left_right_view,
                    parent,
                    false
                )
                return LeftRightItemViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BigItemViewHolder -> holder.onBind(position)
            is LeftRightItemViewHolder -> holder.onBind(position)
            is SmallItemViewHolder -> holder.onBind(position)
        }
    }


    override fun getItemViewType(position: Int): Int {
        return when {
            list[position] == null -> {
                LEFT_RIGHT_ITEM
            }
            list[position]?.isBig == true -> {
                ITEM_TYPE_BIG
            }
            else -> ITEM_TYPE_SMALL
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class SmallItemViewHolder(binding: ItemHeightRulerSmallBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(position: Int) {

        }
    }

    inner class BigItemViewHolder(
        var binding: ItemHeightRulerBigBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(position: Int) {
            val model = list[position]
            binding.tvName.text = model?.height
            /* binding.tvName.setOnClickListener {
                 clickListener.onWeightClick(position, model)
                 parentRecycler?.scrollToPosition(adapterPosition)
             }*/
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

interface HeightClickListener {
    fun onWeightClick(position: Int, model: WeightsModel)
}

data class HeightModel(
    var height: String,
    var isSelected: Boolean = false,
    var isBig: Boolean,
    var isEdge: Boolean = false
)