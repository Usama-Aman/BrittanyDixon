package com.cp.brittany.dixon.activities.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.astritveliu.boom.Boom
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.auth.models.UserDetailData
import com.cp.brittany.dixon.activities.home.food_tab.FoodFragment
import com.cp.brittany.dixon.activities.home.food_tab.RecipeDetailActivity
import com.cp.brittany.dixon.activities.home.insight_rec_detail.InsightRecommendationDetailActivity
import com.cp.brittany.dixon.activities.home.insight_tab.InsightFragment
import com.cp.brittany.dixon.activities.home.premium.BillingClientSetup.billingClient
import com.cp.brittany.dixon.activities.home.premium.BillingClientSetup.queryPurchases
import com.cp.brittany.dixon.activities.home.premium.SubscriptionBoughtActivity
import com.cp.brittany.dixon.activities.home.profile_tab.ProfileFragment
import com.cp.brittany.dixon.activities.home.shops_tab.ComingShopFragment
import com.cp.brittany.dixon.activities.home.shops_tab.ShopFragment
import com.cp.brittany.dixon.activities.home.workout_detail.WorkoutDetailActivity
import com.cp.brittany.dixon.activities.home.workout_tab.WorkoutFragment
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.base.transparentStatusBar
import com.cp.brittany.dixon.databinding.ActivityHomeBinding
import com.cp.brittany.dixon.notification.NotificationModel
import com.cp.brittany.dixon.utils.AppUtils
import com.cp.brittany.dixon.utils.Constants
import com.cp.brittany.dixon.utils.Constants.monthly_package
import com.cp.brittany.dixon.utils.Constants.quarterly_package
import com.cp.brittany.dixon.utils.Constants.test_1
import com.cp.brittany.dixon.utils.Constants.test_2
import com.cp.brittany.dixon.utils.Constants.yearly_package
import com.cp.brittany.dixon.utils.FragmentEnums
import com.cp.brittany.dixon.utils.SharedPreference
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import org.json.JSONObject

