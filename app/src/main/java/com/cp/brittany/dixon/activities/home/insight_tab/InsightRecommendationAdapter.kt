package com.cp.brittany.dixon.activities.home.insight_tab

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.insight_rec_detail.InsightRecommendationDetailActivity
import com.cp.brittany.dixon.activities.home.models.InsightsRecommendationData
import com.cp.brittany.dixon.databinding.ItemInsightRecommendationBinding
import com.bumptech.glide.Glide
import com.cp.brittany.dixon.utils.AppUtils

class InsightRecommendationAdapter(
    var insightRecommendations: MutableList<InsightsRecommendationData>,
    val recommendationsInterface: RecommendationsInterface
) :
    RecyclerView.Adapter<InsightRecommendationAdapter.ViewHolder>() {

    private lateinit var mContext: Context

    interface RecommendationsInterface {
        fun onItemClicked(position: Int, options: ActivityOptionsCompat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemInsightRecommendationBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_insight_recommendation,
            parent,
            false
        )
        return ViewHolder(binding, recommendationsInterface)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int = insightRecommendations.size

    inner class ViewHolder(
        var binding: ItemInsightRecommendationBinding,
        val listener: RecommendationsInterface
    ) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(position: Int) {
            binding.tvTitle.text = insightRecommendations[position].name
            binding.tvTime.text = insightRecommendations[position].duration

            Glide.with(mContext)
                .load(insightRecommendations[position].image_path)
                .into(binding.ivRecommendation)

            itemView.setOnClickListener {
                AppUtils.preventDoubleClick(itemView)
                //recommendationsInterface.onItemClicked(position)

//                val intent = Intent(mContext, InsightRecommendationDetailActivity::class.java)
//                intent.putExtra("id", insightRecommendations[position].id)
//                intent.putExtra("insightImage", insightRecommendations[position].image_path)
//                intent.putExtra("time", insightRecommendations[position].duration)
//                intent.putExtra("unit", insightRecommendations[position].unit)
//                intent.putExtra("title", insightRecommendations[position].name)
//                intent.putExtra("date", insightRecommendations[position].date)
//                intent.putExtra("isBookmarked", insightRecommendations[position].is_bookmarked)
//                intent.putExtra("details", insightRecommendations[position].detail)
//                intent.putExtra("totalLikes", insightRecommendations[position].total_likes)
//                intent.putExtra("isLiked", insightRecommendations[position].is_liked)
//                intent.putExtra("created_at", insightRecommendations[position].created_at)
                val p1 = Pair.create<View, String>((binding.ivRecommendation as View?)!!, "transitionImage")
                val p2 = Pair.create<View, String>((binding.tvTitle as View?)!!, "title")
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (mContext as AppCompatActivity),
                    p1, p2
                )
                listener.onItemClicked(position, options)
            }

        }
    }
}