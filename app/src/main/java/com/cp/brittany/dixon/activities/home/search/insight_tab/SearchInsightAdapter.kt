package com.cp.brittany.dixon.activities.home.search.insight_tab

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.models.SearchInsightModel
import com.cp.brittany.dixon.databinding.ItemInsightMainBinding
import com.bumptech.glide.Glide
import com.cp.brittany.dixon.utils.AppUtils
import com.cp.brittany.dixon.utils.viewGone

class SearchInsightAdapter(
    val myInsightSearchFilterList: MutableList<SearchInsightModel?>,
    val insightSearchInterface: InsightSearchInterface
) :
    RecyclerView.Adapter<SearchInsightAdapter.SearchViewHolder>() {

    private lateinit var mContext: Context

    interface InsightSearchInterface {
        fun onItemClicked(position: Int)
    }

    inner class SearchViewHolder(
        var binding: ItemInsightMainBinding,
        val searchInterface: InsightSearchInterface
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(position: Int) {
            val insightItem = myInsightSearchFilterList[position]
            binding.ivBookmark.viewGone()

            Glide.with(mContext)
                .load(insightItem?.image_path)
                .into(binding.ivRecommendation)

            binding.tvDescription.text = insightItem?.name
            binding.tvTime.text = insightItem?.duration + " " + insightItem?.unit
            binding.root.setOnClickListener {
                AppUtils.preventDoubleClick(binding.root)
                searchInterface.onItemClicked(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        mContext = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemInsightMainBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_insight_main,
            parent,
            false
        )
        return SearchViewHolder(binding, insightSearchInterface)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return myInsightSearchFilterList.size
    }

}