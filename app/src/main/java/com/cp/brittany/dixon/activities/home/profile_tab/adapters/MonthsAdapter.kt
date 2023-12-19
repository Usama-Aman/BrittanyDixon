package com.cp.brittany.dixon.activities.home.profile_tab.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ItemLeftRightViewBinding
import com.cp.brittany.dixon.databinding.ItemMonthsBinding

class MonthsAdapter(
    var context: Context,
    var selectedMonth: Int,
    var list: ArrayList<MonthsModel>,
    var listener: MonthClickListener
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

    fun updateSelection(selectedMonth: Int) {
        this.selectedMonth = selectedMonth
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == ITEM_TYPE_MAIN) {
            val binding: ItemMonthsBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.item_months,
                parent,
                false
            )
            return MainViewHolder(binding,listener)
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
            is MainViewHolder -> {
                holder.onBind(position)

            }
            is LeftRightItemViewHolder -> holder.onBind(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> LEFT_RIGHT_ITEM
            list.size - 1 -> LEFT_RIGHT_ITEM
            else -> ITEM_TYPE_MAIN
        }
    }

    inner class MainViewHolder(
        var binding: ItemMonthsBinding,
        val listener: MonthClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(position: Int) {
            val model = list[position]
            binding.tvName.text = model.month

            /*   binding.tvName.setOnClickListener {
                   listener.onMonthClick(adapterPosition, list[adapterPosition])
                   selectedMonth = model.monthPosition
   //                (context as BaseActivity).showSuccessToast(context, "$selectedMonth")
                   notifyDataSetChanged()
               }*/

            if (model.isSelected) {
                binding.tvName.textSize = 26f
                binding.tvName.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.black
                    )
                )
                binding.tvName.setOnClickListener {  listener.onMonthClick(position,model)}
            } else {
                binding.tvName.textSize = 20f
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

interface MonthClickListener {
    fun onMonthClick(position: Int, model: MonthsModel)
}

data class MonthsModel(var month: String, var monthPosition: Int, var isSelected: Boolean = false)