class HomeActivity : BaseActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var currentFragment: Fragment
    private lateinit var vpf: ViewPagerFragmentAdapter
    private var isGuest = 0
    private var isSubscribed = false

    private var referralCode = ""
    private var workoutCode = ""
    private var insightCode = ""
    private lateinit var model: NotificationModel
    private lateinit var data: UserDetailData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeBarColor(R.color.white)
        blackStatusBarIcons()
        data = AppUtils.getUserDetails(applicationContext)
        isGuest = data.is_guest
        isSubscribed = data.isSubscribed
        if (!isSubscribed) {
            subscribeToTopic()
        }
        listener()
        initVar()
        binding.viewWorkout.callOnClick()
        getLinkFromIntent(intent)
    }

    fun changeBarColor(color: Int) {
        changeStatusBarColor(color)
    }

    private fun initVar() {
        Boom(binding.groupWorkout)
        Boom(binding.groupFood)
        Boom(binding.groupShop)
        Boom(binding.groupInsight)
        Boom(binding.groupProfile)
    }

    private fun listener() {
        binding.viewWorkout.setOnClickListener {
            colorControl(0)
            blackStatusBarIcons()
            currentFragment = WorkoutFragment()
            changeFragmentWithoutReCreation(currentFragment, FragmentEnums.WORKOUT_FRAGMENT.type)
        }

        binding.viewFood.setOnClickListener {
            colorControl(1)
            blackStatusBarIcons()
            currentFragment = FoodFragment()
            changeFragmentWithoutReCreation(currentFragment, FragmentEnums.FOOD_FRAGMENT.type)
        }

        binding.viewShop.setOnClickListener {
            colorControl(2)
//            currentFragment = ShopFragment()
            transparentStatusBar()
            currentFragment = ComingShopFragment()
            changeFragmentWithoutReCreation(currentFragment, FragmentEnums.SHOP_FRAGMENT.type)
        }

        binding.viewInsight.setOnClickListener {
            colorControl(3)
            blackStatusBarIcons()
            currentFragment = InsightFragment()
            changeFragmentWithoutReCreation(currentFragment, FragmentEnums.INSIGHT_FRAGMENT.type)
        }

        binding.viewProfile.setOnClickListener {
            val data = AppUtils.getUserDetails(applicationContext)
            isGuest = data.is_guest
            blackStatusBarIcons()
            if (isGuest == 0) {
                colorControl(4)
                currentFragment = ProfileFragment()
                changeFragmentWithoutReCreation(currentFragment, FragmentEnums.PROFILE_FRAGMENT.type)
            } else
                showLoginDialog()
        }
    }

    private fun colorControl(i: Int) {
        binding.ivWorkout.setImageResource(
            if (i == 0) {
                R.drawable.ic_workout_active
            } else {
                R.drawable.ic_workout
            }
        )
        binding.ivFood.setImageResource(
            if (i == 1) {
                R.drawable.ic_food_active
            } else {
                R.drawable.ic_food
            }
        )
        binding.ivShop.setImageResource(
            if (i == 2) {
                R.drawable.ic_shop_active
            } else {
                R.drawable.ic_shop
            }
        )
        binding.ivInsight.setImageResource(
            if (i == 3) {
                R.drawable.ic_insight_active
            } else {
                R.drawable.ic_insight
            }
        )
        binding.ivProfile.setImageResource(
            if (i == 4) {
                R.drawable.ic_profile_active
            } else {
                R.drawable.ic_profile
            }
        )
        if (i == 4) {
            changeStatusBarColor(R.color.light_blue_50)
        } else {
            changeStatusBarColor(R.color.white)
        }
        binding.tvWorkout.setTextColor(resources.getColor(if (i == 0) R.color.bottom_bar_text_selected else R.color.blue_grey_900))
        binding.tvFood.setTextColor(resources.getColor(if (i == 1) R.color.bottom_bar_text_selected else R.color.blue_grey_900))
        binding.tvShop.setTextColor(resources.getColor(if (i == 2) R.color.bottom_bar_text_selected else R.color.blue_grey_900))
        binding.tvInsight.setTextColor(resources.getColor(if (i == 3) R.color.bottom_bar_text_selected else R.color.blue_grey_900))
        binding.tvProfile.setTextColor(resources.getColor(if (i == 4) R.color.bottom_bar_text_selected else R.color.blue_grey_900))

    }

    private class ViewPagerFragmentAdapter(
        fragmentActivity: FragmentActivity
    ) :
        FragmentStateAdapter(fragmentActivity) {
        private val context: Context = fragmentActivity

        override fun createFragment(position: Int): Fragment {


            when (position) {
                0 -> return WorkoutFragment()
                1 -> return FoodFragment()
                2 -> return ShopFragment()
                3 -> return InsightFragment()
                4 -> return ProfileFragment()
            }
            return WorkoutFragment()
        }

        override fun getItemCount(): Int {
            return 5
        }
    }

    private fun getLinkFromIntent(i: Intent?) {
        if (i != null) {
            if (intent.hasExtra("myData"))
                model = intent.getParcelableExtra("myData")!!
            //val parameters = data.pathSegments
            referralCode = i.getStringExtra(Constants.referralCode) ?: ""
            workoutCode = i.getStringExtra(Constants.workoutCode) ?: ""
            insightCode = i.getStringExtra(Constants.insightCode) ?: ""

            when {
                ::model.isInitialized -> {
                    when (model.type) {
                        "foods" -> {
                            val i = Intent(applicationContext, RecipeDetailActivity::class.java)
                            i.putExtra("foodId", model.type_id)
                            startActivity(i)
                        }
                        "workouts" -> {
                            val i = Intent(applicationContext, WorkoutDetailActivity::class.java)
                            i.putExtra("workoutId", model.type_id)
                            startActivity(i)
                        }
                        "insights" -> {
                            val i = Intent(applicationContext, InsightRecommendationDetailActivity::class.java)
                            i.putExtra("id", model.type_id.toInt())
                            startActivity(i)
                        }
                    }
                }
                workoutCode != "" -> {
                    val i = Intent(applicationContext, WorkoutDetailActivity::class.java)
                    i.putExtra("workoutId", workoutCode)
                    startActivity(i)
                }
                insightCode != "" -> {
                    val i = Intent(applicationContext, InsightRecommendationDetailActivity::class.java)
                    i.putExtra("id", insightCode.toInt())
                    startActivity(i)
                }
            }

        }
        /*  AppController.profileReferralCode = data?.getQueryParameter(Constants.referralCode).toString()*/

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            //val parameters = data.pathSegments
            referralCode = intent.getStringExtra(Constants.referralCode) ?: ""
            workoutCode = intent.getStringExtra(Constants.workoutCode) ?: ""
            insightCode = intent.getStringExtra(Constants.insightCode) ?: ""

            when {
                ::model.isInitialized -> {
                    when (model.type) {
                        "foods" -> {
                            val i = Intent(applicationContext, RecipeDetailActivity::class.java)
                            i.putExtra("foodId", model.type_id)
                            startActivity(i)
                        }
                        "workouts" -> {
                            val i = Intent(applicationContext, WorkoutDetailActivity::class.java)
                            i.putExtra("workoutId", model.type_id)
                            startActivity(i)
                        }
                        "insights" -> {
                            val i = Intent(applicationContext, InsightRecommendationDetailActivity::class.java)
                            i.putExtra("id", model.type_id.toInt())
                            startActivity(i)
                        }
                    }
                }
                workoutCode != "" -> {
                    val i = Intent(applicationContext, WorkoutDetailActivity::class.java)
                    i.putExtra("workoutId", workoutCode)
                    startActivity(i)
                }
                insightCode != "" -> {
                    val i = Intent(applicationContext, InsightRecommendationDetailActivity::class.java)
                    i.putExtra("id", insightCode.toInt())
                    startActivity(i)
                }
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        BillingClientSetup.destroyConnection()
    }

    @SuppressLint("HardwareIds")
    private fun subscribeToTopic() {
        Firebase.messaging.subscribeToTopic("brittany")
            .addOnCompleteListener { task ->
                var msg = "subscribed"
                if (!task.isSuccessful) {
                    msg = "subscribe_failed"
                }
                if (task.isSuccessful) {
                    data.isSubscribed = true
                    AppUtils.saveUserModel(applicationContext, data)
                }
                Log.d("subscribeToTopic", msg)
            }

    }

    override fun onResume() {
        super.onResume()

        if (billingClient.isReady) {
            queryPurchases() {

                var countOfExpiredOrPausePackages = 0
                for (i in it.indices) {
                    if (it[i].purchaseState != 1)
                        countOfExpiredOrPausePackages++
                }

                if (countOfExpiredOrPausePackages == it.size) {
                    SharedPreference.saveBoolean(this@HomeActivity, Constants.isPremiumUser, false)
                } else {
                    SharedPreference.saveBoolean(this@HomeActivity, Constants.isPremiumUser, it.isNotEmpty())
                }

                if (SharedPreference.getBoolean(this@HomeActivity, Constants.isComingAfterSubscribing)) {
                    SharedPreference.saveBoolean(this@HomeActivity, Constants.isComingAfterSubscribing, false)
                    if (it.isNotEmpty()) {
                        val json = JSONObject(it.last().originalJson)
                        val intent = Intent(this@HomeActivity, SubscriptionBoughtActivity::class.java)
                        intent.putExtra("subscriptionData", json.toString())
                        startActivity(intent)
                    }
                }
                Log.d("HomeActivity", "Query Purchases called from on resume")
            }
        }
    }
}
