package com.cp.brittany.dixon.activities.home.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.result.ActivityResult
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.search.food_tab.FoodFilterData
import com.cp.brittany.dixon.activities.home.search.food_tab.FoodFilterModel
import com.cp.brittany.dixon.activities.home.search.food_tab.FoodFilterResult
import com.cp.brittany.dixon.activities.home.search.food_tab.SearchFoodFragment
import com.cp.brittany.dixon.activities.home.search.insight_tab.InsightFilterData
import com.cp.brittany.dixon.activities.home.search.insight_tab.InsightFilterModel
import com.cp.brittany.dixon.activities.home.search.insight_tab.InsightFilterResult
import com.cp.brittany.dixon.activities.home.search.insight_tab.SearchInsightFragment
import com.cp.brittany.dixon.activities.home.search.workout_tab.SearchWorkoutFragment
import com.cp.brittany.dixon.activities.home.search.workout_tab.WorkoutFilterData
import com.cp.brittany.dixon.activities.home.search.workout_tab.WorkoutFilterModel
import com.cp.brittany.dixon.activities.home.search.workout_tab.WorkoutFilterResult
import com.cp.brittany.dixon.activities.home.search_filters.FoodFilterActivity
import com.cp.brittany.dixon.activities.home.search_filters.InsightFiltersActivity
import com.cp.brittany.dixon.activities.home.search_filters.WorkoutFiltersActivity
import com.cp.brittany.dixon.activities.home.workout_tab.WorkoutFragment
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.base.BaseActivityResult
import com.cp.brittany.dixon.databinding.ActivitySearchBinding
import com.cp.brittany.dixon.loader.Loader
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.ApiStatus
import com.cp.brittany.dixon.utils.AppUtils
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call

class SearchActivity : BaseActivity() {

    lateinit var binding: ActivitySearchBinding
    private lateinit var vpf: ViewPagerAdapter
    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>

    private var workoutFragmentInstance: SearchWorkoutFragment? = null
    private var insightFragmentInstance: SearchInsightFragment? = null
    private var foodFragmentInstance: SearchFoodFragment? = null

    private var currentFragmentNo = 0 //0 For workout, 1 for insight, 2 for food fragment

    private var workoutFilterResult: WorkoutFilterResult? = null
    private var workoutFilters: WorkoutFilterData? = null

    private var insightFilters: InsightFilterData? = null
    private var insightFilterResult: InsightFilterResult? = null

    private var foodFilters: FoodFilterData? = null
    private var foodFilterResult: FoodFilterResult? = null
    private var isWorkoutFilterApplied = false
    private var isInsightFilterApplied = false
    private var isFoodFilterApplied = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()

        retrofitClient = RetrofitClient.getClient(this).create(Api::class.java)

