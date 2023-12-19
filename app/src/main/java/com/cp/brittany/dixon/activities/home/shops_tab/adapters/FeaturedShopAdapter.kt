package com.cp.brittany.dixon.activities.home.shops_tab.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.models.ProductSectionsData
import com.cp.brittany.dixon.databinding.ItemFeaturedShopsTabBinding
import com.bumptech.glide.Glide

class FeaturedShopAdapter(
    var productSectionsList: MutableList<ProductSectionsData>,
    val featureSectionsInterface: FeatureSectionsInterface
) : RecyclerView.Adapter<FeaturedShopAdapter.ViewHolder>() {

    private lateinit var mContext: Context

    interface FeatureSectionsInterface {
        fun onItemClicked(position: Int, option: ActivityOptionsCompat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemFeaturedShopsTabBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_featured_shops_tab,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int = productSectionsList.size


    inner class ViewHolder(var binding: ItemFeaturedShopsTabBinding) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(position: Int) {
            Glide.with(mContext)
                .load(productSectionsList[position].image_path)
                .into(binding.imgMain)

            itemView.setOnClickListener {
                val p1 = Pair.create<View, String>((binding.imgMain as View?)!!, "transitionImage")
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (mContext as AppCompatActivity),
                    p1
                )
                featureSectionsInterface.onItemClicked(position, options)
            }

        }


    }
}