package com.cp.brittany.dixon.activities.home.profile_tab

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProviders
import com.cp.brittany.dixon.activities.home.food_tab.RecipeDetailActivity
import com.cp.brittany.dixon.activities.home.insight_rec_detail.InsightRecommendationDetailActivity
import com.cp.brittany.dixon.activities.home.models.BookmarkModel
import com.cp.brittany.dixon.activities.home.models.BookmarkModelData
import com.cp.brittany.dixon.activities.home.profile_tab.adapters.*
import com.cp.brittany.dixon.activities.home.workout_detail.WorkoutDetailActivity
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.base.BaseActivityResult
import com.cp.brittany.dixon.databinding.ActivityBookmaerkDetailsBinding
import com.cp.brittany.dixon.loader.Loader
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.ApiStatus
import com.cp.brittany.dixon.utils.AppUtils
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call

class BookmarkDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityBookmaerkDetailsBinding
    private lateinit var bookmark: BookmarkModelData
    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>
    private var type = -1
    val intentResult = Intent()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookmaerkDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        retrofitClient = RetrofitClient.getClient(applicationContext).create(Api::class.java)
        blackStatusBarIcons()
        initViews()
        initVM()
        getWorkoutBookmarks()
        observeApiResponse()
    }

    @SuppressLint("SetTextI18n")
    private fun initViews() {
        type = intent.getIntExtra("type", -1)
        when (type) {
            TYPE_WORKOUT -> {
                binding.tvTitle.text = "Workout Bookmarks"
            }
            TYPE_INSIGHT -> {
                binding.tvTitle.text = "Insights Bookmarks"
            }
            TYPE_FOOD -> {
                binding.tvTitle.text = "Foods Bookmarks"
            }
        }
    }

    private fun initVM() {
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        binding.lifecycleOwner = this
    }

    private fun initAdapter() {
        binding.rv.adapter = BookmarksInnerAdapter(bookmark, type, object : BookmarksInnerAdapter.BookmarksClickListener {
            override fun onItemClick(position: Int, option: ActivityOptionsCompat) {
                when (type) {
                    TYPE_WORKOUT -> {
                        val intent = Intent(applicationContext, WorkoutDetailActivity::class.java)
                        intent.putExtra("workoutId", bookmark.workouts[position].bookmarked_item.id.toString())
                        intent.putExtra("workoutImage", bookmark.workouts[position].bookmarked_item.image_path)
                        intent.putExtra("isBookmarked", bookmark.workouts[position].bookmarked_item.is_bookmarked)
                        intent.putExtra("is_free", bookmark.workouts[position].bookmarked_item.is_free)
                        activityLauncher.launch(intent, option, object : BaseActivityResult.OnActivityResult<ActivityResult> {
                            @SuppressLint("NotifyDataSetChanged")
                            override fun onActivityResult(result: ActivityResult) {
                                if (result.resultCode == Activity.RESULT_OK) {
                                    if (result.data != null) {
                                        val isBookmarked = result.data?.getIntExtra("bookmark", -1)
                                        if (isBookmarked != bookmark.workouts[position].bookmarked_item.is_bookmarked) {
                                            if (isBookmarked == 0) {
                                                bookmark.workouts.removeAt(position)
                                                binding.rv.adapter?.notifyDataSetChanged()
                                                setIntentData()
                                            }
                                        }
                                    }
                                }
                            }
                        })
                    }
                    TYPE_INSIGHT -> {
                        val intent = Intent(applicationContext, InsightRecommendationDetailActivity::class.java)
                        intent.putExtra("isFromBookmark", true)
                        intent.putExtra("id", bookmark.insights[position].bookmarked_item.id)
                        intent.putExtra("insightImage", bookmark.insights[position].bookmarked_item.image_path)
                        intent.putExtra("time", bookmark.insights[position].bookmarked_item.duration)
                        intent.putExtra("unit", bookmark.insights[position].bookmarked_item.unit)
                        intent.putExtra("title", bookmark.insights[position].bookmarked_item.name)
                        intent.putExtra("date", bookmark.insights[position].bookmarked_item.date)
                        intent.putExtra("isBookmarked", bookmark.insights[position].bookmarked_item.is_bookmarked)
                        intent.putExtra("details", bookmark.insights[position].bookmarked_item.detail)
                        intent.putExtra("totalLikes", bookmark.insights[position].bookmarked_item.total_likes)
                        intent.putExtra("isLiked", bookmark.insights[position].bookmarked_item.is_liked)
                        intent.putExtra("created_at", bookmark.insights[position].bookmarked_item.created_at)
                        activityLauncher.launch(intent, option, object : BaseActivityResult.OnActivityResult<ActivityResult> {
                            @SuppressLint("NotifyDataSetChanged")
                            override fun onActivityResult(result: ActivityResult) {
                                if (result.resultCode == Activity.RESULT_OK) {
                                    if (result.data != null) {
                                        val isBookmarked = result.data?.getIntExtra("bookmark", -1)
                                        if (isBookmarked != bookmark.insights[position].bookmarked_item.is_bookmarked) {
                                            if (isBookmarked == 0) {
                                                bookmark.insights.removeAt(position)
                                                binding.rv.adapter?.notifyDataSetChanged()
                                                setIntentData()
                                            }
                                        }
                                    }
                                }
                            }
                        })
                    }
                    TYPE_FOOD -> {
                        val intent = Intent(applicationContext, RecipeDetailActivity::class.java)
                        intent.putExtra("foodId", bookmark.foods[position].bookmarked_item.id.toString())
                        intent.putExtra("image", bookmark.foods[position].bookmarked_item.image_path)
                        intent.putExtra("time", bookmark.foods[position].bookmarked_item.time)
                        intent.putExtra("type", bookmark.foods[position].bookmarked_item.type)
                        intent.putExtra("calories", bookmark.foods[position].bookmarked_item.calories)
                        intent.putExtra("unit", bookmark.foods[position].bookmarked_item.unit)
                        intent.putExtra("title", bookmark.foods[position].bookmarked_item.name)
                        intent.putExtra("isBookmarked", bookmark.foods[position].bookmarked_item.is_bookmarked)
                        if (!bookmark.foods[position].bookmarked_item.video.isNullOrEmpty())
                            intent.putExtra("videoPath", bookmark.foods[position].bookmarked_item.video_path)
                        activityLauncher.launch(intent, option, object : BaseActivityResult.OnActivityResult<ActivityResult> {
                            @SuppressLint("NotifyDataSetChanged")
                            override fun onActivityResult(result: ActivityResult) {
                                if (result.resultCode == Activity.RESULT_OK) {
                                    if (result.data != null) {
                                        val isBookmarked = result.data?.getIntExtra("bookmark", -1)
                                        if (isBookmarked != bookmark.foods[position].bookmarked_item.is_bookmarked) {
                                            if (isBookmarked == 0) {
                                                bookmark.foods.removeAt(position)
                                                binding.rv.adapter?.notifyDataSetChanged()
                                                setIntentData()
                                            }
                                        }
                                    }
                                }
                            }
                        })
                    }
                }

            }

        })
    }

    private fun getWorkoutBookmarks() {

        apiCall = retrofitClient.getWorkoutBookmark("all")
        viewModel.getWorkoutBookmarks(apiCall)
    }

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
                    AppUtils.showToast(this@BookmarkDetailsActivity, it.message!!, false)
                }
                ApiStatus.SUCCESS -> {
                    Loader.hideLoader()
                    when (it.tag) {
                        ApiTags.GET_BOOKMARK -> {
                            val model = Gson().fromJson(it.data.toString(), BookmarkModel::class.java)
                            setBookMarks(model)
                        }
                    }
                }
            }
        })
    }

    private fun setBookMarks(model: BookmarkModel) {
        if (model != null && model.data != null) {
            //binding.noData.visibility = View.GONE
            bookmark = model.data
            initAdapter()
        }
    }

    private fun setIntentData() {

        when (type) {
            TYPE_WORKOUT -> {
                intentResult.putExtra(
                    "size",
                    if (bookmark.workouts.isNullOrEmpty())
                        0
                    else
                        bookmark.workouts.size
                )
            }
            TYPE_INSIGHT -> {
                intentResult.putExtra(
                    "size",
                    if (bookmark.insights.isNullOrEmpty())
                        0
                    else
                        bookmark.insights.size
                )
            }
            TYPE_FOOD -> {
                intentResult.putExtra(
                    "size",
                    if (bookmark.foods.isNullOrEmpty())
                        0
                    else
                        bookmark.foods.size
                )
            }
        }
        setResult(Activity.RESULT_OK, intentResult)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(!intentResult.hasExtra("size")){
            setResult(Activity.RESULT_CANCELED)
        }
    }
}