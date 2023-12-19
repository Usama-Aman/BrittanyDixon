package com.cp.brittany.dixon.activities.home.profile_tab.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.models.BookmarkModelData
import com.cp.brittany.dixon.databinding.ItemBookmarkBinding

class BookmarksAdapter(private val bookmarkList: ArrayList<BookmarkModelData>) : RecyclerView.Adapter<BookmarksAdapter.ViewHolder>() {
    private lateinit var mContext: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarksAdapter.ViewHolder {
        mContext = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemBookmarkBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_bookmark,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookmarksAdapter.ViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int = bookmarkList.size

    inner class ViewHolder(val binding: ItemBookmarkBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(position: Int) {
            when (position) {
//                0 -> {
//                    binding.rv.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
//                    binding.rv.adapter = BookmarksInnerAdapter(TYPE_WORKOUT)
//                    binding.ivCategory.setImageResource(R.drawable.ic_workout)
//                }
//                1 -> {
//                    binding.rv.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
//                    binding.rv.adapter = BookmarksInnerAdapter(TYPE_SHOP)
//                    binding.ivCategory.setImageResource(R.drawable.ic_shop)
//                }
//                2 -> {
//                    binding.rv.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
//                    binding.rv.adapter = BookmarksInnerAdapter(TYPE_FOOD)
//                    binding.ivCategory.setImageResource(R.drawable.ic_food)
//                }
//                3 -> {
//                    binding.rv.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
//                    binding.rv.adapter = BookmarksInnerAdapter(TYPE_INSIGHT)
//                    binding.ivCategory.setImageResource(R.drawable.ic_insight)
//                }
            }
            //binding.rv.adapter = BookmarksInnerAdapter()
        }
    }

}