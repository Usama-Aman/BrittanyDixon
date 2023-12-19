package com.cp.brittany.dixon.activities.home.podcast

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.databinding.ItemPodcastListBinding
import com.cp.brittany.dixon.utils.viewGone
import com.cp.brittany.dixon.utils.viewVisible
import java.text.SimpleDateFormat
import java.util.*

class PodcastsAdapter(val podcastsList: MutableList<PodcastsData>, val podcastInterface: PodcastInterface) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var mContext: Context

    interface PodcastInterface {
        fun onPodcastClicked(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mContext = parent.context
        return Item(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), (R.layout.item_podcast_list), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is Item -> holder.bind(position)
        }
    }

    override fun getItemCount(): Int = podcastsList.size

    inner class Item(val binding: ItemPodcastListBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {

            Glide.with(mContext)
                .load(podcastsList[position].podcast_thumbnail_path)
                .placeholder(R.drawable.ic_placeholder)
                .into(binding.ivPodcast)

            binding.tvPodcastTitle.text = podcastsList[position].name
            binding.tvPodcastSingerName.text = "${podcastsList[position].artist_name} | ${podcastsList[position].album_name}"
            binding.tvPodcastDuration.text = getTheTime(podcastsList[position].duration)

            if (podcastsList[position].isPlaying) {
                binding.ivPodcastPlaying.viewVisible()
                binding.constraintItemPodcast.setBackgroundColor(ContextCompat.getColor(mContext, R.color.blue_grey_500_alpha_8))
            } else {
                binding.ivPodcastPlaying.viewGone()
                binding.constraintItemPodcast.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white))
            }

            binding.constraintItemPodcast.setOnClickListener {
                podcastInterface.onPodcastClicked(position)
            }

        }

        @SuppressLint("SimpleDateFormat")
        private fun getTheTime(currentVideoDuration: Int): String {
            val date = Date(currentVideoDuration * 1000L) //260000 milliseconds
            val sdf = SimpleDateFormat("mm:ss")
            return sdf.format(date)
        }

    }
}