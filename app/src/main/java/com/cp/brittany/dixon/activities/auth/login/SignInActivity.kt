package com.cp.brittany.dixon.activities.auth.login

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
import com.cp.brittany.dixon.activities.auth.forgot_password.ForgotPasswordActivity
import com.cp.brittany.dixon.activities.auth.models.LoginModel
import com.cp.brittany.dixon.activities.auth.models.UserDetailData
import com.cp.brittany.dixon.activities.auth.signup.SignUpActivity
import com.cp.brittany.dixon.activities.auth.verify_otp.VerifyOtpActivity
import com.cp.brittany.dixon.activities.auth.workout_method.WorkoutMethodActivity
import com.cp.brittany.dixon.activities.home.HomeActivity
import com.cp.brittany.dixon.activities.view_models.AuthViewModel
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivitySignInBinding
import com.cp.brittany.dixon.loader.Loader
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.*
import com.astritveliu.boom.Boom
import com.cp.brittany.dixon.activities.home.cart.CartActivity
import com.facebook.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.OAuthCredential
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import java.util.*


class SignInActivity : BaseActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var mContext: Context
    private lateinit var viewModel: AuthViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>
    private var isFromCart = false
    private var deviceId = ""
    private var fcmToken = ""
    private lateinit var mGoogleSignInClient: GoogleSignInClient
//    var firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getFcmTokenAndDeviceID()
        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()
        mContext = this
        retrofitClient = RetrofitClient.getClient(this).create(Api::class.java)

        initVM()
        initializeGoogleLogin()
        observeApiResponse()
        initVar()
        setEditTexts()
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
                        if (this@SignInActivity::apiCall.isInitialized)
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
                        ApiTags.LOGIN -> {
                            val model = Gson().fromJson(it.data.toString(), LoginModel::class.java)
                            SharedPreference.saveBoolean(
                                this@SignInActivity,
                                Constants.isUserLoggedIn,
                                true
                            )
                            SharedPreference.saveString(
                                this@SignInActivity,
                                Constants.accessToken,
                                model.data._token
                            )
                            AppUtils.saveUserModel(this@SignInActivity, model.data.user)
                            checkForUserData(model.data.user)
                        }
                        ApiTags.SOCIAL_LOGIN -> {
                            val model = Gson().fromJson(it.data.toString(), LoginModel::class.java)
                            SharedPreference.saveBoolean(
                                this@SignInActivity,
                                Constants.isUserLoggedIn,
                                true
                            )
                            SharedPreference.saveString(
                                this@SignInActivity,
                                Constants.accessToken,
                                model.data._token
                            )
                            AppUtils.saveUserModel(this@SignInActivity, model.data.user)
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
            data.email_verified_at == 0 -> {
                i = Intent(mContext, VerifyOtpActivity::class.java)
                i.putExtra("type", "email")
                i.putExtra("email", data.email)
                i.putExtra("isFromCart", isFromCart)
                i.putExtra("fromProfile", false)
                i.putExtra("isFromRegister", true)
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
                i = if (isFromCart) {
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
        Boom(binding.btnLogIn)
        Boom(binding.btnApple)
        Boom(binding.btnGoogle)
        Boom(binding.btnFacebook)
        if (intent.hasExtra("isFromCart")) {
            isFromCart = intent.getBooleanExtra("isFromCart", false)
        }

    }

    private fun listener() {
        binding.btnGoogle.setOnClickListener {
            if (::mGoogleSignInClient.isInitialized) {
                googleSignIn(mGoogleSignInClient) {
                    if (it != null) {
                        socialLogin(
                            it.id ?: "",
                            "google",
                            it.email ?: "",
                            it.displayName ?: ""
                        )
                    }
                }
            }
        }

        binding.btnFacebook.setOnClickListener {
            facebookLogin(binding.fbLoginButton) {
                if (it != null) {
                    socialLogin(
                        it.getString("id"), "facebook",
                        it.getString("email"), it.getString("name")
                    )
                }
            }
        }

        binding.btnApple.setOnClickListener {
            signInWithApple {
                if (it != null) {
                    if (it.user != null)
                        socialLogin(
                            it.user?.providerData?.get(1)?.uid ?: "", "apple",
                            it.user?.email ?: "", it.user?.displayName ?: ""
                        )
                } else
                    AppUtils.showToast(this@SignInActivity, "Apple Failed", false)
            }
        }

        binding.ivPasswordToggle.setOnClickListener {
            hideShowPassword(
                binding.etPassword,
                binding.ivPasswordToggle
            )
        }
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.tvForgotPassword.setOnClickListener {
            val intent = Intent(mContext, ForgotPasswordActivity::class.java)
            intent.putExtra("type", "forgotPassword")
            intent.putExtra("fromProfile", false)
            startActivity(intent)
        }
        binding.tvSignUpNow.setOnClickListener {
            val i = Intent(this, SignUpActivity::class.java)
            i.putExtra("isFromCart", isFromCart)
            startActivity(i)
        }
        binding.btnLogIn.setOnClickListener {
            if (validate())
                login()
        }
    }

    private fun socialLogin(socialId: String, socialType: String, email: String, name: String) {
        apiCall = retrofitClient.socialLogin(socialId, socialType, email, name,deviceId,fcmToken)
        viewModel.socialLogin(apiCall)
    }

    fun login() {
        apiCall = retrofitClient.login(
            binding.etEmail.text.toString().trim(),
            binding.etPassword.text.toString().trim(),
            deviceId,
            fcmToken)
        viewModel.login(apiCall)
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

    private fun setEditTexts() {
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
    }

    private fun validate(): Boolean {

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
/*
The registration token may change when:

The app is restored on a new device
The user uninstalls/reinstall the app
The user clears app data.
 */