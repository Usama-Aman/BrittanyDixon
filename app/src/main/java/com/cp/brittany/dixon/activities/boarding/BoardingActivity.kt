package com.cp.brittany.dixon.activities.boarding

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings.Secure
import android.transition.Scene
import android.util.Log
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.auth.login.SignInActivity
import com.cp.brittany.dixon.activities.auth.models.BannerData
import com.cp.brittany.dixon.activities.auth.models.BannerModel
import com.cp.brittany.dixon.activities.auth.models.LoginModel
import com.cp.brittany.dixon.activities.auth.signup.SignUpActivity
import com.cp.brittany.dixon.activities.boarding.adapters.BoardingImagesAdapter
import com.cp.brittany.dixon.activities.boarding.fragments.Boarding1Fragment
import com.cp.brittany.dixon.activities.boarding.fragments.Boarding2Fragment
import com.cp.brittany.dixon.activities.boarding.fragments.Boarding3Fragment
import com.cp.brittany.dixon.activities.home.HomeActivity
import com.cp.brittany.dixon.activities.view_models.AuthViewModel
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivityBoardingBinding
import com.cp.brittany.dixon.loader.Loader
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.*
import com.faltenreich.skeletonlayout.Skeleton
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call


class BoardingActivity : BaseActivity() {
    private lateinit var binding: ActivityBoardingBinding
    private lateinit var vpf: ViewPagerFragmentAdapter
    private lateinit var imagesAdapter: BoardingImagesAdapter
    private var imagesList: ArrayList<BannerData> = ArrayList()

    private lateinit var scene1: Scene
    private lateinit var scene2: Scene
    private lateinit var scene3: Scene
    private lateinit var sceneRoot: ViewGroup

    private lateinit var viewModel: AuthViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>
    private var deviceId = ""
    private lateinit var skeletonLayout: Skeleton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor(R.color.indigo_50)
        retrofitClient = RetrofitClient.getClient(this).create(Api::class.java)

        blackStatusBarIcons()
        onSetupViewGroup(binding.content)
        initVar()
        initVM()
        observeApiResponse()
        listener()
        getBanner()
    }

    private fun initVM() {
        viewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        binding.lifecycleOwner = this
    }

    @SuppressLint("HardwareIds")
    private fun initVar() {
        skeletonLayout = binding.skeletonLayout
        binding.skeletonLayout.viewVisible()
        skeletonLayout.showSkeleton()
        deviceId = Secure.getString(
            this.contentResolver,
            Secure.ANDROID_ID
        )
    }

    private fun setupScene() {
        sceneRoot = binding.rootView
        scene1 = Scene.getSceneForLayout(sceneRoot, R.layout.boarding_scene_1, this)
        scene2 = Scene.getSceneForLayout(sceneRoot, R.layout.boarding_scene_2, this)
        scene3 = Scene.getSceneForLayout(sceneRoot, R.layout.boarding_scene_3, this)
    }

    private fun setImagesRecView() {
        imagesAdapter = BoardingImagesAdapter(this, imagesList)
        binding.rvImages.adapter = imagesAdapter

        binding.rvImages.layoutManager =
            ZoomRecyclerLayout(this, LinearLayoutManager.HORIZONTAL, false)
        binding.indicator.attachToRecyclerView(binding.rvImages)

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvImages)

        binding.rvImages.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val centerView = snapHelper.findSnapView(binding.rvImages.layoutManager)
                    val pos = (binding.rvImages.layoutManager as ZoomRecyclerLayout).getPosition(
                        centerView!!
                    )
                    val lm = recyclerView.layoutManager as LinearLayoutManager
                    val position = lm.findFirstVisibleItemPosition()
                    println("$position")

                    setBottomTab(position)
                    Log.e("Snapped Item Position:", "" + pos)
                }
            }
        })
    }

    private fun setViewPager() {
        vpf = ViewPagerFragmentAdapter(this@BoardingActivity)
        binding.viewPager.adapter = vpf
        binding.viewPager.offscreenPageLimit = 3
        binding.dotsIndicator.setViewPager2(binding.viewPager)
        binding.viewPager.registerOnPageChangeCallback(pageChangeCallback)
    }

    private fun listener() {
        binding.btnLogIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
        binding.btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        binding.btnGuest.setOnClickListener {
            loginAsGuest()
        }
    }

    private var pageChangeCallback: ViewPager2.OnPageChangeCallback =
        object : ViewPager2.OnPageChangeCallback() {

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                setBottomTab(position)
            }

        }


    fun setBottomTab(i: Int = 0) {

        when (i) {
            0 -> {
                changeStatusBarColor(R.color.indigo_50)
            }
            1 -> {
                changeStatusBarColor(R.color.cyan_50)
            }
            else -> {
                changeStatusBarColor(R.color.red_50)
            }
        }
        binding.content.setBackgroundResource(
            when (i) {
                0 -> {
                    R.drawable.bg_gradient_boarding_1
                }
                1 -> {
                    R.drawable.bg_gradient_boarding_2
                }
                else -> {
                    R.drawable.bg_gradient_boarding_3
                }
            }
        )

    }

    private class ViewPagerFragmentAdapter(
        fragmentActivity: FragmentActivity
    ) :
        FragmentStateAdapter(fragmentActivity) {

        override fun createFragment(position: Int): Fragment {
            when (position) {
                0 -> return Boarding1Fragment()
                1 -> return Boarding2Fragment()
                2 -> return Boarding3Fragment()
            }
            return Boarding1Fragment()
        }

        override fun getItemCount(): Int {
            return 3
        }
    }

    private fun loginAsGuest() {
        apiCall = retrofitClient.loginAsGuest(deviceId)
        viewModel.loginAsGuest(apiCall)
    }
    private fun getBanner() {
        apiCall = retrofitClient.getBanner("dashboard")
        viewModel.getBanner(apiCall)
    }
    private fun observeApiResponse() {
        viewModel.getApiResponse().observe(this, {
            when (it.status) {
                ApiStatus.LOADING -> {
                    Loader.showLoader(this) {
                        if (this@BoardingActivity::apiCall.isInitialized)
                            apiCall.cancel()
                    }
                }
                ApiStatus.ERROR -> {
                    Loader.hideLoader()
                    AppUtils.showToast(this@BoardingActivity, it.message!!, false)
                }
                ApiStatus.SUCCESS -> {
                    Loader.hideLoader()
                    when (it.tag) {
                        ApiTags.GUEST_LOGIN -> {
                            val model = Gson().fromJson(it.data.toString(), LoginModel::class.java)
                            if(model.message == "Logged in as guest") {
                                SharedPreference.saveBoolean(
                                    this@BoardingActivity,
                                    Constants.isUserLoggedIn,
                                    true
                                )
                                SharedPreference.saveString(
                                    this@BoardingActivity,
                                    Constants.accessToken,
                                    model.data._token
                                )
                                AppUtils.saveUserModel(this@BoardingActivity, model.data.user)
                                val i = Intent(this@BoardingActivity, HomeActivity::class.java)
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(i)
                            }
                        }
                        ApiTags.GET_BANNER -> {
                            val model = Gson().fromJson(it.data.toString(), BannerModel::class.java)
                            imagesList.addAll(model.data)
                            binding.skeletonLayout.viewGone()
                            skeletonLayout.showOriginal()
                            setImagesRecView()
                            setupScene()
                            setViewPager()
                            binding.btnGuest.viewVisible()
                            binding.btnLogIn.viewVisible()
                            binding.btnSignUp.viewVisible()
                        }
                    }
                }
            }
        })
    }
}