package com.cp.brittany.dixon.activities.auth.signup

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.auth.dietary_preference.DietaryPreferencesActivity
import com.cp.brittany.dixon.activities.auth.login.SignInActivity
import com.cp.brittany.dixon.activities.auth.models.RegisterModel
import com.cp.brittany.dixon.activities.auth.models.UserDetailData
import com.cp.brittany.dixon.activities.auth.verify_otp.VerifyOtpActivity
import com.cp.brittany.dixon.activities.auth.workout_method.WorkoutMethodActivity
import com.cp.brittany.dixon.activities.home.HomeActivity
import com.cp.brittany.dixon.activities.view_models.AuthViewModel
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivitySignUpBinding
import com.cp.brittany.dixon.loader.Loader
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.*
import com.astritveliu.boom.Boom
import com.cp.brittany.dixon.activities.home.cart.CartActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call

class SignUpActivity : BaseActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var mContext: Context
    private lateinit var viewModel: AuthViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>
    private var isFromCart = false
    private var isGuest = 0
    private var deviceId = ""
    private var fcmToken = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        getFcmTokenAndDeviceID()
        setContentView(binding.root)
        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()
        mContext = this
        retrofitClient = RetrofitClient.getClient(this).create(Api::class.java)

        initVM()
        observeApiResponse()
        setEditTexts()
        initVar()
        listener()
    }

    private fun initVM() {
        viewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        binding.lifecycleOwner = this
    }

    private fun observeApiResponse() {
        viewModel.getApiResponse().observe(this, {
            when (it.status) {
                ApiStatus.LOADING -> {
                    Loader.showLoader(this) {
                        if (this@SignUpActivity::apiCall.isInitialized)
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
                        ApiTags.REGISTER -> {
                            val model =
                                Gson().fromJson(it.data.toString(), RegisterModel::class.java)
                            SharedPreference.saveBoolean(
                                this@SignUpActivity,
                                Constants.isUserLoggedIn,
                                true
                            )
                            SharedPreference.saveString(
                                this@SignUpActivity,
                                Constants.accessToken,
                                model.data._token
                            )
                            AppUtils.saveUserModel(this@SignUpActivity, model.data.user)
                            checkForUserData(model.data.user)
                        }
                        ApiTags.GUEST_REGISTER -> {
                            Log.e("https",it.data.toString())
                            val model =
                                Gson().fromJson(it.data.toString(), RegisterModel::class.java)
                            SharedPreference.saveBoolean(
                                this@SignUpActivity,
                                Constants.isUserLoggedIn,
                                true
                            )
                            SharedPreference.saveString(
                                this@SignUpActivity,
                                Constants.accessToken,
                                model.data._token
                            )
                            AppUtils.saveUserModel(this@SignUpActivity, model.data.user)
                            checkForUserData(model.data.user)
                        }
                    }
                }
            }
        })
    }

    private fun checkForUserData(data: UserDetailData) {
        val i: Intent
        when {
            data.is_email_verified == 0 -> {
                i = Intent(mContext, VerifyOtpActivity::class.java)
                i.putExtra("fromProfile", false)
                i.putExtra("isFromCart", isFromCart)
                i.putExtra("email", data.email)
                i.putExtra("type", "email")
                i.putExtra("isFromRegister", true)
            }
            isGuest == 1 -> {
                i = Intent(applicationContext,CartActivity::class.java)
            }
            data.total_workout_preferences == 0 -> {
                i = Intent(mContext, WorkoutMethodActivity::class.java)
                i.putExtra("isFromRegister", true)
            }
            data.total_diet_preferences == 0 -> {
                i = Intent(mContext, DietaryPreferencesActivity::class.java)
                i.putExtra("isFromRegister", true)
            }
            else -> {
                i = if(isFromCart){
                    Intent(mContext, CartActivity::class.java)
                } else {
                    Intent(mContext, HomeActivity::class.java)
                }
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                SharedPreference.saveBoolean(mContext, Constants.isUserLoggedIn, true)
            }
        }
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(i)
            finish()
        }, 1000)
    }


    private fun initVar() {
        if(intent.hasExtra("isFromCart")){
            isFromCart = intent.getBooleanExtra("isFromCart", false)
        }
        if(SharedPreference.getBoolean(this, Constants.isUserLoggedIn)) {
            val data = AppUtils.getUserDetails(applicationContext)
            isGuest = data.is_guest
        }
    }

    private fun register() {
        if(isGuest == 1){
            apiCall = retrofitClient.registerGuest(
                binding.etFullName.text.toString(),
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString(),
                binding.etRepeatPassword.text.toString()
            )
            viewModel.registerGuest(apiCall)
        }else {
            apiCall = retrofitClient.register(
                binding.etFullName.text.toString(),
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString(),
                binding.etRepeatPassword.text.toString(),
                deviceId,
                fcmToken
            )
            viewModel.register(apiCall)
        }
    }

    private fun listener() {
        Boom(binding.btnSignUp)
        binding.btnSignUp.setOnClickListener {
            if (validate())
                register()
        }
        binding.ivPasswordToggle.setOnClickListener {
            hideShowPassword(
                binding.etPassword,
                binding.ivPasswordToggle
            )
        }
        binding.ivRepeatPasswordToggle.setOnClickListener {
            hideShowPassword(
                binding.etRepeatPassword,
                binding.ivRepeatPasswordToggle
            )
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.tvLoginNow.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }

    private fun setEditTexts() {
        binding.etFullName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.etFullName.backgroundTintList =
                    ContextCompat.getColorStateList(mContext, R.color.view_line_color)
                binding.tilFullName.error = null
                binding.tilFullName.isErrorEnabled = false
            }
        })
        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.etEmail.backgroundTintList =
                    ContextCompat.getColorStateList(mContext, R.color.view_line_color)
                binding.tilEmail.error = null
                binding.tilEmail.isErrorEnabled = false
            }
        })
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.etPassword.backgroundTintList =
                    ContextCompat.getColorStateList(mContext, R.color.view_line_color)
                binding.tilPassword.error = null
                binding.tilPassword.viewGone()
            }
        })
        binding.etRepeatPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.etRepeatPassword.backgroundTintList =
                    ContextCompat.getColorStateList(mContext, R.color.view_line_color)
                binding.tilRepeatPassword.error = null
                binding.tilRepeatPassword.viewGone()
            }
        })
    }

    private fun validate(): Boolean {
        if (binding.etFullName.text.toString().isBlank()) {
            binding.etFullName.backgroundTintList =
                ContextCompat.getColorStateList(mContext, R.color.red)
            binding.tilFullName.viewVisible()
            binding.tilFullName.error = "Enter Full Name!"
            binding.etFullName.requestFocus()
            return false
        }

        if (binding.etEmail.text.toString().isBlank()) {
            binding.etEmail.backgroundTintList =
                ContextCompat.getColorStateList(mContext, R.color.red)
            binding.tilEmail.viewVisible()
            binding.tilEmail.error = "Enter Email!"
            binding.etEmail.requestFocus()
            return false
        }

        if (!binding.etEmail.text.toString().isValidEmail()) {
            binding.etEmail.backgroundTintList =
                ContextCompat.getColorStateList(mContext, R.color.red)
            binding.tilEmail.viewVisible()
            binding.tilEmail.error = "Email is invalid!"
            binding.etEmail.requestFocus()
            return false
        }
        if (binding.etPassword.text.toString().isBlank()) {
            binding.etPassword.backgroundTintList =
                ContextCompat.getColorStateList(mContext, R.color.red)
            binding.tilPassword.viewVisible()
            binding.tilPassword.error = "Enter Password!"
            binding.etPassword.requestFocus()
            return false
        }
        if (binding.etPassword.text.toString().length < 8) {
            binding.etPassword.backgroundTintList =
                ContextCompat.getColorStateList(mContext, R.color.red)
            binding.tilPassword.viewVisible()
            binding.tilPassword.error = "Password must be at least 8 characters long!"
            binding.etPassword.requestFocus()
            return false
        }

        if (binding.etRepeatPassword.text.toString().isBlank()) {
            binding.etRepeatPassword.backgroundTintList =
                ContextCompat.getColorStateList(mContext, R.color.red)
            binding.tilRepeatPassword.viewVisible()
            binding.tilRepeatPassword.error = "Enter Repeat Password!"
            binding.etRepeatPassword.requestFocus()
            return false
        }
        if (binding.etRepeatPassword.text.toString() != binding.etPassword.text.toString()) {
            binding.etRepeatPassword.backgroundTintList =
                ContextCompat.getColorStateList(mContext, R.color.red)
            binding.tilRepeatPassword.viewVisible()
            binding.tilRepeatPassword.error = "Repeat Password doesn't match!"
            binding.etRepeatPassword.requestFocus()
            return false
        }
        return true

    }
    @SuppressLint("HardwareIds")
    private fun getFcmTokenAndDeviceID(){
        deviceId = Settings.Secure.getString(
            this.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        Log.d("deviceId", deviceId)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("fcm Token", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            fcmToken = task.result

            // Log and toast
            Log.d("fcm Token", fcmToken)
        })
    }
}