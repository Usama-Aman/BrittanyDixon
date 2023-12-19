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
import com.cp.brittany.dixon.activities.home.models.TrendingProductsItem
import com.cp.brittany.dixon.databinding.ItemTrendingShopsTabBinding
import com.bumptech.glide.Glide
import com.cp.brittany.dixon.utils.viewGone

class TrendingShopAdapter(
    val trendingProducts: MutableList<TrendingProductsItem>,
    val trendingProductsInterface: TrendingProductsInterface
) : RecyclerView.Adapter<TrendingShopAdapter.ViewHolder>() {

    private lateinit var mContext: Context

    interface TrendingProductsInterface {
        fun onItemClicked(position: Int, option: ActivityOptionsCompat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemTrendingShopsTabBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_trending_shops_tab,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int = trendingProducts.size


    inner class ViewHolder(var binding: ItemTrendingShopsTabBinding) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(position: Int) {
            binding.tvName1.text = trendingProducts[position].product_data.product_name
            if (trendingProducts[position].product_data.sub_category != null) {
                binding.tvCategory1.text = trendingProducts[position].product_data.sub_category.name
            } else {
                binding.tvCategory1.text = trendingProducts[position].product_data.categories.name
            }
            if (position == trendingProducts.size - 1) {
                binding.viewBelowTrending1.viewGone()
            }
            binding.tvPrice.text = trendingProducts[position].product_data.minimum_price.price
            Glide.with(mContext)
                .load(trendingProducts[position].product_data.first_image.file_path)
                .into(binding.imgMain1)

            itemView.setOnClickListener {
                val p1 = Pair.create<View, String>((binding.imgMain1 as View?)!!, "transitionImage")
                val p2 = Pair.create<View, String>((binding.tvName1 as View?)!!, "title")
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (mContext as AppCompatActivity),
                    p1, p2
                )
                trendingProductsInterface.onItemClicked(position, options)
            }
        }
    }
}