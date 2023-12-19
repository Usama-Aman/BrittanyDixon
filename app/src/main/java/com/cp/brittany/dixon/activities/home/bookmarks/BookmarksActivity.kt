package com.cp.brittany.dixon.activities.home.bookmarks

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.bookmarks.posts_tab.BookmarksPostsFragment
import com.cp.brittany.dixon.activities.home.bookmarks.workout_tab.BookmarksWorkoutFragment
import com.cp.brittany.dixon.activities.home.models.BookMarksModel
import com.cp.brittany.dixon.activities.home.workout_tab.WorkoutFragment
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivityBookmarksBinding
import com.cp.brittany.dixon.loader.Loader
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.ApiStatus
import com.cp.brittany.dixon.utils.AppUtils
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call

class BookmarksActivity : BaseActivity() {
    private lateinit var binding: ActivityBookmarksBinding
    private lateinit var vpf: ViewPagerAdapter
    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>
    private var apiCallType: String = "workouts"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookmarksBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()
        retrofitClient = RetrofitClient.getClient(applicationContext).create(Api::class.java)

        initViews()
        initVM()
        initListeners()
        observeApiResponse()
        setViewPager()
        //getBookmarksCount(apiCallType)
    }

    private fun initViews() {

    }
    private fun initVM() {
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        binding.lifecycleOwner = this
    }
    private fun initListeners() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.tvWorkout.setOnClickListener {
            binding.viewPager.setCurrentItem(0, false)
        }

        binding.tvPosts.setOnClickListener {
            binding.viewPager.setCurrentItem(1, false)
        }
    }

    private fun setViewPager() {
        vpf = ViewPagerAdapter(this@BookmarksActivity)
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
        binding.tvPosts.setTextColor(resources.getColor(if (i == 1) R.color.blue_a700 else R.color.home_heading_text))
    }


    private class ViewPagerAdapter(
        fragmentActivity: FragmentActivity
    ) :
        FragmentStateAdapter(fragmentActivity) {
        private val context: Context = fragmentActivity

        override fun createFragment(position: Int): Fragment {


            when (position) {
                0 -> return BookmarksWorkoutFragment()
                1 -> return BookmarksPostsFragment()
            }
            return WorkoutFragment()
        }

        override fun getItemCount(): Int {
            return 2
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeApiResponse() {
        viewModel.getApiResponse().observe(this@BookmarksActivity, {
            when (it.status) {
                ApiStatus.LOADING -> {
//                    Loader.showLoader(this@BookmarksActivity) {
//                        if (this::apiCall.isInitialized)
//                            apiCall.cancel()
//                    }
                }
                ApiStatus.ERROR -> {
                    //Loader.hideLoader()
                    AppUtils.showToast(this@BookmarksActivity, it.message!!, false)
                }
                ApiStatus.SUCCESS -> {
                    //Loader.hideLoader()
                    when (it.tag) {
                        ApiTags.GET_BOOKMARK -> {
                            val model = Gson().fromJson(it.data.toString(), BookMarksModel::class.java)
                            val size = model.data.size
                            if(size > 0) {
                                if(model.data[0].table == "workouts") {
                                    binding.tvWorkout.text = "Workout ($size)"
                                    apiCallType = "insights"
                                    getBookmarksCount(apiCallType)
                                }
                                else {
                                    binding.tvPosts.text = "Posts ($size)"
                                }

                            }
                            else{
                                if(apiCallType == "workouts"){
                                    binding.tvWorkout.text = "Workout (0)"
                                    apiCallType = "insights"
                                    getBookmarksCount(apiCallType)
                                }
                                else{
                                    binding.tvPosts.text = "Posts (0)"
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    private fun getBookmarksCount(type: String){
        apiCall = retrofitClient.getWorkoutBookmark(type)
        viewModel.getWorkoutBookmarks(apiCall)
    }

    @SuppressLint("SetTextI18n")
    fun updateHeader(count: Int, type: Int){
        if(type == 0)
            binding.tvWorkout.text = "Workout ($count)"
        else
            binding.tvPosts.text = "Posts ($count)"
    }

}