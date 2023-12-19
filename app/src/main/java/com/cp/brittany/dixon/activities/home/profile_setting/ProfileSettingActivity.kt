package com.cp.brittany.dixon.activities.home.profile_setting

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModelProviders
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.OnAlertOneButtonClickListener
import com.cp.brittany.dixon.activities.auth.login.SignInActivity
import com.cp.brittany.dixon.activities.auth.workout_method.WorkoutMethodActivity
import com.cp.brittany.dixon.activities.home.bookmarks.BookmarksActivity
import com.cp.brittany.dixon.activities.home.cart.CartActivity
import com.cp.brittany.dixon.activities.home.change_email.ChangeEmailActivity
import com.cp.brittany.dixon.activities.home.change_password.ChangePasswordActivity
import com.cp.brittany.dixon.activities.home.edit_profile.EditProfileActivity
import com.cp.brittany.dixon.activities.home.faq_feedback.FaqFeedbackActivity
import com.cp.brittany.dixon.activities.home.invite_friend.InviteFriendActivity
import com.cp.brittany.dixon.activities.home.models.ProfileData
import com.cp.brittany.dixon.activities.home.models.ProfileModel
import com.cp.brittany.dixon.activities.home.my_orders.MyOrdersActivity
import com.cp.brittany.dixon.activities.home.payment.PaymentActivity
import com.cp.brittany.dixon.activities.home.shipping_address.ShippingAddressActivity
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivityProfileSettingBinding
import com.cp.brittany.dixon.loader.Loader
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.ApiStatus
import com.cp.brittany.dixon.utils.AppUtils
import com.cp.brittany.dixon.utils.Constants
import com.cp.brittany.dixon.utils.SharedPreference
import com.bumptech.glide.Glide
import com.cp.brittany.dixon.activities.boarding.BoardingActivity
import com.cp.brittany.dixon.activities.home.checkout_payment.AddNewCardActivity
import com.cp.brittany.dixon.databinding.ActivityAddNewCardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import androidx.annotation.NonNull
import com.cp.brittany.dixon.activities.auth.dietary_preference.DietaryPreferencesActivity
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task


class ProfileSettingActivity : BaseActivity() {

    private lateinit var binding: ActivityProfileSettingBinding

    private lateinit var mContext: Context
    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var profileData: ProfileData
    private var isFacebookLogin = false
    private var isProfileEditClick: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext = this
        retrofitClient = RetrofitClient.getClient(this).create(Api::class.java)
        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()

