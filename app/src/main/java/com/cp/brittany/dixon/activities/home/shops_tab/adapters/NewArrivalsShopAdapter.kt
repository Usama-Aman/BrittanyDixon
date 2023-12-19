package com.cp.brittany.dixon.activities.home.shops_tab.adapters

import android.annotation.SuppressLint
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
import com.cp.brittany.dixon.activities.home.models.NewArrivalsProductsItem
import com.cp.brittany.dixon.databinding.ItemHistoryShopsTabBinding
import com.bumptech.glide.Glide
import com.cp.brittany.dixon.utils.viewGone

class NewArrivalsShopAdapter(
    var newArrivalProducts: MutableList<NewArrivalsProductsItem>,
    val newArrivalProductInterface: NewArrivalProductInterface
) : RecyclerView.Adapter<NewArrivalsShopAdapter.ViewHolder>() {

    private lateinit var mContext: Context

    interface NewArrivalProductInterface {
        fun onItemClicked(position: Int, option: ActivityOptionsCompat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemHistoryShopsTabBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_history_shops_tab,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int = newArrivalProducts.size


    inner class ViewHolder(var binding: ItemHistoryShopsTabBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun onBind(position: Int) {

            Glide.with(mContext)
                .load(newArrivalProducts[position].first_image.file_path)
                .into(binding.imgMain)
            binding.tvName.text = newArrivalProducts[position].product_name
            if (newArrivalProducts[position].minimum_price.compare_at_price.toDouble() == 0.0) {
                binding.tvDiscountedPrice.text = "$${newArrivalProducts[position].minimum_price.price}"
                binding.tvActualPrice.viewGone()
            } else {
                binding.tvActualPrice.text = "$${newArrivalProducts[position].minimum_price.price}"
                binding.tvDiscountedPrice.text = "$${newArrivalProducts[position].minimum_price.compare_at_price}"
            }
            if (newArrivalProducts[position].sub_category != null) {
                binding.tvCategory.text = newArrivalProducts[position].sub_category.name
            } else {
                binding.tvCategory.text = newArrivalProducts[position].categories.name
            }

            itemView.setOnClickListener {
                val p1 = Pair.create<View, String>((binding.imgMain as View?)!!, "transitionImage")
                val p2 = Pair.create<View, String>((binding.tvName as View?)!!, "title")
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (mContext as AppCompatActivity),
                    p1, p2
                )
                newArrivalProductInterface.onItemClicked(position, options)
            }
        }
    }
}