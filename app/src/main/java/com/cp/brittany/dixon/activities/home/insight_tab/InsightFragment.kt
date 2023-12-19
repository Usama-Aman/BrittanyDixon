package com.cp.brittany.dixon.activities.home.insight_tab

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.HomeActivity
import com.cp.brittany.dixon.activities.home.insight_rec_detail.InsightRecommendationDetailActivity
import com.cp.brittany.dixon.activities.home.models.*
import com.cp.brittany.dixon.activities.home.podcast.ComingPodcastActivity
import com.cp.brittany.dixon.activities.home.podcast.PlayPodcastActivity
import com.cp.brittany.dixon.activities.home.search.SimpleFoodSearchActivity
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.base.BaseActivityResult
import com.cp.brittany.dixon.databinding.FragmentInsightBinding
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.ApiStatus
import com.cp.brittany.dixon.utils.AppUtils
import com.cp.brittany.dixon.utils.viewGone
import com.cp.brittany.dixon.utils.viewVisible
import com.faltenreich.skeletonlayout.Skeleton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.transition.MaterialFadeThrough
import com.google.gson.Gson
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import okhttp3.ResponseBody
import retrofit2.Call

class InsightFragment : Fragment() {
    private lateinit var binding: FragmentInsightBinding
    private lateinit var baseActivity: BaseActivity
    private lateinit var recommendAdapter: InsightRecommendationAdapter
    private lateinit var mainAdapter: InsightMainAdapter

    private lateinit var mContext: Context
    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>

    private var insightsRecommendations: MutableList<InsightsRecommendationData> = ArrayList()
    private var tabsCategoriesList: ArrayList<InsightCategoriesData> = ArrayList()
    private var insightWithCategories: ArrayList<InsightWithCategoryData> = ArrayList()
    private var isPullRefresh = false

