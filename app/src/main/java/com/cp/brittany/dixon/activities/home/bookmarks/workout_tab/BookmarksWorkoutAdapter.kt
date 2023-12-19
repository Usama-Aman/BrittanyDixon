package com.cp.brittany.dixon.activities.home.bookmarks.workout_tab

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
import com.cp.brittany.dixon.databinding.ItemBookmarksWorkoutBinding
import com.bumptech.glide.Glide

class BookmarksWorkoutAdapter(private val list: MutableList<BookMarkData>, private val mListner: BookmarkInterface) :
    RecyclerView.Adapter<BookmarksWorkoutAdapter.ViewHolder>() {
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        val binding: ItemBookmarksWorkoutBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_bookmarks_workout,
            parent,
            false
        )
        return ViewHolder(binding, mListner)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(
        var binding: ItemBookmarksWorkoutBinding,
        val listener: BookmarkInterface
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(position: Int) {
            val model: BookmarkedItem = list[position].bookmarked_item
            Glide.with(context).load(model.image_path).centerCrop().into(binding.ivImage)
            binding.tvLevel.text = "Level ${model.level}"
            binding.tvTitle.text = model.name
            binding.tvWeeks.text = "${model.duration} ${model.unit}"
            binding.root.setOnClickListener {
                val p1 = Pair.create<View,String>((binding.ivImage as View?)!!, "workoutImage")
                val p2 = Pair.create<View,String>((binding.tvTitle as View?)!!, "title")
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (context as AppCompatActivity),
                    p1,p2
                )
                listener.onItemClicked(position,options)
            }
        }
    }
    interface BookmarkInterface {
        fun onItemClicked(position: Int, options: ActivityOptionsCompat)
    }
}