        initViews()
        initVM()
        initListeners()
        observeApiResponse()
    }

    private fun initViews() {
        if (SharedPreference.getBoolean(applicationContext, Constants.isUserLoggedIn)) {
            val data = AppUtils.getUserDetails(this)
            if (data.is_guest == 0 && data.social_type != null && data.social_type == "google") {
                initializeGoogleLogin()
            } else if (data.is_guest == 0 && data.social_type != null && data.social_type == "facebook") {
                isFacebookLogin = true
            }
        }
    }

    private fun initializeGoogleLogin() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(resources.getString(R.string.google_sign_in_key_CP))
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun initVM() {
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        binding.lifecycleOwner = this
    }

    private fun initListeners() {
        binding.ivBack.setOnClickListener {
            AppUtils.preventDoubleClick(binding.ivBack)
            onBackPressed()
        }
//        binding.llEmail.setOnClickListener {
//            val intent = Intent(this, ChangeEmailActivity::class.java)
//            intent.putExtra("email", profileData.user_data.email)
//            startActivity(intent)
//            isProfileEditClick = false
//        }
        binding.llChangePassword.setOnClickListener {
            AppUtils.preventDoubleClick(binding.llChangePassword)
            val intent = Intent(this, ChangePasswordActivity::class.java)
            intent.putExtra("email", profileData.user_data.email)
            startActivity(intent)
            isProfileEditClick = false
        }
        binding.llPayment.setOnClickListener {
            AppUtils.preventDoubleClick(binding.llPayment)
            val intent = Intent(this, PaymentActivity::class.java)
            if (profileData.card != null)
                intent.putExtra("card", profileData.card)
            else {
                isProfileEditClick = false
                startActivity(Intent(applicationContext, AddNewCardActivity::class.java))
                return@setOnClickListener
            }
            startActivity(intent)
            isProfileEditClick = false
        }
        binding.llBookmark.setOnClickListener {
            AppUtils.preventDoubleClick(binding.llBookmark)
            startActivity(Intent(this, BookmarksActivity::class.java))
        }
        binding.llInviteFriends.setOnClickListener {
            startActivity(Intent(this, InviteFriendActivity::class.java))
            isProfileEditClick = false
        }
        binding.llAddress.setOnClickListener {
            AppUtils.preventDoubleClick(binding.llAddress)
            val i = Intent(this, ShippingAddressActivity::class.java)
            for (item in profileData.shipping_addresses) {
                if (item.is_default == 1) {
                    i.putExtra("address", item)
                    break
                }
            }
            if (!i.hasExtra("address") && profileData.shipping_addresses.isNotEmpty()) {
                i.putExtra("address", profileData.shipping_addresses[0])
            }
            i.putExtra("name", profileData.user_data.name)
            startActivity(i)
            isProfileEditClick = false
        }
        binding.llFaqsFeedback.setOnClickListener {
            AppUtils.preventDoubleClick(binding.llFaqsFeedback)
            startActivity(Intent(this, FaqFeedbackActivity::class.java))
            isProfileEditClick = false
        }
        binding.llMyOrders.setOnClickListener {
            AppUtils.preventDoubleClick(binding.llMyOrders)
            startActivity(Intent(this, MyOrdersActivity::class.java))
            isProfileEditClick = false
        }
        binding.llCart.setOnClickListener {
            AppUtils.preventDoubleClick(binding.llCart)
            startActivity(Intent(this, CartActivity::class.java))
            isProfileEditClick = false
        }
        binding.ivEdit.setOnClickListener {
            AppUtils.preventDoubleClick(binding.ivEdit)
            isProfileEditClick = true
            val intent = Intent(this, EditProfileActivity::class.java)
            intent.putExtra("profileData", profileData.user_data)
            startActivity(intent)
        }
        binding.llPreferences.setOnClickListener {
            AppUtils.preventDoubleClick(binding.llPreferences)
            val intent = Intent(this, WorkoutMethodActivity::class.java)
            startActivity(intent)
            isProfileEditClick = false
        }
        binding.llPreferencesDiet.setOnClickListener {
            AppUtils.preventDoubleClick(binding.llPreferencesDiet)
            val intent = Intent(this, DietaryPreferencesActivity::class.java)
            startActivity(intent)
            isProfileEditClick = false
        }
        binding.btnLogOut.setOnClickListener {
            AppUtils.preventDoubleClick(binding.btnLogOut)
            Loader.showLoader(this) {

            }
            showDialog("Confirmation", "Do you want to Logout", false, object : OnAlertOneButtonClickListener {
                override fun okClickListener() {
                    when {
                        ::mGoogleSignInClient.isInitialized -> {
                            signOutGoogle()
                        }
                        isFacebookLogin -> {
                            disconnectFromFacebook()
                        }
                        FirebaseAuth.getInstance().currentUser != null -> {
                            appleLogout()
                        }
                        else -> {
                            logOut()
                        }
                    }
                }

                override fun cancelClickListener() {
                    Loader.hideLoader()
                }

            })
        }
    }

    private fun appleLogout() {
        FirebaseAuth.getInstance().signOut()
        logOut()
    }

    @SuppressLint("NotifyDataSetChanged")
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
                        ApiTags.GET_PROFILE -> {
                            val model = Gson().fromJson(it.data.toString(), ProfileModel::class.java)
                            if (isProfileEditClick) {
                                val userModel = AppUtils.getUserDetails(applicationContext)
                                userModel.image_path = model.data.user_data.image_path
                                AppUtils.saveUserModel(applicationContext, userModel)
                            }
                            profileData = model.data
                            setUpData()
                        }
                        ApiTags.LOGOUT -> {
                            SharedPreference.saveBoolean(applicationContext, Constants.isUserLoggedIn, false)
                            val intent = Intent(applicationContext, BoardingActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            Handler(Looper.getMainLooper()).postDelayed({
                                startActivity(intent)
                                finish()
                            }, 1000)

                        }

                    }
                }
            }
        })
    }

    private fun setUpData() {
        if (profileData != null) {
            Glide.with(this)
                .load(profileData.user_data.image_path)
                .placeholder(R.drawable.ic_placeholder)
                .into(binding.ivProfile)

            binding.tvProfileName.text = profileData.user_data.name
            binding.tvEmail.text = profileData.user_data.email
            binding.tvAddress.text = profileData.user_data.address
            binding.tvBookmark.text = profileData.bookmarks.toString()
            binding.tvCart.text = profileData.cart_items.toString()
        }
    }

    private fun getProfileData() {
        apiCall = retrofitClient.getProfile()
        viewModel.getProfile(apiCall)
    }

    override fun onResume() {
        super.onResume()
        getProfileData()
    }

    private fun signOutGoogle() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    logOut()
                }
            }
    }

    fun disconnectFromFacebook() {
        if (AccessToken.getCurrentAccessToken() == null) {
            logOut()
            return  // already logged out
        }
        GraphRequest(
            AccessToken.getCurrentAccessToken(),
            "/me/permissions/",
            null,
            HttpMethod.DELETE,
            GraphRequest.Callback {
                LoginManager.getInstance().logOut()
                logOut()
            }).executeAsync()
    }

    fun logOut() {
        apiCall = retrofitClient.logOut()
        viewModel.logout(apiCall)
    }
}