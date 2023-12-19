package com.cp.brittany.dixon.activities.home.workout_detail

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModelProviders
import com.astritveliu.boom.Boom
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.models.InsightBookMarkModel
import com.cp.brittany.dixon.activities.home.start_workout.PlayWorkoutModel
import com.cp.brittany.dixon.activities.home.start_workout.PlayWorkoutVideoActivity
import com.cp.brittany.dixon.activities.home.start_workout.StartSectionActivity
import com.cp.brittany.dixon.activities.home.workout_detail.adapters.WorkoutDetailsHeaderAdapter
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.base.BaseActivityResult
import com.cp.brittany.dixon.databinding.ActivityWorkoutDetailBinding
import com.cp.brittany.dixon.loader.Loader
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.*
import com.bumptech.glide.Glide
import com.cp.brittany.dixon.base.AppController
import com.cp.brittany.dixon.network.SocketIO
import com.faltenreich.skeletonlayout.Skeleton
import com.google.gson.Gson
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import android.provider.CalendarContract
import com.cp.brittany.dixon.activities.home.premium.PremiumActivity
import com.google.android.material.transition.MaterialFadeThrough
import java.util.*
import kotlin.collections.ArrayList


class WorkoutDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityWorkoutDetailBinding
    private lateinit var workoutDetailsHeaderAdapter: WorkoutDetailsHeaderAdapter
    private lateinit var mContext: Context

    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>
    private var isFromBookmark = false
    private var isBookmarked = 0
    var workoutId = ""
    private var workoutImage: String = ""
    private var isGuest = 0
    private var isFree = 1
    private var videoListToBuffer = arrayListOf<String>()
    private var setCount = 0
    private var isRestartClick = false

    //    private var workoutListWithoutRest: MutableList<WorkoutSection> = ArrayList()
    val workoutList: MutableList<WorkoutSection> = ArrayList()

    companion object {
        val videosListToPlay: MutableList<PlayWorkoutModel> = ArrayList()
    }

    private var workoutName: String = ""
    private lateinit var skeletonLayout: Skeleton
    private var percentageCompleted: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkoutDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        window.statusBarColor = resources.getColor(R.color.transparent)

        mContext = this
        retrofitClient = RetrofitClient.getClient(mContext).create(Api::class.java)

        initVM()
        observeApiResponse()
        initViews()
        initListener()
        initAdapters()
    }


    private fun initVM() {
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        binding.lifecycleOwner = this
    }

    private fun observeApiResponse() {
        viewModel.getApiResponse().observe(this, {
            when (it.status) {
                ApiStatus.LOADING -> {
//                    Loader.showLoader(this) {
//                        if (this@WorkoutDetailActivity::apiCall.isInitialized)
//                            apiCall.cancel()
//                    }
                }
                ApiStatus.ERROR -> {
//                    Loader.hideLoader()
                    AppUtils.showToast(this, it.message!!, false)
                }
                ApiStatus.SUCCESS -> {
                    Loader.hideLoader()
                    when (it.tag) {
                        ApiTags.GET_WORKOUT_DETAILS -> {
                            val model =
                                Gson().fromJson(it.data.toString(), WorkoutDetailModel::class.java)
                            setWorkoutsData(model)
                        }
                        ApiTags.BOOKMARK_THINGS -> {
                            val model = Gson().fromJson(it.data.toString(), InsightBookMarkModel::class.java)
                            AppUtils.showToast(this@WorkoutDetailActivity, model.message, true)
                            isBookmarked = if (model.message == "Bookmark added") {
                                binding.ivBookmark.setImageResource(R.drawable.ic_heart_selected)
                                1
                            } else {
                                binding.ivBookmark.setImageResource(R.drawable.ic_heart)
                                0
                            }
                        }
                        ApiTags.RESET_WORKOUT -> {
                            getDetails()
                        }
                    }
                }
            }
        })
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun setWorkoutsData(model: WorkoutDetailModel?) {
        if (model != null) {
            workoutName = model.data.workouts.name
            if (workoutImage.isEmpty())
                Glide.with(this).load(model.data.workouts.image_path).centerCrop().into(binding.ivWorkoutImage)
            binding.tabConstraint.viewVisible()
            binding.tvTitle.text = model.data.workouts?.name
            binding.tvWorkoutDetails.text = model.data.workouts?.description
            binding.tvWorkoutPercentage.text = "${String.format("%.2f", model.data.workouts?.percentage_completed)}% completed"
            if (model.data.workouts?.percentage_completed.toDouble() > 0.0 && model.data.workouts?.percentage_completed.toDouble() < 100.00) {
                binding.tvStartWorkout.text = "Resume Workout"
            } else {
                binding.tvStartWorkout.text = "Start Workout"
            }
            percentageCompleted = model.data.workouts?.percentage_completed
            binding.tvWeight.text = model.data.workouts?.weight_status
            if (model.data.workouts.is_free == 0)
                binding.ivPremium.viewVisible()
            else
                binding.ivPremium.viewGone()
            when (model.data.workouts?.level) {
                1 -> binding.tvLevel.text = "Beginner"
                2 -> binding.tvLevel.text = "Medium"
                3 -> binding.tvLevel.text = "Advanced"
            }
            binding.tvCategory.text = model.data.workouts.categories.name
            binding.tvTime.text = "${model.data.workouts?.duration} min"
            binding.tvCalories.text = "~${model.data.workouts?.cal_gain_reduce} cal"
            isBookmarked = model.data.workouts?.is_bookmarked
            if (model.data.workouts?.is_bookmarked == 0)
                binding.ivBookmark.setImageResource(R.drawable.ic_heart)
            else
                binding.ivBookmark.setImageResource(R.drawable.ic_heart_selected)

            if (model.data.workouts?.workout_sections != null) {
                val list: MutableList<WorkoutSection> = model.data.workouts.workout_sections as MutableList<WorkoutSection>

                workoutList.clear()
                workoutList.addAll(list)

                workoutDetailsHeaderAdapter.notifyDataSetChanged()

                if (workoutList.isNullOrEmpty()) {
                    binding.rvMain.viewGone()
                    binding.llNoData.viewVisible()
                } else {
                    binding.rvMain.viewVisible()
                    binding.llNoData.viewGone()
                }
            }
            binding.skeletonLayout.viewGone()
            skeletonLayout.showOriginal()
            binding.rvMain.viewVisible()
            if (!model.data.workouts.workout_sections.isNullOrEmpty()) {
                binding.tvStartWorkout.viewVisible()
                binding.llNoData.viewGone()
                binding.rvMain.viewVisible()
            } else {
                binding.llNoData.viewVisible()
                binding.tvStartWorkout.viewGone()
                binding.rvMain.viewGone()
            }
//            startPreLoadingService()
            if (isRestartClick) {
                isRestartClick = false
                startWorkout()
            }
        }
    }

    private fun startPreLoadingService() {
        for (i in workoutList.indices) {
            for (j in workoutList[i].get_this_workout_lessons.indices) {
                when (workoutList[i].get_this_workout_lessons[j].type) {
                    BrittanyEnums.SINGLE_TYPE.type -> {
                        if (workoutList[i].get_this_workout_lessons[j].files != null)
                            videoListToBuffer.add(workoutList[i].get_this_workout_lessons[j].files?.video_path ?: "")
                    }
                    BrittanyEnums.SET_TYPE.type -> {
                        if (workoutList[i].get_this_workout_lessons[j].childs.isNotEmpty())
                            for (k in workoutList[i].get_this_workout_lessons[j].childs.indices)
                                if (workoutList[i].get_this_workout_lessons[j].childs[k].files != null)
                                    videoListToBuffer.add(workoutList[i].get_this_workout_lessons[j].childs[k].files?.video_path ?: "")
                    }
                }
            }
        }

        val preloadingServiceIntent = Intent(mContext, VideoPreLoadingService::class.java)
        preloadingServiceIntent.putStringArrayListExtra(Constants.VIDEO_LIST, videoListToBuffer)
        startService(preloadingServiceIntent)
    }

    private fun stopPreLoadingService() {
        stopService(Intent(mContext, VideoPreLoadingService::class.java))
    }

    @SuppressLint("SetTextI18n")
    private fun initViews() {
        Boom(binding.tvAddToCalender)
        val data = AppUtils.getUserDetails(this)
        isGuest = data.is_guest
        skeletonLayout = binding.skeletonLayout
        workoutId = intent.getStringExtra("workoutId").toString()
        isFromBookmark = intent.getBooleanExtra("isFromBookmark", false)
        workoutImage = intent.getStringExtra("workoutImage") ?: ""
        binding.tvTitle.text = intent.getStringExtra("title") ?: ""
        isBookmarked = intent.getIntExtra("isBookmarked", 0)
        binding.tvTime.text = "${intent.getStringExtra("time") ?: ""} min"
//        binding.tvCalories.text = "~${intent.getStringExtra("calories") ?: ""} Kcal"
        binding.tvWorkoutDetails.text = intent.getStringExtra("details") ?: ""
        binding.tvWeight.text = intent.getStringExtra("weight")
        isFree = intent.getIntExtra("is_free", 1)
        if (isFree == 0)
            binding.ivPremium.viewVisible()
        else
            binding.ivPremium.viewGone()
        when (intent.getIntExtra("level", -1)) {
            1 -> binding.tvLevel.text = "Beginner"
            2 -> binding.tvLevel.text = "Medium"
            3 -> binding.tvLevel.text = "Advanced"
        }
        binding.tvWorkoutPercentage.text = "${String.format("%.2f", intent.getDoubleExtra("percentageCompleted", 0.0))}% completed"

//        if (isBookmarked == 0)
//            binding.ivBookmark.setImageResource(R.drawable.ic_bookmark_blue)
//        else
//            binding.ivBookmark.setImageResource(R.drawable.ic_bookmark_filled)
        Glide.with(this).load(workoutImage).centerCrop().into(binding.ivWorkoutImage)
        binding.layout.setBackgroundResource(R.drawable.card_top_corner_radius)
        binding.scrollView.setBackgroundResource(R.drawable.bg_transparent_rect_top_radius)
        binding.constraintLayout.setBackgroundResource(R.drawable.bg_transparent_rect_top_radius)

        getDetails()


        binding.tvRestartTraining.setOnClickListener {
            startActivity(Intent(mContext, WorkoutCompletionActivity::class.java))
        }

    }

    private fun getDetails() {
        binding.rvMain.viewGone()
        binding.skeletonLayout.viewVisible()
        skeletonLayout.showSkeleton()
        apiCall = retrofitClient.getWorkoutDetails(workoutId.toInt())
        viewModel.getWorkoutDetails(apiCall)
    }

    private fun resetWorkout() {
        binding.rvMain.viewGone()
        binding.skeletonLayout.viewVisible()
        skeletonLayout.showSkeleton()
        apiCall = retrofitClient.resetWorkout(workoutId.toInt())
        viewModel.resetWorkout(apiCall)
    }

    private fun bookmark() {
        apiCall = retrofitClient.bookmarkThings("workouts", workoutId.toInt())
        viewModel.bookmarkThings(apiCall)
    }


    private fun initListener() {
        binding.ivBookmark.setOnClickListener {
            AppUtils.preventDoubleClick(binding.ivBookmark)
            if (isGuest == 0) {
                bookmark()
            } else {
                showLoginDialog()
            }
        }

        val isPU =SharedPreference.getBoolean(mContext, Constants.isPremiumUser)

        binding.tvAddToCalender.setOnClickListener {
            AppUtils.preventDoubleClick(binding.tvAddToCalender)
            if (isGuest == 0) {
                if ((isFree == 0 && !SharedPreference.getBoolean(mContext, Constants.isPremiumUser))) {
                    startActivity(Intent(applicationContext, PremiumActivity::class.java))
                    return@setOnClickListener
                } else {
                    val time = Calendar.getInstance()
                    val beginTime: Calendar = Calendar.getInstance()
                    beginTime.set(
                        time.get(Calendar.YEAR),
                        time.get(Calendar.MONTH),
                        time.get(Calendar.DAY_OF_MONTH),
                        time.get(Calendar.HOUR_OF_DAY),
                        time.get(Calendar.MINUTE)
                    )
                    val endTime: Calendar = Calendar.getInstance()
                    endTime.set(
                        time.get(Calendar.YEAR),
                        time.get(Calendar.MONTH),
                        time.get(Calendar.DAY_OF_MONTH),
                        time.get(Calendar.HOUR_OF_DAY) + 1,
                        time.get(Calendar.MINUTE)
                    )
                    val intent: Intent = Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.timeInMillis)
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.timeInMillis)
                        .putExtra(CalendarContract.Events.TITLE, "Workout")
                        .putExtra(CalendarContract.Events.DESCRIPTION, binding.tvTitle.text.toString())
                        //.putExtra(CalendarContract.Events.EVENT_LOCATION, "The gym")
                        .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                    //.putExtra(Intent.EXTRA_EMAIL, "rowan@example.com,trevor@example.com")
                    startActivity(intent)
                }

            } else
                showLoginDialog()
        }

        binding.ivBack.setOnClickListener {
            AppUtils.preventDoubleClick(binding.ivBack)
            onBackPressed()
        }

        binding.tvStartWorkout.setOnClickListener {
            AppUtils.preventDoubleClick(binding.tvStartWorkout)
            //0 means false 1 means true
            if (isGuest == 0) {
                if (isFree == 0 && !SharedPreference.getBoolean(mContext, Constants.isPremiumUser)) {
                    startActivity(Intent(applicationContext, PremiumActivity::class.java))
                } else {
                    startWorkout()
                    return@setOnClickListener
                }
            } else
                showLoginDialog()
        }

        binding.tvRestartTraining.setOnClickListener {
            AppUtils.preventDoubleClick(binding.tvRestartTraining)
            if (isGuest == 0) {
                if (isFree == 0 && !SharedPreference.getBoolean(mContext, Constants.isPremiumUser)) {
                    startActivity(Intent(applicationContext, PremiumActivity::class.java))
                    return@setOnClickListener
                } else {
                    isRestartClick = true
                    resetWorkout()
                }
            } else
                showLoginDialog()
        }

        binding.ivShare.setOnClickListener {
            AppUtils.preventDoubleClick(binding.ivShare)
            val builder: Uri.Builder = Uri.Builder()
            builder.scheme(resources.getString(R.string.web_link_url_scheme))
                .authority(resources.getString(R.string.web_link_url))
                .appendQueryParameter(Constants.workoutCode, workoutId)
            val myUrl: String = builder.build().toString()

            val mIntentShare = Intent(Intent.ACTION_SEND)
            val shareBody = "Here is the link to BrittanyDixon app \n\n$myUrl"
            mIntentShare.type = "text/plain"
            mIntentShare.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            mIntentShare.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(mIntentShare, "Please select one option"))
        }
    }

    private fun initAdapters() {
        workoutDetailsHeaderAdapter =
            WorkoutDetailsHeaderAdapter(this, workoutList, isFree, object : WorkoutDetailsHeaderAdapter.WorkoutClickInterface {
                override fun onWorkoutClicked(sectionPosition: Int, lessonPosition: Int, childPosition: Int) {
                    if (isGuest == 0) {
                        if (isFree == 0 && !SharedPreference.getBoolean(mContext, Constants.isPremiumUser)) {
                            startActivity(Intent(applicationContext, PremiumActivity::class.java))
                        } else {
                            startWorkout(sectionPosition, lessonPosition, childPosition, true)
                        }
                    } else {
                        showLoginDialog()
                    }
                }
            })
        binding.rvMain.adapter = workoutDetailsHeaderAdapter
    }

    @SuppressLint("SetTextI18n")
    private fun startWorkout(sp: Int = 0, lp: Int = 0, cp: Int = 0, fromAdapter: Boolean = false) {
        var sectionPosition = sp
        var lessonPosition = lp
        var childPosition = cp
        videosListToPlay.clear()
        var resumeFound = false
        if (binding.tvStartWorkout.text == "Start Workout") {
            resumeFound = true
        }
        for (i in sectionPosition until workoutList.size) {
            for (j in lessonPosition until workoutList[i].get_this_workout_lessons.size) {
                when (workoutList[i].get_this_workout_lessons[j].type) {
                    BrittanyEnums.SINGLE_TYPE.type -> {
                        val playWorkoutModel = PlayWorkoutModel()
                        if (!fromAdapter && !resumeFound && percentageCompleted != 100.00) {
                            if (workoutList[i].get_this_workout_lessons[j].watch_time != null) {
                                if (workoutList[i].get_this_workout_lessons[j].watch_time?.percentage_completed?.toDouble() == 100.00) {
                                    continue
                                } else {
                                    resumeFound = true
                                }
                            }
                        }
                        playWorkoutModel.video_path = workoutList[i].get_this_workout_lessons[j].files?.video_path ?: ""
                        playWorkoutModel.thumbnail_path = workoutList[i].get_this_workout_lessons[j].files?.thumbnail_path ?: ""
                        playWorkoutModel.lesson_id = workoutList[i].get_this_workout_lessons[j].id
                        playWorkoutModel.is_rest = workoutList[i].get_this_workout_lessons[j].is_rest
                        playWorkoutModel.reps = workoutList[i].get_this_workout_lessons[j].reps ?: ""
                        playWorkoutModel.duration = workoutList[i].get_this_workout_lessons[j].duration ?: ""
                        playWorkoutModel.video_id = workoutList[i].get_this_workout_lessons[j].id
                        playWorkoutModel.video_name = workoutList[i].get_this_workout_lessons[j].name ?: ""
                        playWorkoutModel.no_of_sets = workoutList[i].get_this_workout_lessons[j].no_of_set ?: ""
                        playWorkoutModel.type = workoutList[i].get_this_workout_lessons[j].type ?: ""
                        playWorkoutModel.workout_section_id = workoutList[i].id
                        playWorkoutModel.workout_name = workoutName
                        playWorkoutModel.section_name = workoutList[i].name
                        playWorkoutModel.section_image = workoutList[i].get_this_workout_lessons[j].thumbnail ?: ""

                        println(workoutList[i].get_this_workout_lessons[j].name)
                        videosListToPlay.add(playWorkoutModel)

                    }
                    BrittanyEnums.SET_TYPE.type -> {
                        for (k in 0 until workoutList[i].get_this_workout_lessons[j].no_of_set.toInt()) {
                            for (c in childPosition until workoutList[i].get_this_workout_lessons[j].childs.size) {
                                val playWorkoutModel = PlayWorkoutModel()
                                if (!fromAdapter && !resumeFound && percentageCompleted != 100.00) {
                                    if (workoutList[i].get_this_workout_lessons[j].watch_time != null) {
                                        if (workoutList[i].get_this_workout_lessons[j].watch_time?.no_of_set?.toInt() == workoutList[i].get_this_workout_lessons[j].watch_time?.elapsed_set) {
                                            continue
                                        } else if (workoutList[i].get_this_workout_lessons[j].watch_time?.elapsed_set != null && setCount < workoutList[i].get_this_workout_lessons[j].watch_time?.elapsed_set!!) {
                                            continue
                                        } else {
                                            resumeFound = true
                                        }
                                    } else {
                                        resumeFound = true
                                    }
                                }
                                if (k == 0 && c == 0) {
                                    setCount = 0
                                    playWorkoutModel.isNewSet = true
                                }
                                playWorkoutModel.video_path = workoutList[i].get_this_workout_lessons[j].childs[c].files?.video_path ?: ""
                                playWorkoutModel.thumbnail_path = workoutList[i].get_this_workout_lessons[j].childs[c].files?.thumbnail_path ?: ""
                                playWorkoutModel.lesson_id = workoutList[i].get_this_workout_lessons[j].childs[c].id
                                playWorkoutModel.is_rest = workoutList[i].get_this_workout_lessons[j].childs[c].is_rest
                                playWorkoutModel.reps = workoutList[i].get_this_workout_lessons[j].childs[c].reps ?: ""
                                playWorkoutModel.duration = workoutList[i].get_this_workout_lessons[j].childs[c].duration ?: ""
                                playWorkoutModel.video_id = workoutList[i].get_this_workout_lessons[j].childs[c].id
                                playWorkoutModel.video_name = workoutList[i].get_this_workout_lessons[j].childs[c].name ?: ""
                                playWorkoutModel.no_of_sets = workoutList[i].get_this_workout_lessons[j].no_of_set ?: ""
                                playWorkoutModel.type = workoutList[i].get_this_workout_lessons[j].childs[c].type ?: ""
                                playWorkoutModel.workout_section_id = workoutList[i].id
                                playWorkoutModel.workout_name = workoutName
                                playWorkoutModel.section_name = workoutList[i].name
                                playWorkoutModel.section_image = workoutList[i].get_this_workout_lessons[j].thumbnail ?: ""
                                playWorkoutModel.current_set_count = setCount
                                playWorkoutModel.set_id = workoutList[i].get_this_workout_lessons[j].id
                                println(workoutList[i].get_this_workout_lessons[j].childs[c].name)
                                videosListToPlay.add(playWorkoutModel)

                                if (k + 1 == workoutList[i].get_this_workout_lessons[j].childs.size) {// it means loop is finished
                                    childPosition = 0
                                }
                            }
                            setCount++
                        }
                    }
                }

                if (j + 1 == workoutList[i].get_this_workout_lessons.size) {// it means loop is finished
                    lessonPosition = 0
                }
            }

            if (i + 1 == workoutList.size) {// it means loop is finished
                sectionPosition = 0
            }
        }
        if (videosListToPlay.isNotEmpty()) {
            val intent = Intent(this@WorkoutDetailActivity, StartSectionActivity::class.java)
            intent.putExtra("workoutName", videosListToPlay[0].workout_name)
            intent.putExtra("sectionName", videosListToPlay[0].section_name)
            intent.putExtra("sectionImage", videosListToPlay[0].thumbnail_path)
            activityLauncher.launch(intent, object : BaseActivityResult.OnActivityResult<ActivityResult> {
                override fun onActivityResult(result: ActivityResult) {
                    if (result.resultCode == Activity.RESULT_OK) {
                        val i = Intent(this@WorkoutDetailActivity, PlayWorkoutVideoActivity::class.java)
                        startActivity(i)
                    }
                }
            })
        }
    }

    override fun onBackPressed() {
        //if (isFromBookmark)
        setIntentData()
        //supportFinishAfterTransition()
    }

    private fun setIntentData() {
        val intentResult = Intent()
        intentResult.putExtra("bookmark", isBookmarked)
        intentResult.putExtra("id", workoutId)
        setResult(Activity.RESULT_OK, intentResult)
        supportFinishAfterTransition()
    }

    override fun onDestroy() {
        super.onDestroy()
//        stopPreLoadingService()
    }

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({
            apiCall = retrofitClient.getWorkoutDetails(workoutId.toInt())
            viewModel.getWorkoutDetails(apiCall)
        }, 3000)

    }
}