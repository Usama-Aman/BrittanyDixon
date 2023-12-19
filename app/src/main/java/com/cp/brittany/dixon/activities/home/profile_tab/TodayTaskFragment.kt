package com.cp.brittany.dixon.activities.home.profile_tab

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.ZoomImageActivity
import com.cp.brittany.dixon.activities.home.HomeActivity
import com.cp.brittany.dixon.activities.home.food_tab.RecipeDetailActivity
import com.cp.brittany.dixon.activities.home.insight_rec_detail.InsightRecommendationDetailActivity
import com.cp.brittany.dixon.activities.home.models.TodayTasks
import com.cp.brittany.dixon.activities.home.models.TodayTasksModel
import com.cp.brittany.dixon.activities.home.product_page.ProductPageActivity
import com.cp.brittany.dixon.activities.home.profile_tab.adapters.ProfileTabTasksAdapter
import com.cp.brittany.dixon.activities.home.workout_detail.WorkoutDetailActivity
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.base.BaseActivityResult
import com.cp.brittany.dixon.base.ImagePickerActivity
import com.cp.brittany.dixon.base.formatePickerDateNew2
import com.cp.brittany.dixon.base.longDateFormat
import com.cp.brittany.dixon.databinding.FragmentTodayTaskBinding
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.ApiStatus
import com.cp.brittany.dixon.utils.AppUtils
import com.cp.brittany.dixon.utils.viewGone
import com.cp.brittany.dixon.utils.viewVisible
import com.faltenreich.skeletonlayout.Skeleton
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class TodayTaskFragment : Fragment() {

    private lateinit var binding: FragmentTodayTaskBinding
    private lateinit var skeletonLayout: Skeleton
    private var todayTasks: MutableList<TodayTasks> = ArrayList()
    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>
    private lateinit var tasksAdapter: ProfileTabTasksAdapter
    private val mCalendar: Calendar = Calendar.getInstance()
    private var selectedMonth = -1
    private var selectedYear = -1
    private var currentMonth = -1
    private var selectedDate: String = ""
    private var currentDay = -1
    private lateinit var date: DatePickerDialog.OnDateSetListener

    private val activityLauncher: BaseActivityResult<Intent, ActivityResult> =
        BaseActivityResult.registerActivityForResult(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTodayTaskBinding.inflate(layoutInflater, container, false)
        selectedDate = AppUtils.convertToCustomFormat(Calendar.getInstance().time.toString())
        retrofitClient = RetrofitClient.getClient(requireContext()).create(Api::class.java)
        skeletonLayout = binding.skeletonLayout
        initViews()
        initListeners()
        initAdapter()
        initVM()
        observeApiResponse()
        binding.ivNext.isClickable = false
        binding.ivPrevious.isClickable = false
        binding.tvDate.isClickable = false
        binding.tvDay.isClickable = false
        val c = Calendar.getInstance()
        val df = SimpleDateFormat("MM-dd-yyyy")
        binding.tvDate.text = (activity as HomeActivity).longDateFormat(c.time)
        binding.tvDay.text = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH)
        selectedDate = df.format(c.time)
        getTodayTasks()
        return binding.root
    }

    private fun initViews(){
        val c = Calendar.getInstance()
        selectDate()
        currentDay = c.get(Calendar.DAY_OF_MONTH)
        selectedYear = c.get(Calendar.YEAR)
        currentMonth = c[Calendar.MONTH]
        selectedMonth = currentMonth
    }
    private fun initListeners() {
        binding.pullToRefresh.setOnRefreshListener {
            binding.ivNext.isClickable = false
            binding.ivPrevious.isClickable = false
            binding.tvDate.isClickable = false
            binding.tvDay.isClickable = false
            getTodayTasks()
        }
        binding.btnAddImage.setOnClickListener {
            AppUtils.preventDoubleClick(binding.btnAddImage)
            val intent = Intent(requireContext(), ImagePickerActivity::class.java)
            activityLauncher.launch(intent, object : BaseActivityResult.OnActivityResult<ActivityResult> {
                override fun onActivityResult(result: ActivityResult) {
                    if (result.resultCode == Activity.RESULT_OK) {
                        val path = result.data?.getStringExtra("filePath") ?: ""
                        if (path != "") {

                            val file = java.io.File(path)

                            val multipartBody: MultipartBody.Part =
                                MultipartBody.Part.createFormData(
                                    "img", file.name, file.asRequestBody("image/*".toMediaTypeOrNull())
                                )
                            saveUserImage(multipartBody)
                        }
                    }
                }
            })
        }

        binding.tvDate.setOnClickListener {
            AppUtils.preventDoubleClick(binding.tvDate)
            if(!binding.tvDate.isClickable){
                return@setOnClickListener
            }
            val datepicker = DatePickerDialog(
                requireContext(), R.style.MyDatePickerStyle, date, mCalendar
                    .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)
            )
            datepicker.datePicker.maxDate = System.currentTimeMillis()
            datepicker.show()

        }

        binding.tvDay.setOnClickListener {
            AppUtils.preventDoubleClick(binding.tvDay)
            if(!binding.tvDay.isClickable){
                return@setOnClickListener
            }
            val datepicker = DatePickerDialog(
                requireContext(), R.style.MyDatePickerStyle, date, mCalendar
                    .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)
            )
            datepicker.datePicker.maxDate = System.currentTimeMillis()
            datepicker.show()

        }

        binding.ivPrevious.setOnClickListener {
            AppUtils.preventDoubleClick(binding.ivPrevious)
            if(!binding.ivPrevious.isClickable){
                return@setOnClickListener
            }
            binding.ivNext.isClickable = false
            binding.ivPrevious.isClickable = false
            binding.tvDate.isClickable = false
            binding.tvDay.isClickable = false
            mCalendar.set(Calendar.DAY_OF_MONTH, mCalendar[Calendar.DAY_OF_MONTH]-1)
            currentMonth = mCalendar[Calendar.MONTH]
            selectedMonth = currentMonth
            selectedYear = mCalendar[Calendar.YEAR]
            currentDay = mCalendar.get(Calendar.DAY_OF_MONTH)
            binding.tvDate.text = (activity as HomeActivity).longDateFormat(mCalendar.time)
            binding.tvDay.text = mCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH)
            selectedDate = AppUtils.convertToCustomFormat(mCalendar.time.toString())
            getTodayTasks()
        }

        binding.ivNext.setOnClickListener {
            AppUtils.preventDoubleClick(binding.ivNext)
            if(!binding.ivNext.isClickable || AppUtils.convertToCustomFormat(mCalendar.time.toString()) == AppUtils.convertToCustomFormat(Calendar.getInstance().time.toString())){
                return@setOnClickListener
            }
            binding.ivNext.isClickable = false
            binding.ivPrevious.isClickable = false
            binding.tvDate.isClickable = false
            binding.tvDay.isClickable = false
            mCalendar.set(Calendar.DAY_OF_MONTH, mCalendar[Calendar.DAY_OF_MONTH]+1)
            currentMonth = mCalendar[Calendar.MONTH]
            selectedMonth = currentMonth
            selectedYear = mCalendar[Calendar.YEAR]
            currentDay = mCalendar.get(Calendar.DAY_OF_MONTH)
            binding.tvDate.text = (activity as HomeActivity).longDateFormat(mCalendar.time)
            binding.tvDay.text = mCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH)
            selectedDate = AppUtils.convertToCustomFormat(mCalendar.time.toString())
            getTodayTasks()
        }
    }

    private fun saveUserImage(multipartBody: MultipartBody.Part) {
        apiCall = retrofitClient.saveUserImage(selectedDate, multipartBody)
        viewModel.saveUserImage(apiCall)
    }

    private fun initAdapter() {
        tasksAdapter = ProfileTabTasksAdapter(todayTasks, object : ProfileTabTasksAdapter.ProfileAdapterInterface {
            override fun onItemClicked(position: Int, options: ActivityOptionsCompat) {

                when (todayTasks[position].type) {
                    "foods" -> {
                        val intent = Intent(requireContext(), RecipeDetailActivity::class.java)
                        intent.putExtra("foodId", todayTasks[position].task.id.toString())
                        intent.putExtra("image", todayTasks[position].task.image_path)
                        intent.putExtra("time", todayTasks[position].task.time)
                        intent.putExtra("type", todayTasks[position].task.type)
                        intent.putExtra("calories", todayTasks[position].task.calories)
                        intent.putExtra("unit", todayTasks[position].task.unit)
                        intent.putExtra("title", todayTasks[position].task.name)
                        if (!todayTasks[position].task.video.isNullOrEmpty())
                            intent.putExtra("videoPath", todayTasks[position].task.video_path)
                        startActivity(intent, options.toBundle())
                    }
                    "workouts" -> {
                        val intent = Intent(requireContext(), WorkoutDetailActivity::class.java)
                        intent.putExtra("workoutId", todayTasks[position].task.id.toString())
                        intent.putExtra("workoutImage", todayTasks[position].task.image_path)
                        intent.putExtra("title", todayTasks[position].task.name)
                        intent.putExtra("details", todayTasks[position].task.description)
                        intent.putExtra("weight", todayTasks[position].task.weight_status)
                        intent.putExtra("level", todayTasks[position].task.level)
                        intent.putExtra("percentageCompleted", todayTasks[position].task.percentage_completed)
                        intent.putExtra("isBookmarked", todayTasks[position].task.is_bookmarked)
                        intent.putExtra("time", todayTasks[position].task.duration)
                        intent.putExtra("unit", todayTasks[position].task.unit)
                        intent.putExtra("is_free", todayTasks[position].task.is_free)
                        intent.putExtra("calories", todayTasks[position].task.cal_gain_reduce)
                        startActivity(intent, options.toBundle())
                    }
                    "insights" -> {
                        val i = Intent(requireContext(), InsightRecommendationDetailActivity::class.java)
                        i.putExtra("id", todayTasks[position].task.id)
                        i.putExtra("insightImage", todayTasks[position].task.image_path)
                        i.putExtra("time", todayTasks[position].task.duration)
                        i.putExtra("unit", todayTasks[position].task.unit)
                        i.putExtra("title", todayTasks[position].task.name)
                        i.putExtra("date", todayTasks[position].task.date)
                        i.putExtra("isBookmarked", todayTasks[position].task.is_bookmarked)
                        i.putExtra("details", todayTasks[position].task.detail)
                        i.putExtra("totalLikes", todayTasks[position].task.total_likes)
                        i.putExtra("isLiked", todayTasks[position].task.is_liked)
                        i.putExtra("created_at", todayTasks[position].task.created_at)
                        requireContext().startActivity(i, options.toBundle())
                    }
                    "products" -> {
                        val intent = Intent(requireContext(), ProductPageActivity::class.java)
                        //intent.putExtra("productId", todayTasks[position].task.id)
                        intent.putExtra("productId", todayTasks[position].task.id)
                        intent.putExtra("title", todayTasks[position].task.name)
                        intent.putExtra("price", todayTasks[position].task.minimum_price.compare_at_price)
                        intent.putExtra("discountedPrice", todayTasks[position].task.minimum_price.price)
                        if (todayTasks[position].task.sub_category_name != null)
                            intent.putExtra("category", todayTasks[position].task.sub_category_name.name)
                        else
                            intent.putExtra("category", todayTasks[position].task.category_name.name)
                        startActivity(intent, options.toBundle())
                    }
                }

            }

            override fun onImageClicked(position: Int, options: ActivityOptionsCompat) {
                val intent = Intent(requireContext(), ZoomImageActivity::class.java)
                intent.putExtra("imageUrl", todayTasks[position].task.image_path)
                startActivity(intent, options.toBundle())
            }
        })
        binding.rvTasksForToday.adapter = tasksAdapter
//        binding.rvTasksForToday.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                if (dy > 0 && binding.btnAddImage.visibility == View.VISIBLE) {
//                    binding.btnAddImage.hide()
//                } else if (dy < 0 && binding.btnAddImage.visibility != View.VISIBLE) {
//                    binding.btnAddImage.show()
//                }
//            }
//
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    binding.btnAddImage.show()
//                }
//            }
//        })
    }

    private fun initVM() {
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        binding.lifecycleOwner = this
    }

    private fun observeApiResponse() {
        viewModel.getApiResponse().observe(requireActivity(), {
            when (it.status) {
                ApiStatus.LOADING -> {
//                    if (it.tag != ApiTags.GET_INSIGHT_CATEGORIES)
//                        Loader.showLoader((requireActivity() as AppCompatActivity)) {
//                            if (this::apiCall.isInitialized)
//                                apiCall.cancel()
//                        }
                }
                ApiStatus.ERROR -> {
                    //Loader.hideLoader()
                    binding.pullToRefresh.isRefreshing = false
                    binding.ivNext.isClickable = true
                    binding.ivPrevious.isClickable = true
                    binding.tvDate.isClickable = true
                    binding.tvDay.isClickable = true
                    AppUtils.showToast(requireActivity(), it.message!!, false)
                }
                ApiStatus.SUCCESS -> {
                    //Loader.hideLoader()
                    when (it.tag) {
                        ApiTags.GET_TODAY_TASKS -> {

                            binding.pullToRefresh.isRefreshing = false
                            Handler(Looper.getMainLooper()).postDelayed({
                                binding.ivNext.isClickable = true
                                binding.ivPrevious.isClickable = true
                                binding.tvDate.isClickable = true
                                binding.tvDay.isClickable = true
                                binding.rvTasksForToday.viewVisible()
                                binding.skeletonLayoutScroll.viewGone()
                                binding.skeletonLayout.viewGone()
                                skeletonLayout.showOriginal()
                            }, 1000)


                            val model = Gson().fromJson(it.data.toString(), TodayTasksModel::class.java)
                            todayTasks.clear()
                            todayTasks.addAll(model.data.task)
                            if (!todayTasks.isNullOrEmpty()) {
                                binding.noData.viewGone()
                                tasksAdapter.notifyDataSetChanged()
                            } else {
                                Handler(Looper.getMainLooper()).postDelayed({
                                    binding.noData.viewVisible()
                                    binding.rvTasksForToday.viewGone()
                                }, 1000)
                            }

                        }
                        ApiTags.SAVE_USER_IMAGE -> {
                            val model = Gson().fromJson(it.data.toString(), TodayTasksModel::class.java)
                            AppUtils.showToast(requireActivity(), model.message, true)
                            Handler(Looper.getMainLooper()).postDelayed({
                                getTodayTasks()
                            }, 700)
                        }
                    }
                }
            }
        })
    }

    private fun getTodayTasks() {
        binding.noData.viewGone()
        binding.rvTasksForToday.viewGone()
        binding.skeletonLayoutScroll.viewVisible()
        binding.skeletonLayout.viewVisible()
        skeletonLayout.showSkeleton()
        apiCall = retrofitClient.getTodayTasks(selectedDate)
        viewModel.getTodayTasks(apiCall)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun selectDate() {
        date =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                mCalendar.set(Calendar.YEAR, year)
                mCalendar.set(Calendar.MONTH, monthOfYear)
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                currentMonth = mCalendar[Calendar.MONTH]
                selectedMonth = currentMonth
                selectedYear = mCalendar[Calendar.YEAR]
                currentDay = mCalendar.get(Calendar.DAY_OF_MONTH)
                binding.tvDate.text = (activity as HomeActivity).longDateFormat(mCalendar.time)
                binding.tvDay.text = mCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH)
                selectedDate = AppUtils.convertToCustomFormat(mCalendar.time.toString())
                Log.e("Current date", selectedDate)
                binding.ivNext.isClickable = false
                binding.ivPrevious.isClickable = false
                binding.tvDate.isClickable = false
                binding.tvDay.isClickable = false
                getTodayTasks()
            }


    }

}