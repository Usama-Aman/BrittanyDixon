package com.cp.brittany.dixon.activities.home.profile_tab.adapters

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
import com.cp.brittany.dixon.activities.home.models.BookmarkModelData
import com.cp.brittany.dixon.databinding.ItemBookmarkListBinding
import com.cp.brittany.dixon.databinding.ItemHistoryShopsTabBinding
import com.bumptech.glide.Glide
import com.cp.brittany.dixon.utils.AppUtils
import com.cp.brittany.dixon.utils.viewGone
import com.cp.brittany.dixon.utils.viewVisible

const val TYPE_SHOP = 0
const val TYPE_WORKOUT = 1
const val TYPE_FOOD = 2
const val TYPE_INSIGHT = 3

class BookmarksInnerAdapter(private val bookmarks: BookmarkModelData, private val type: Int, private val mListener: BookmarksClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var mContext: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mContext = parent.context
        if (type == TYPE_SHOP) {
            val inflater = LayoutInflater.from(parent.context)
            val binding: ItemHistoryShopsTabBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.item_history_shops_tab,
                parent,
                false
            )
            return ShopViewHolder(binding, mListener)
        } else {
            val inflater = LayoutInflater.from(parent.context)
            val binding: ItemBookmarkListBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.item_bookmark_list,
                parent,
                false
            )
            return WorkoutsViewHolder(binding, mListener)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is WorkoutsViewHolder -> holder.onBind(position)
            is ShopViewHolder -> holder.onBind(position)
        }
    }

    override fun getItemCount(): Int {
        return if (type == TYPE_INSIGHT) {
            bookmarks.insights.size
        } else if (type == TYPE_FOOD) {
            bookmarks.foods.size
        } else if (type == TYPE_WORKOUT) {
            bookmarks.workouts.size
        } else {
            bookmarks.products.size
        }
    }

    inner class WorkoutsViewHolder(val binding: ItemBookmarkListBinding, val listener: BookmarksClickListener) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(position: Int) {
            when (type) {
                TYPE_INSIGHT -> {
                    if (position == bookmarks.insights.size - 1) {
                        binding.view.viewGone()
                    }
                    Glide.with(mContext)
                        .load(bookmarks.insights[position].bookmarked_item.image_path)
                        .into(binding.iv)
                    binding.tvInsightTime.text = bookmarks.insights[position].bookmarked_item.duration
                    binding.tvName.text = bookmarks.insights[position].bookmarked_item.name
                    binding.llTime.viewVisible()
                }
                TYPE_FOOD -> {
                    if (position == bookmarks.foods.size - 1) {
                        binding.view.viewGone()
                    }
                    Glide.with(mContext)
                        .load(bookmarks.foods[position].bookmarked_item.image_path)
                        .into(binding.iv)
                    binding.tvTime.text = "${bookmarks.foods[position].bookmarked_item.time} ${bookmarks.foods[position].bookmarked_item.unit}"
                    binding.tvName.text = bookmarks.foods[position].bookmarked_item.name
                    binding.tvCalories.text = "${bookmarks.foods[position].bookmarked_item.calories} Calories"
                    binding.llTimeAndCalories.viewVisible()
                }
                TYPE_WORKOUT -> {
                    if (position == bookmarks.workouts.size - 1) {
                        binding.view.viewGone()
                    }
                    Glide.with(mContext)
                        .load(bookmarks.workouts[position].bookmarked_item.image_path)
                        .into(binding.iv)
                    binding.tvWeeks.text =
                        "${bookmarks.workouts[position].bookmarked_item.duration} ${bookmarks.workouts[position].bookmarked_item.unit}"
                    binding.tvName.text = bookmarks.workouts[position].bookmarked_item.name
                    binding.tvLevel.text = "Level ${bookmarks.workouts[position].bookmarked_item.level}"
                    binding.llLevelsAndWeeks.viewVisible()
                }
            }
            binding.root.setOnClickListener {
                AppUtils.preventDoubleClick(binding.root)
                when (type) {
                    TYPE_INSIGHT -> {
                        val p1 = Pair.create<View, String>((binding.iv as View?)!!, "transitionImage")
                        val p2 = Pair.create<View, String>((binding.tvName as View?)!!, "title")
                        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            (mContext as AppCompatActivity),
                            p1, p2
                        )
                        listener.onItemClick(position, options)
                    }
                    TYPE_FOOD -> {
                        val p1 = Pair.create<View, String>((binding.iv as View?)!!, "transitionImage")
                        val p2 = Pair.create<View, String>((binding.tvName as View?)!!, "title")
                        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            (mContext as AppCompatActivity),
                            p1, p2
                        )
                        listener.onItemClick(position, options)
                    }
                    TYPE_WORKOUT -> {
                        val p1 = Pair.create<View, String>((binding.iv as View?)!!, "transitionImage")
                        val p2 = Pair.create<View, String>((binding.tvName as View?)!!, "title")
                        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            (mContext as AppCompatActivity),
                            p1, p2
                        )
                        listener.onItemClick(position, options)
                    }
                }

            }
        }
    }

    inner class ShopViewHolder(val binding: ItemHistoryShopsTabBinding, val listener: BookmarksClickListener) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(position: Int) {
            Glide.with(mContext)
                .load(bookmarks.products[position].bookmarked_item.first_image)
                .into(binding.imgMain)
            binding.tvActualPrice.text = bookmarks.products[position].bookmarked_item.minimum_price.compare_at_price
            binding.tvDiscountedPrice.text = bookmarks.products[position].bookmarked_item.minimum_price.price
            if (bookmarks.products[position].bookmarked_item.sub_category_name != null)
                binding.tvCategory.text = bookmarks.products[position].bookmarked_item.sub_category_name.name
            else
                binding.tvCategory.text = bookmarks.products[position].bookmarked_item.category_name.name
            binding.tvName.text = bookmarks.products[position].bookmarked_item.name
            binding.root.setOnClickListener {
                val p1 = Pair.create<View, String>((binding.imgMain as View?)!!, "transitionImage")
                val p2 = Pair.create<View, String>((binding.tvName as View?)!!, "title")
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (mContext as AppCompatActivity),
                    p1, p2
                )
                listener.onItemClick(position, options)
            }
        }
    }

    interface BookmarksClickListener {
        fun onItemClick(position: Int, option: ActivityOptionsCompat)
    }

}