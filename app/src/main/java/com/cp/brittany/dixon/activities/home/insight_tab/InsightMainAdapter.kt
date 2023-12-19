package com.cp.brittany.dixon.activities.home.insight_tab

import android.annotation.SuppressLint
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
import com.cp.brittany.dixon.activities.home.models.InsightWithCategoryData
import com.cp.brittany.dixon.databinding.ItemInsightMainBinding
import com.bumptech.glide.Glide
import com.cp.brittany.dixon.utils.AppUtils

class InsightMainAdapter(
    var insightWithCategoryList: ArrayList<InsightWithCategoryData>,
    val insightMainInterface: InsightMainInterface
) :
    RecyclerView.Adapter<InsightMainAdapter.ViewHolder>() {

    private lateinit var mContext: Context

    interface InsightMainInterface {
        fun onItemClicked(position: Int, options: ActivityOptionsCompat)
        fun onBookmarkClicked(position: Int)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemInsightMainBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_insight_main,
            parent,
            false
        )
        return ViewHolder(binding, insightMainInterface)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int = insightWithCategoryList.size

    inner class ViewHolder(var binding: ItemInsightMainBinding,
    val listener: InsightMainInterface) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun onBind(position: Int) {

            binding.tvDescription.text = insightWithCategoryList[position].name
            binding.tvTime.text = "${insightWithCategoryList[position].duration}${insightWithCategoryList[position].unit}"
            if(insightWithCategoryList[position].is_bookmarked == 0){
                binding.ivBookmark.setImageResource(R.drawable.ic_heart)
            }
            else
                binding.ivBookmark.setImageResource(R.drawable.ic_heart_selected)
            Glide.with(mContext)
                .load(insightWithCategoryList[position].image_path)
                .into(binding.ivRecommendation)
            binding.ivBookmark.setOnClickListener {
                AppUtils.preventDoubleClick(binding.ivBookmark)
                listener.onBookmarkClicked(position)
            }
            itemView.setOnClickListener {
                AppUtils.preventDoubleClick(itemView)
                //insightMainInterface.onItemClicked(position)
//                val intent = Intent(mContext, InsightRecommendationDetailActivity::class.java)
//                intent.putExtra("id", insightWithCategoryList[position].id)
//                intent.putExtra("insightImage", insightWithCategoryList[position].image_path)
//                intent.putExtra("time", insightWithCategoryList[position].duration)
//                intent.putExtra("unit", insightWithCategoryList[position].unit)
//                intent.putExtra("title", insightWithCategoryList[position].name)
//                intent.putExtra("date", insightWithCategoryList[position].date)
//                intent.putExtra("isBookmarked", insightWithCategoryList[position].is_bookmarked)
//                intent.putExtra("details", insightWithCategoryList[position].detail)
//                intent.putExtra("totalLikes", insightWithCategoryList[position].total_likes)
//                intent.putExtra("isLiked", insightWithCategoryList[position].is_liked)
//                intent.putExtra("created_at", insightWithCategoryList[position].created_at)
                val p1 = Pair.create<View, String>((binding.ivRecommendation as View?)!!, "transitionImage")
                val p2 = Pair.create<View, String>((binding.tvDescription as View?)!!, "title")
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (mContext as AppCompatActivity),
                    p1, p2
                )
                listener.onItemClicked(position, options)
                //mContext.startActivity(intent, options.toBundle())
            }


        }
    }
}