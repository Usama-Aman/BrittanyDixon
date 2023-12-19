package com.cp.brittany.dixon.activities.home.bookmarks.posts_tab

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
import com.cp.brittany.dixon.activities.home.models.BookMarkData
import com.cp.brittany.dixon.activities.home.models.BookmarkedItem
import com.cp.brittany.dixon.databinding.ItemBookmarksPostsBinding
import com.bumptech.glide.Glide

class BookmarksPostsAdapter(private val list: MutableList<BookMarkData>, private val mListener: BookmarkInterface) :
    RecyclerView.Adapter<BookmarksPostsAdapter.ViewHolder>() {
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        val binding: ItemBookmarksPostsBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_bookmarks_posts,
            parent,
            false
        )
        return ViewHolder(binding,mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(
        var binding: ItemBookmarksPostsBinding,
        val listener: BookmarkInterface
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(position: Int) {
            val model: BookmarkedItem = list[position].bookmarked_item
            Glide.with(context).load(model.image_path).centerCrop().into(binding.ivFoodImage)
            binding.tvDescription.text = model.name
            binding.tvTime.text = "${model.duration} ${model.unit}"
            binding.root.setOnClickListener {
//                listener.onItemClicked(position)
//                val intent = Intent(context, InsightRecommendationDetailActivity::class.java)
//                intent.putExtra("isFromBookmark", true)
//                intent.putExtra("id", model.id)
//                intent.putExtra("insightImage", model.image_path.toString())
//                intent.putExtra("title", model.name)
//                intent.putExtra("time", model.duration)
//                intent.putExtra("unit", model.unit)
                val p1 = Pair.create<View,String>((binding.ivFoodImage as View?)!!, "insightImage")
                val p2 = Pair.create<View,String>((binding.tvTime as View?)!!, "time")
                val p3 = Pair.create<View,String>((binding.tvDescription as View?)!!, "title")
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (context as AppCompatActivity),
                    p1,p2,p3
                )
                listener.onItemClicked(position,options)
                //context.startActivity(intent, options.toBundle())
            }
        }
    }
    interface BookmarkInterface {
        fun onItemClicked(position: Int, options: ActivityOptionsCompat)
    }
}