package com.cp.brittany.dixon.activities.home.search.insight_tab

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.cp.brittany.dixon.activities.home.insight_rec_detail.InsightRecommendationDetailActivity
import com.cp.brittany.dixon.activities.home.models.SearchInsightModel
import com.cp.brittany.dixon.activities.home.models.SearchInsightResponse
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.databinding.FragmentSearchInsightBinding
import com.cp.brittany.dixon.loader.Loader
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.*
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call

class SearchInsightFragment : Fragment() {

    private lateinit var binding: FragmentSearchInsightBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>
    private lateinit var mContext: Context

    private var insightFilterList: MutableList<SearchInsightModel?> = ArrayList()

    private val myAdapter: SearchInsightAdapter by lazy {
        SearchInsightAdapter(insightFilterList, object : SearchInsightAdapter.InsightSearchInterface {
            override fun onItemClicked(position: Int) {
                val i = Intent(mContext, InsightRecommendationDetailActivity::class.java)
                i.putExtra("id", insightFilterList[position]?.id)
                i.putExtra("insightImage", insightFilterList[position]?.image_path)
                i.putExtra("time", insightFilterList[position]?.duration)
                i.putExtra("unit", insightFilterList[position]?.unit)
                i.putExtra("title", insightFilterList[position]?.name)
                i.putExtra("date", insightFilterList[position]?.date)
                i.putExtra("isBookmarked", insightFilterList[position]?.is_bookmarked)
                i.putExtra("details", insightFilterList[position]?.detail)
                i.putExtra("totalLikes", insightFilterList[position]?.total_likes)
                i.putExtra("isLiked", insightFilterList[position]?.is_liked)
                i.putExtra("created_at", insightFilterList[position]?.created_at)
                mContext.startActivity(i)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSearchInsightBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = requireContext()
        retrofitClient = RetrofitClient.getClient(mContext).create(Api::class.java)

        initViews()
        observeApiResponse()
        setAdapter()

        getInsightFilterData()
    }

    private fun initViews() {
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        binding.lifecycleOwner = this
    }


    private fun setAdapter() {
        binding.rvInsight.let {
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = myAdapter
        }
    }

    fun hitApi(search: String, filters: InsightFilterResult?) {
        if (filters == null)
            getInsightFilterData(search = search)
        else
            getInsightFilterData(filters.categoryId, filters.duration_value.toInt(), search)
    }


    private fun getInsightFilterData(categoryId: Int = 0, maxDuration: Int = 0, search: String = "") {
        apiCall = retrofitClient.getInsightFilterData(categoryId, maxDuration, search)
        viewModel.getInsightSearchFilter(apiCall)
    }

    private fun observeApiResponse() {
        viewModel.getApiResponse().observe(requireActivity(), {
            when (it.status) {
                ApiStatus.LOADING -> {
                    Loader.showLoader((requireActivity() as AppCompatActivity)) {
                        if (this::apiCall.isInitialized)
                            apiCall.cancel()
                    }
                }
                ApiStatus.ERROR -> {
                    Loader.hideLoader()
                    AppUtils.showToast(requireActivity(), it.message!!, false)
                }
                ApiStatus.SUCCESS -> {
                    Loader.hideLoader()
                    when (it.tag) {
                        ApiTags.SEARCH_INSIGHT_FILTER -> {
                            insightFilterList.clear()
                            val model = Gson().fromJson(it.data.toString(), SearchInsightResponse::class.java)
                            if (model.data != null)
                                insightFilterList.addAll(model.data)
                            if (insightFilterList.isNullOrEmpty()) {
                                binding.rvInsight.viewGone()
                                binding.llNoData.viewVisible()
                            } else {
                                binding.rvInsight.viewVisible()
                                binding.llNoData.viewGone()
                            }
                            myAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        })
    }
}