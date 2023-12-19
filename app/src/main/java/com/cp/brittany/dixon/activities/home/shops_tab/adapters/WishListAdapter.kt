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
import com.cp.brittany.dixon.activities.home.models.WishListData
import com.cp.brittany.dixon.databinding.ItemHistoryBinding
import com.cp.brittany.dixon.databinding.ItemWishListBinding
import com.bumptech.glide.Glide

class WishListAdapter(private val list: ArrayList<WishListData>, private val isFromShop: Boolean, private val mListener: WishListProductsInterface) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var context: Context

    inner class ViewHolderWishlist(
        val binding: ItemWishListBinding,
        private val listener: WishListProductsInterface
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(position: Int) {
            Glide.with(context)
                .load(list[position].bookmarked_item.first_image)
                .into(binding.ivProduct)
            binding.tvName.text = list[position].bookmarked_item.name
            if (list[position].bookmarked_item.sub_category_name != null)
                binding.tvCategory.text = list[position].bookmarked_item.sub_category_name.name
            else
                binding.tvCategory.text = list[position].bookmarked_item.category_name.name
            binding.tvPrice.text = list[position].bookmarked_item.minimum_price.price
            binding.tvColor.text = "Color: Black"
            binding.tvQuantity.text = "Quantity: x1"
            binding.tvSize.text = "Size: L"
            binding.root.setOnClickListener {
                val p1 = Pair.create<View, String>((binding.ivProduct as View?)!!, "transitionImage")
                val p2 = Pair.create<View, String>((binding.tvName as View?)!!, "title")
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (context as AppCompatActivity),
                    p1, p2
                )
                listener.onItemClick(position, options)
            }
        }
    }

    inner class ViewHolderHistory(
        var binding: ItemHistoryBinding,
        private val listener: WishListProductsInterface
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(position: Int) {
            Glide.with(context)
                .load(list[position].bookmarked_item.first_image)
                .into(binding.imgMain)
            binding.tvName.text = list[position].bookmarked_item.name
            if (list[position].bookmarked_item.sub_category_name != null)
                binding.tvCategory.text = list[position].bookmarked_item.sub_category_name.name
            else
                binding.tvCategory.text = list[position].bookmarked_item.category_name.name
            binding.tvActualPrice.text = "$${list[position].bookmarked_item.minimum_price.price}"
            binding.tvDiscountedPrice.text = "$${list[position].bookmarked_item.minimum_price.price}"
            binding.root.setOnClickListener {
                val p1 = Pair.create<View, String>((binding.imgMain as View?)!!, "transitionImage")
                val p2 = Pair.create<View, String>((binding.tvName as View?)!!, "title")
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (context as AppCompatActivity),
                    p1, p2
                )
                listener.onItemClick(position, options)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        if (isFromShop) {
            val inflater = LayoutInflater.from(context)
            val binding: ItemWishListBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.item_wish_list,
                parent,
                false
            )
            return ViewHolderWishlist(binding, mListener)
        } else {
            val inflater = LayoutInflater.from(parent.context)
            val binding: ItemHistoryBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.item_history,
                parent,
                false
            )
            return ViewHolderHistory(binding, mListener)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderWishlist -> holder.onBind(position)
            is ViewHolderHistory -> holder.onBind(position)
        }
    }

    override fun getItemCount(): Int = list.size

    interface WishListProductsInterface {
        fun onItemClick(position: Int, option: ActivityOptionsCompat)
    }
}