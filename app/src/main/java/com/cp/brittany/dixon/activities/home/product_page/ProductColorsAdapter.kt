package com.cp.brittany.dixon.activities.home.product_page

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.models.Color
import com.cp.brittany.dixon.databinding.ItemProductColorsBinding
import com.cp.brittany.dixon.utils.viewGone
import com.cp.brittany.dixon.utils.viewVisible

class ProductColorsAdapter(val colorList: MutableList<Color>, val productColorInterface: ProductColorInterface) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var mContext: Context

    interface ProductColorInterface {
        fun onColorSelected(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mContext = parent.context

        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemProductColorsBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_product_colors,
            parent,
            false
        )
        return Item(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is Item -> holder.bind(position)
        }
    }

    override fun getItemCount(): Int = colorList.size


    inner class Item(val binding: ItemProductColorsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {

            if (colorList[position].isSelected)
                binding.ivSelected.viewVisible()
            else
                binding.ivSelected.viewGone()

            binding.ivCardView.setCardBackgroundColor(android.graphics.Color.parseColor("#" + colorList[position].code))

            itemView.setOnClickListener {
                productColorInterface.onColorSelected(position)
            }

        }

    }

    fun String.toColor(): Int {
        val colorString = if (this.length == 4) {
            replace("#([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])".toRegex(), this)
        } else {
            this
        }

        return try {
            colorString.toColorInt()
        } catch (e: Exception) {
            return android.graphics.Color.BLACK
        }
    }

}