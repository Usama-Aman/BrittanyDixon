package com.cp.brittany.dixon.activities.home.search

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.food_tab.RecipeDetailActivity
import com.cp.brittany.dixon.activities.home.home_models.FoodsList
import com.cp.brittany.dixon.activities.home.home_models.FoodsModel
import com.cp.brittany.dixon.activities.home.insight_rec_detail.InsightRecommendationDetailActivity
import com.cp.brittany.dixon.activities.home.models.*
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivitySimpleFoodSearchBinding
import com.cp.brittany.dixon.loader.Loader
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.ApiStatus
import com.cp.brittany.dixon.utils.AppUtils
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call

class SimpleFoodSearchActivity : BaseActivity() {
    private val FOOD_TYPE = 0
    private val INSIGHT_Type = 1
    private lateinit var binding: ActivitySimpleFoodSearchBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>
    private lateinit var searchAdapter: SimpleSearchAdapter
    private var activityType: Int = -1
    private var foodsList: MutableList<FoodsList> = ArrayList<FoodsList>()
    private var insightRecommendations: MutableList<InsightsRecommendationData> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySimpleFoodSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()
        retrofitClient = RetrofitClient.getClient(this).create(Api::class.java)

        initViews()
        initVM()
        observeApiResponse()
        initListeners()
        initAdapter()
        initSearch("")
    }

    private fun initViews() {
        onSetupViewGroup(binding.content)
        if (intent != null) {
            activityType = intent.getIntExtra("type", -1)
            if (activityType == FOOD_TYPE) {
                binding.tvHead.text = "Food"
            } else {
                binding.tvHead.text = "Insight"
            }
        }
    }

    private fun initAdapter() {
        searchAdapter = SimpleSearchAdapter(object : SimpleSearchAdapter.RecipeClickListener {
            override fun onItemClicked(position: Int, type: Int) {
                if(type == FOOD_TYPE){
                    val foodId = foodsList[position].id.toString()
                    val intent = Intent(applicationContext, RecipeDetailActivity::class.java)
                    intent.putExtra("foodId", foodId)
                    intent.putExtra("image", foodsList[position].image_path)
                    if (!foodsList[position].video.isNullOrEmpty())
                        intent.putExtra("videoPath", foodsList[position].video_path)
                    startActivity(intent)
                }
                else{
                    val i = Intent(applicationContext, InsightRecommendationDetailActivity::class.java)
                    i.putExtra("id", insightRecommendations[position].id)
                    i.putExtra("insightImage", insightRecommendations[position].image_path)
                    i.putExtra("time", insightRecommendations[position].duration)
                    i.putExtra("unit", insightRecommendations[position].unit)
                    i.putExtra("title", insightRecommendations[position].name)
                    i.putExtra("date", insightRecommendations[position].date)
                    i.putExtra("isBookmarked", insightRecommendations[position].is_bookmarked)
                    i.putExtra("details", insightRecommendations[position].detail)
                    i.putExtra("totalLikes", insightRecommendations[position].total_likes)
                    i.putExtra("isLiked", insightRecommendations[position].is_liked)
                    i.putExtra("created_at", insightRecommendations[position].created_at)
                    startActivity(i)
                }
            }
        }, activityType)
        binding.rvFindRecipes.adapter = searchAdapter
    }

    private fun initVM() {
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        binding.lifecycleOwner = this
    }

    private fun initListeners() {
        binding.ivBack.setOnClickListener {
            AppUtils.preventDoubleClick(binding.ivBack)
            setResult(Activity.RESULT_CANCELED, null)
            onBackPressed()
        }
        binding.etSearch.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                initSearch(binding.etSearch.text.toString())
            }
            true
        })
    }

    private fun initSearch(search: String) {
        if (activityType == FOOD_TYPE) {
            apiCall = retrofitClient.simpleSearchFoodByName(search)
            viewModel.simpleSearchFoodByName(apiCall)
        } else {
            apiCall = retrofitClient.simpleSearchInsightByName(search)
            viewModel.simpleSearchInsightByName(apiCall)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeApiResponse() {
        viewModel.getApiResponse().observe(this, {
            when (it.status) {
                ApiStatus.LOADING -> {
                    Loader.showLoader(this) {
                        if (this::apiCall.isInitialized)
                            apiCall.cancel()
                    }

                }
                ApiStatus.ERROR -> {
                    Loader.hideLoader()
                    AppUtils.showToast(this, it.message!!, false)
                }
                ApiStatus.SUCCESS -> {
                    Loader.hideLoader()
                    when (it.tag) {
                        ApiTags.FOOD_SIMPLE_SEARCH -> {
                            val model = Gson().fromJson(it.data.toString(), FoodsModel::class.java)
                            setAllFoods(model)
                        }
                        ApiTags.SIMPLE_SEARCH_INSIGHT_BY_NAME -> {
                            val model = Gson().fromJson(it.data.toString(), InsightsRecommendationModel::class.java)
                            setAllInsights(model)
                        }

                    }
                }
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setAllFoods(model: FoodsModel) {
        foodsList.clear()
        if (model.data == null || model.data.isEmpty()) {
            binding.noData.visibility = View.VISIBLE
        } else {
            binding.noData.visibility = View.GONE
            foodsList.addAll(model.data)
        }
        searchAdapter.setFoodList(foodsList)
        binding.rvFindRecipes.adapter?.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setAllInsights(model: InsightsRecommendationModel) {
        insightRecommendations.clear()
        if (model.data == null || model.data.isEmpty()) {
            binding.noData.visibility = View.VISIBLE
        } else {
            binding.noData.visibility = View.GONE
            insightRecommendations.addAll(model.data)
        }

        searchAdapter.setInsightList(insightRecommendations)
        binding.rvFindRecipes.adapter?.notifyDataSetChanged()
    }
}