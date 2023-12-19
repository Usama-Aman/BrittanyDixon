package com.cp.brittany.dixon.activities.home.start_workout

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.home_models.WorkoutLessonsModel
import com.cp.brittany.dixon.activities.home.start_workout.adapters.StartWorkoutVideosAdapter
import com.cp.brittany.dixon.activities.home.video_full_screen.VideoFullScreenActivity
import com.cp.brittany.dixon.activities.home.workout_detail.GetThisWorkoutLesson
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivityStartWorkoutBinding
import com.cp.brittany.dixon.loader.Loader
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.ApiStatus
import com.cp.brittany.dixon.utils.AppUtils
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call

class StartWorkoutActivity : BaseActivity() {
    private lateinit var binding: ActivityStartWorkoutBinding
    private lateinit var startVideoAdapter: StartWorkoutVideosAdapter
    private var sectionId = ""
    var workoutLessonList: MutableList<GetThisWorkoutLesson> = ArrayList()

    private lateinit var mContext: Context
    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartWorkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()

        mContext = this
        retrofitClient = RetrofitClient.getClient(mContext).create(Api::class.java)

        initVM()
        observeApiResponse()
        initViews()
        initListeners()
        initAdapters()
    }

    private fun initVM() {
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        binding.lifecycleOwner = this
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeApiResponse() {
        viewModel.getApiResponse().observe(this, {
            when (it.status) {
                ApiStatus.LOADING -> {
                    Loader.showLoader(this) {
                        if (this@StartWorkoutActivity::apiCall.isInitialized)
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
                        ApiTags.GET_WORKOUT_LESSONS -> {
                            val model =
                                Gson().fromJson(it.data.toString(), WorkoutLessonsModel::class.java)
                            workoutLessonList.addAll(model.data[0].get_this_workout_lessons)
                            startVideoAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        })
    }


    private fun getWorkoutLessons() {
        apiCall = retrofitClient.getWorkoutLessons(sectionId.toInt())
        viewModel.getWorkoutLessons(apiCall)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initViews() {
        sectionId = intent.getStringExtra("sectionId").toString()
        getWorkoutLessons()
    }

    private fun initListeners() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.btnFullScreen.setOnClickListener {
            startActivity(Intent(this, VideoFullScreenActivity::class.java))
        }
    }

    private fun initAdapters() {
        startVideoAdapter = StartWorkoutVideosAdapter(this, workoutLessonList,
            object : StartWorkoutVideosAdapter.StartWorkOutVideoInterface {
                override fun onVideoClicked(position: Int) {

                }
            })
        binding.rvVideos.adapter = startVideoAdapter
    }
}