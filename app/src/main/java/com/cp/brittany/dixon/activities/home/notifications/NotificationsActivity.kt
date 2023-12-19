package com.cp.brittany.dixon.activities.home.notifications

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.home_models.NotificationsList
import com.cp.brittany.dixon.activities.home.home_models.NotificationsModel
import com.cp.brittany.dixon.activities.home.insight_rec_detail.InsightRecommendationDetailActivity
import com.cp.brittany.dixon.activities.home.notifications.adapter.NotificationsAdapter
import com.cp.brittany.dixon.activities.home.workout_detail.WorkoutDetailActivity
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivityNotificationsBinding
import com.cp.brittany.dixon.loader.Loader
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.ApiStatus
import com.cp.brittany.dixon.utils.AppUtils
import com.cp.brittany.dixon.utils.viewGone
import com.cp.brittany.dixon.utils.viewVisible
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call

class NotificationsActivity : BaseActivity() {
    private lateinit var binding: ActivityNotificationsBinding
    private lateinit var mContext: Context
    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>

    private lateinit var notificationsAdapter: NotificationsAdapter
    private var notificationsList: MutableList<NotificationsList> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()

        mContext = this
        retrofitClient = RetrofitClient.getClient(this).create(Api::class.java)

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

    private fun observeApiResponse() {
        viewModel.getApiResponse().observe(this, {
            when (it.status) {
                ApiStatus.LOADING -> {
                    Loader.showLoader(this) {
                        if (this@NotificationsActivity::apiCall.isInitialized)
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
                        ApiTags.GET_NOTIFICATIONS -> {
                            val model =
                                Gson().fromJson(it.data.toString(), NotificationsModel::class.java)
                            setNotifications(model.data)
                        }
                        ApiTags.READ_NOTIFICATION -> {

                        }
                    }
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setNotifications(list: List<NotificationsList>) {
        if(list.isEmpty()){
            binding.noData.viewVisible()
            binding.rvNotifications.viewGone()
            return
        }
        binding.noData.viewGone()
        binding.rvNotifications.viewVisible()
        notificationsList.addAll(list)
        notificationsAdapter.setItems(notificationsList)
        binding.tvTitle.text = "Notifications (${notificationsList.size})"
    }

    private fun getNotifications() {
        apiCall = retrofitClient.getNotifications()
        viewModel.getNotifications(apiCall)
    }

    private fun readNotifications(id: Int) {
        apiCall = retrofitClient.readNotification(id)
        viewModel.readNotification(apiCall)
    }

    private fun initViews() {
        getNotifications()
    }

    private fun initListeners() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initAdapters() {
        notificationsAdapter = NotificationsAdapter(this, notificationsList, object: NotificationsAdapter.NotificationInterface{
            override fun onItemClicked(position: Int) {
                if(notificationsList[position].is_read == 0) {
                    readNotifications(notificationsList[position].id)
                    notificationsList[position].is_read = 1
                    binding.rvNotifications.adapter?.notifyDataSetChanged()
                }
                if(notificationsList[position].notification_type == "workouts") {
                    val intent = Intent(applicationContext, WorkoutDetailActivity::class.java)
                    intent.putExtra("workoutId", notificationsList[position].notification_data.id.toString())
                    startActivity(intent)
                }
                else if(notificationsList[position].notification_type == "insights"){
                    val intent = Intent(applicationContext, InsightRecommendationDetailActivity::class.java)
                    intent.putExtra("id", notificationsList[position].notification_data.id)
                    startActivity(intent)
                }

            }

        })
        binding.rvNotifications.adapter = notificationsAdapter
    }
}