        initViews()
        initVM()
        initListeners()
        setViewPager()
        observeApiResponse()

    }

    private fun initViews() {
        binding.etSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                || actionId == EditorInfo.IME_ACTION_DONE
            ) {
                when (currentFragmentNo) {
                    0 -> {
                        if (workoutFragmentInstance != null)
                            workoutFragmentInstance?.hitApi(binding.etSearch.text.toString(), workoutFilterResult)
                    }
                    1 -> {
                        if (insightFragmentInstance != null)
                            insightFragmentInstance?.hitApi(binding.etSearch.text.toString(), insightFilterResult)
                    }
                    2 -> {
                        if (foodFragmentInstance != null)
                            foodFragmentInstance?.hitApi(binding.etSearch.text.toString(), foodFilterResult)
                    }
                }
                true
            }
            // Return true if you have consumed the action, else false.
            false
        }
    }

    private fun initVM() {
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        binding.lifecycleOwner = this
    }

    private fun initListeners() {

        binding.tvWorkout.setOnClickListener {
            currentFragmentNo = 0
            binding.viewPager.setCurrentItem(0, false)
            if(isWorkoutFilterApplied){
                binding.ivFilterStatus.visibility = View.VISIBLE
            }
            else{
                binding.ivFilterStatus.visibility = View.GONE
            }
        }

        binding.tvInsight.setOnClickListener {
            currentFragmentNo = 1
            binding.etSearch.setText(workoutFilterResult?.searchWord)
            binding.viewPager.setCurrentItem(1, false)
            if(isInsightFilterApplied){
                binding.ivFilterStatus.visibility = View.VISIBLE
            }
            else{
                binding.ivFilterStatus.visibility = View.GONE
            }
        }
        binding.tvFood.setOnClickListener {
            currentFragmentNo = 2
            binding.viewPager.setCurrentItem(2, false)
            if(isFoodFilterApplied){
                binding.ivFilterStatus.visibility = View.VISIBLE
            }
            else{
                binding.ivFilterStatus.visibility = View.GONE
            }
        }
        binding.ivFilter.setOnClickListener {
            AppUtils.preventDoubleClick(binding.ivFilter)
            when (binding.viewPager.currentItem) {
                0 -> {
                    if (workoutFilters == null)
                        getWorkoutAvailableFilters()
                    else {
                        launchWorkFilterActivity()
                    }
                }
                1 -> {
                    if (insightFilters == null)
                        getInsightAvailableFilters()
                    else {
                        launchInsightFilterActivity()
                    }
                }
                2 -> {
                    if (foodFilters == null)
                        getFoodAvailableFilters()
                    else {
                        launchFoodFilterActivity()
                    }
                }
            }
        }
    }

    private fun launchWorkFilterActivity() {
        val intent = Intent(this, WorkoutFiltersActivity::class.java)
        if (workoutFilterResult == null) {
            workoutFilterResult = WorkoutFilterResult()
        }
        intent.putExtra("filterData", workoutFilterResult)
        intent.putExtra("filters", workoutFilters)
        activityLauncher.launch(intent, object : BaseActivityResult.OnActivityResult<ActivityResult> {
            override fun onActivityResult(result: ActivityResult) {
                if (result.resultCode == Activity.RESULT_OK) {
                    if (result.data != null) {
                        workoutFilterResult = result.data?.getParcelableExtra("filterData")
                        workoutFragmentInstance?.hitApi(binding.etSearch.text.toString(), workoutFilterResult)
                        if(workoutFilterResult == null){
                            binding.ivFilterStatus.visibility = View.GONE
                            isWorkoutFilterApplied = false
                        }
                        else{
                            binding.ivFilterStatus.visibility = View.VISIBLE
                            isWorkoutFilterApplied = true
                        }
                    }
                }
            }
        })
    }

    private fun launchInsightFilterActivity() {
        val intent = Intent(this, InsightFiltersActivity::class.java)
        if (insightFilterResult == null) {
            insightFilterResult = InsightFilterResult()
        }
        intent.putExtra("filterData", insightFilterResult)
        intent.putExtra("filters", insightFilters)
        activityLauncher.launch(intent, object : BaseActivityResult.OnActivityResult<ActivityResult> {
            override fun onActivityResult(result: ActivityResult) {
                if (result.resultCode == Activity.RESULT_OK) {
                    if (result.data != null) {
                        insightFilterResult = result.data?.getParcelableExtra("filterData")
                        insightFragmentInstance?.hitApi(binding.etSearch.text.toString(), insightFilterResult)
                        if(insightFilterResult == null){
                            binding.ivFilterStatus.visibility = View.GONE
                            isInsightFilterApplied = false
                        }
                        else{
                            binding.ivFilterStatus.visibility = View.VISIBLE
                            isInsightFilterApplied = true
                        }
                    }
                }
            }
        })
    }

    private fun launchFoodFilterActivity() {
        val intent = Intent(this, FoodFilterActivity::class.java)
        if (foodFilterResult == null) {
            foodFilterResult = FoodFilterResult()
        }
        intent.putExtra("filterData", foodFilterResult)
        intent.putExtra("filters", foodFilters)
        activityLauncher.launch(intent, object : BaseActivityResult.OnActivityResult<ActivityResult> {
            override fun onActivityResult(result: ActivityResult) {
                if (result.resultCode == Activity.RESULT_OK) {
                    if (result.data != null) {
                        foodFilterResult = result.data?.getParcelableExtra("filterData")
                        foodFragmentInstance?.hitApi(binding.etSearch.text.toString(), foodFilterResult)
                        if(foodFilterResult == null){
                            binding.ivFilterStatus.visibility = View.GONE
                            isFoodFilterApplied = false
                        }
                        else{
                            binding.ivFilterStatus.visibility = View.VISIBLE
                            isFoodFilterApplied = true
                        }
                    }
                }
            }
        })
    }

    private fun setViewPager() {
        vpf = ViewPagerAdapter(this@SearchActivity)
        binding.viewPager.adapter = vpf
        binding.viewPager.registerOnPageChangeCallback(pageChangeCallback)
        binding.viewPager.isUserInputEnabled = false
    }


    private var pageChangeCallback: ViewPager2.OnPageChangeCallback =
        object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                colorControl(position)
            }
        }

    private fun colorControl(i: Int) {
        binding.tvWorkout.setTextColor(resources.getColor(if (i == 0) R.color.blue_a700 else R.color.home_heading_text))
        binding.tvInsight.setTextColor(resources.getColor(if (i == 1) R.color.blue_a700 else R.color.home_heading_text))
        binding.tvFood.setTextColor(resources.getColor(if (i == 2) R.color.blue_a700 else R.color.home_heading_text))
    }

    private fun getWorkoutAvailableFilters() {
        apiCall = retrofitClient.getWorkoutAvailableFilters()
        viewModel.getWorkoutAvailableFilters(apiCall)
    }

    private fun getInsightAvailableFilters() {
        apiCall = retrofitClient.getInsightAvailableFilters()
        viewModel.getInsightAvailableFilters(apiCall)
    }

    private fun getFoodAvailableFilters() {
        apiCall = retrofitClient.getFoodAvailableFilters()
        viewModel.getFoodAvailableFilters(apiCall)
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
                    AppUtils.showToast(this, it.message!!, false)
                }
                ApiStatus.SUCCESS -> {
                    Loader.hideLoader()
                    when (it.tag) {
                        ApiTags.GET_WORKOUT_FILTERS -> {
                            val model = Gson().fromJson(it.data.toString(), WorkoutFilterModel::class.java)
                            workoutFilters = model.data
                            launchWorkFilterActivity()
                        }
                        ApiTags.GET_INSIGHT_FILTERS -> {
                            val model = Gson().fromJson(it.data.toString(), InsightFilterModel::class.java)
                            insightFilters = model.data
                            launchInsightFilterActivity()
                        }
                        ApiTags.GET_FOOD_FILTERS -> {
                            val model = Gson().fromJson(it.data.toString(), FoodFilterModel::class.java)
                            foodFilters = model.data
                            launchFoodFilterActivity()
                        }
                    }
                }
            }
        })
    }


    inner class ViewPagerAdapter(
        fragmentActivity: FragmentActivity
    ) :
        FragmentStateAdapter(fragmentActivity) {

        override fun createFragment(position: Int): Fragment {

            when (position) {
                0 -> {
                    workoutFragmentInstance = SearchWorkoutFragment()
                    return workoutFragmentInstance!!
                }
                1 -> {
                    insightFragmentInstance = SearchInsightFragment()
                    return insightFragmentInstance!!
                }
                2 -> {
                    foodFragmentInstance = SearchFoodFragment()
                    return foodFragmentInstance!!
                }
            }
            return WorkoutFragment()
        }

        override fun getItemCount(): Int {
            return 3
        }
    }
}

