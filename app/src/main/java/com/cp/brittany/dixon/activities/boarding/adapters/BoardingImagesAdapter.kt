package com.cp.brittany.dixon.activities.boarding.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.databinding.ItemBoardingImagesBinding
import com.bumptech.glide.Glide
import com.cp.brittany.dixon.activities.auth.models.BannerData
import com.cp.brittany.dixon.activities.auth.models.BannerModel

class BoardingImagesAdapter(
    var context: Context,
    var list: ArrayList<BannerData>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemBoardingImagesBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_boarding_images,
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

    inner class MainViewHolder(
        var binding: ItemBoardingImagesBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(position: Int) {
            val model = list[position]
            binding.tvTop.text = model.title
            binding.tvDetails.text = model.description
            Glide.with(context)
                .load(model.image_path)
                .placeholder(R.drawable.ic_product_placeholder)
                .into(binding.ivImage)
        }
    }
}

data class BoardingImagesModel(var image: Int)