    private var categoryId = 0
    private var isGuest = 0
    private var canLoadMore = false
    private var skip = 0
    private var take = 10
    private lateinit var skeletonLayoutRecomendations: Skeleton
    private lateinit var skeletonLayoutTabs: Skeleton
    private lateinit var skeletonLayoutRvMain: Skeleton
    private var selectedBookmark = -1
    val activityLauncher: BaseActivityResult<Intent, ActivityResult> =
        BaseActivityResult.registerActivityForResult(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentInsightBinding.inflate(layoutInflater, container, false)
        skeletonLayoutRecomendations = binding.skeletonLayoutRecomendations
        skeletonLayoutTabs = binding.skeletonLayoutTabs
        skeletonLayoutRvMain = binding.skeletonLayoutRVMain
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as HomeActivity).changeBarColor(R.color.grey_50)
        retrofitClient = RetrofitClient.getClient(requireContext()).create(Api::class.java)
        baseActivity = BaseActivity()
        mContext = requireContext()
        val data = AppUtils.getUserDetails(requireContext())
        isGuest = data.is_guest
        initVM()
        observeApiResponse()
        initAdapter()
        initListeners()
        getInsightRecommendations()
    }

    private fun initListeners() {
        binding.tvSearch.setOnClickListener {
            AppUtils.preventDoubleClick(binding.tvSearch)
            val intent = Intent(requireContext(), SimpleFoodSearchActivity::class.java)
            intent.putExtra("type", 1)
            startActivity(intent)
        }
//        binding.pullToRefresh.setOnRefreshListener {
//            isPullRefresh = true
//            getInsightsWithCategory(categoryId)
//            //binding.pullToRefresh.isRefreshing = false
//        }

        binding.cvPodcast.setOnClickListener {
            AppUtils.preventDoubleClick(binding.cvPodcast)
//            getInsightRecommendations()
//            startActivity(Intent(mContext, PlayPodcastActivity::class.java))
            startActivity(Intent(mContext, ComingPodcastActivity::class.java))
        }
    }

    private fun initAdapter() {
        recommendAdapter = InsightRecommendationAdapter(insightsRecommendations,
            object : InsightRecommendationAdapter.RecommendationsInterface {
                override fun onItemClicked(position: Int, options: ActivityOptionsCompat) {
                    val i = Intent(mContext, InsightRecommendationDetailActivity::class.java)
                    i.putExtra("id", insightsRecommendations[position].id)
                    i.putExtra("insightImage", insightsRecommendations[position].image_path)
                    i.putExtra("time", insightsRecommendations[position].duration)
                    i.putExtra("unit", insightsRecommendations[position].unit)
                    i.putExtra("title", insightsRecommendations[position].name)
                    i.putExtra("date", insightsRecommendations[position].date)
                    i.putExtra("isBookmarked", insightsRecommendations[position].is_bookmarked)
                    i.putExtra("details", insightsRecommendations[position].detail)
                    i.putExtra("totalLikes", insightsRecommendations[position].total_likes)
                    i.putExtra("isLiked", insightsRecommendations[position].is_liked)
                    i.putExtra("created_at", insightsRecommendations[position].created_at)
                    activityLauncher.launch(i, options, object : BaseActivityResult.OnActivityResult<ActivityResult> {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onActivityResult(result: ActivityResult) {
                            if (result.resultCode == Activity.RESULT_OK) {
                                if (result.data != null) {
                                    val isBookmarked = result.data?.getIntExtra("bookmark", -1)
                                    if (isBookmarked != insightsRecommendations[position].is_bookmarked) {
                                        if (isBookmarked == 1)
                                            insightsRecommendations[position].is_bookmarked = 1
                                        else if (isBookmarked == 0)
                                            insightsRecommendations[position].is_bookmarked = 0
                                    }
                                }
                            }
                        }
                    })
                }
            })
        binding.rvRecommendation.adapter = recommendAdapter
        // Horizontal
        OverScrollDecoratorHelper.setUpOverScroll(binding.rvRecommendation, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL)
        mainAdapter = InsightMainAdapter(insightWithCategories, object : InsightMainAdapter.InsightMainInterface {
            override fun onItemClicked(position: Int, options: ActivityOptionsCompat) {
//                val i = Intent(mContext, InsightRecommendationDetailActivity::class.java)
//                i.putExtra("id", insightWithCategories[position].id)
//                mContext.startActivity(i)
                val i = Intent(mContext, InsightRecommendationDetailActivity::class.java)
                i.putExtra("id", insightWithCategories[position].id)
                i.putExtra("insightImage", insightWithCategories[position].image_path)
                i.putExtra("time", insightWithCategories[position].duration)
                i.putExtra("unit", insightWithCategories[position].unit)
                i.putExtra("title", insightWithCategories[position].name)
                i.putExtra("date", insightWithCategories[position].date)
                i.putExtra("isBookmarked", insightWithCategories[position].is_bookmarked)
                i.putExtra("details", insightWithCategories[position].detail)
                i.putExtra("totalLikes", insightWithCategories[position].total_likes)
                i.putExtra("isLiked", insightWithCategories[position].is_liked)
                i.putExtra("created_at", insightWithCategories[position].created_at)
                activityLauncher.launch(i, options, object : BaseActivityResult.OnActivityResult<ActivityResult> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onActivityResult(result: ActivityResult) {
                        if (result.resultCode == Activity.RESULT_OK) {
                            if (result.data != null) {
                                val isBookmarked = result.data?.getIntExtra("bookmark", -1)
                                if (isBookmarked != insightWithCategories[position].is_bookmarked) {
                                    if (isBookmarked == 1)
                                        insightWithCategories[position].is_bookmarked = 1
                                    else if (isBookmarked == 0)
                                        insightWithCategories[position].is_bookmarked = 0
                                    binding.rvInsightMain.adapter?.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                })
            }

            override fun onBookmarkClicked(position: Int) {
                if (isGuest == 0) {
                    selectedBookmark = position
                    bookmarkInsight(insightWithCategories[position].id)
                } else
                    (activity as HomeActivity).showLoginDialog()
            }
        })
        binding.rvInsightMain.adapter = mainAdapter

        binding.scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            Log.e("position", "${scrollY}---${v.getChildAt(0).measuredHeight - v.measuredHeight}")
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                // end of the scroll view
                if (canLoadMore) {
                    canLoadMore = false
                    take += 10
                    skip = insightWithCategories.size
                    getInsightsWithCategory(categoryId)
                }
            }
        })
    }

    private fun initVM() {
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        binding.lifecycleOwner = this
    }

    private fun initTabs() {
        for (i in tabsCategoriesList.indices) {
            if (i == 0) {
                val tabTextView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_insight_category_tabs, null) as TextView
                tabTextView.text = tabsCategoriesList[i].name
                tabTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.tab_selected
                    )
                )
                tabTextView.typeface = baseActivity.fontMedium
                val tab = binding.insightCategoriesTab.newTab()
                tab.customView = tabTextView
                binding.insightCategoriesTab.addTab(tab)
            } else {
                val tabTextView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_insight_category_tabs, null) as TextView
                tabTextView.text = tabsCategoriesList[i].name
                val tab = binding.insightCategoriesTab.newTab()
                tab.customView = tabTextView
                binding.insightCategoriesTab.addTab(tab)
            }
        }
        binding.insightCategoriesTab.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val tabTextView = tab?.customView
                tabTextView?.findViewById<TextView>(R.id.tvTabTitle)
                    ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.tab_selected))
                tabTextView?.findViewById<TextView>(R.id.tvTabTitle)?.typeface =
                    baseActivity.fontMedium

                categoryId = tabsCategoriesList[tab?.position!!].id
                getInsightsWithCategory(categoryId)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val tabTextView = tab?.customView
                tabTextView?.findViewById<TextView>(R.id.tvTabTitle)
                    ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.tab_unselected))
                tabTextView?.findViewById<TextView>(R.id.tvTabTitle)?.typeface =
                    baseActivity.fontRegular
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeApiResponse() {
        viewModel.getApiResponse().observe(requireActivity(), {
            when (it.status) {
                ApiStatus.LOADING -> {
//                    if (it.tag != ApiTags.GET_INSIGHT_CATEGORIES)
//                        Loader.showLoader((requireActivity() as AppCompatActivity)) {
//                            if (this@InsightFragment::apiCall.isInitialized)
//                                apiCall.cancel()
//                        }
                }
                ApiStatus.ERROR -> {
                    //Loader.hideLoader()
                    AppUtils.showToast(requireActivity(), it.message!!, false)
                }
                ApiStatus.SUCCESS -> {
                    when (it.tag) {
                        ApiTags.GET_INSIGHT_RECOMMENDATIONS -> {
                            hideLoader()
                            val model = Gson().fromJson(it.data.toString(), InsightsRecommendationModel::class.java)
                            insightsRecommendations.clear()
                            insightsRecommendations.addAll(model.data)
                            recommendAdapter.notifyDataSetChanged()
                            if (insightsRecommendations.isEmpty()) {
                                binding.tvYourDaily.viewGone()
                                binding.tvRecommendation.viewGone()
                            } else {
                                binding.tvYourDaily.viewVisible()
                                binding.tvRecommendation.viewVisible()
                            }
                            //hideLoader()
                            getInsightsCategories()
                        }
                        ApiTags.GET_INSIGHT_CATEGORIES -> {
                            //Loader.hideLoader()
                            hideCategoryLoader()
                            val model = Gson().fromJson(it.data.toString(), InsightCategoriesModel::class.java)
                            tabsCategoriesList.clear()
                            tabsCategoriesList.add(InsightCategoriesData(name = "All"))
                            tabsCategoriesList.addAll(model.data)
                            if (binding.insightCategoriesTab.tabCount == 0) {
                                initTabs()
                            }
                            getInsightsWithCategory(categoryId)
                        }
                        ApiTags.GET_INSIGHT_WITH_CATEGORY -> {
                            hideRVLoader()
                            //Loader.hideLoader()
                            val model = Gson().fromJson(it.data.toString(), InsightWithCategoryModel::class.java)
                            insightWithCategories.clear()
                            insightWithCategories.addAll(model.data)

                            if (!insightWithCategories.isNullOrEmpty()) {
                                binding.noData.viewGone()
                            } else {
                                Handler(Looper.getMainLooper()).postDelayed({
                                    binding.rvInsightMain.viewGone()
                                    binding.noData.viewVisible()
                                }, 1000)

                            }

                            mainAdapter.notifyDataSetChanged()
                        }
                        ApiTags.BOOKMARK_THINGS -> {
                            val model = Gson().fromJson(it.data.toString(), InsightBookMarkModel::class.java)
                            AppUtils.showToast(requireActivity(), model.message, true)
                            if (model.message == "Bookmark added") {
                                if (selectedBookmark != -1)
                                    insightWithCategories[selectedBookmark].is_bookmarked = 1
                            } else {
                                if (selectedBookmark != -1)
                                    insightWithCategories[selectedBookmark].is_bookmarked = 0
                            }
                            binding.rvInsightMain.adapter?.notifyDataSetChanged()
                        }
                    }
                }
            }
        })
    }

    private fun getInsightRecommendations() {
        showLoadingAnimation()
        apiCall = retrofitClient.getInsightRecommendations()
        viewModel.getInsightRecommendations(apiCall)
    }

    private fun getInsightsCategories() {
        apiCall = retrofitClient.getInsightCategories()
        viewModel.getInsightCategories(apiCall)
    }

    private fun getInsightsWithCategory(id: Int) {
        showRVLoader()
        apiCall = retrofitClient.getInsightsWithCategory(id,skip,take)
        viewModel.getInsightsWithCategory(apiCall)
    }

    private fun showLoadingAnimation() {
        if (!isPullRefresh) {
            binding.noData.viewGone()
            binding.rvRecommendation.viewGone()
            binding.cvPodcast.viewGone()
            binding.insightCategoriesTab.viewGone()
            binding.rvInsightMain.viewGone()
            binding.skeletonLayoutRecomendations.viewVisible()
            binding.skeletonLayoutTabs.viewVisible()
            binding.skeletonLayoutRVMain.viewVisible()
            skeletonLayoutTabs.showSkeleton()
            skeletonLayoutRecomendations.showSkeleton()
            skeletonLayoutRvMain.showSkeleton()
        }

    }

    private fun showRVLoader() {
        if (!isPullRefresh) {
            binding.noData.viewGone()
            binding.rvInsightMain.viewGone()
            binding.skeletonLayoutRVMain.viewVisible()
            skeletonLayoutRvMain.showSkeleton()
        }
    }

    private fun hideRVLoader() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (insightsRecommendations.size > 0) {
                binding.rvRecommendation.scrollToPosition(0);
            }
            if (insightWithCategories.size > 0) {
                binding.rvInsightMain.scrollToPosition(0)
            }
            //binding.pullToRefresh.isRefreshing = false
            isPullRefresh = false
            binding.rvInsightMain.viewVisible()
            binding.skeletonLayoutRVMain.viewGone()
            skeletonLayoutRvMain.showOriginal()
        }, 1000)
    }

    private fun hideCategoryLoader() {
        binding.insightCategoriesTab.viewVisible()
        binding.skeletonLayoutTabs.viewGone()
        skeletonLayoutTabs.showOriginal()
    }

    private fun hideLoader() {
        binding.rvRecommendation.viewVisible()
        binding.cvPodcast.viewVisible()
        binding.skeletonLayoutRecomendations.viewGone()
        binding.skeletonLayoutTabs.viewGone()
        skeletonLayoutRecomendations.showOriginal()
    }

    private fun bookmarkInsight(insightId: Int) {
        apiCall = retrofitClient.bookmarkThings("insights", insightId)
        viewModel.bookmarkThings(apiCall)
    }
}