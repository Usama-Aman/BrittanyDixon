package com.cp.brittany.dixon.activities.home.product_page

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.models.Size
import com.cp.brittany.dixon.databinding.ItemProductSizeBinding
import com.cp.brittany.dixon.utils.viewGone
import com.cp.brittany.dixon.utils.viewVisible

class ProductSizeAdapter(val sizeList: MutableList<Size>, val productSizeInterface: ProductSizeInterface) :
    RecyclerView.Adapter<ProductSizeAdapter.ViewHolder>() {

    private lateinit var mContext: Context

    interface ProductSizeInterface {
        fun onSizeSelected(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context

        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemProductSizeBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_product_size,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = sizeList.size

    inner class ViewHolder(val binding: ItemProductSizeBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            if (sizeList[position].isSelected)
                binding.ivSelected.viewVisible()
            else
                binding.ivSelected.viewGone()

            binding.tvSize.text = sizeList[position].size

            itemView.setOnClickListener {
                productSizeInterface.onSizeSelected(position)
            }

        }
    }

}