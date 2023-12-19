package com.cp.brittany.dixon.activities.auth.verify_otp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.ViewModelProviders
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.auth.create_new_password.CreateNewPasswordActivity
import com.cp.brittany.dixon.activities.auth.workout_method.WorkoutMethodActivity
import com.cp.brittany.dixon.activities.home.cart.CartActivity
import com.cp.brittany.dixon.activities.view_models.AuthViewModel
import com.cp.brittany.dixon.base.AppController
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivityVerifyOtpBinding
import com.cp.brittany.dixon.loader.Loader
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.ApiStatus
import com.cp.brittany.dixon.utils.AppUtils
import okhttp3.ResponseBody
import retrofit2.Call

class VerifyOtpActivity : BaseActivity() {
    private lateinit var binding: ActivityVerifyOtpBinding
    private lateinit var mContext: Context
    private lateinit var viewModel: AuthViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>
    var email = ""
    var type = ""
    var fromProfile = false
    var isFromCart = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()

        mContext = this
        retrofitClient = RetrofitClient.getClient(this).create(Api::class.java)

        initVM()
        observeApiResponse()
        listener()
        initVar()
    }

    private fun initVar() {
        email = intent.getStringExtra("email").toString()
        type = intent.getStringExtra("type").toString()
        fromProfile = intent.getBooleanExtra("fromProfile", false)
        isFromCart = intent.getBooleanExtra("isFromCart", false)
        if (email.isNotBlank()) {
            if (email.substringBefore('@').length > 2)
                binding.tvDescriptionEmail.text = resources.getString(
                    R.string.sent_you_code_description,
                    "${email.substring(0, 2)}******@${email.substringAfter('@')}"
                )
            else
                binding.tvDescriptionEmail.text = resources.getString(
                    R.string.sent_you_code_description,
                    "${email.substring(0, 1)}******@${email.substringAfter('@')}"
                )
        }

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
                        if (this@VerifyOtpActivity::apiCall.isInitialized)
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
                        ApiTags.VERIFY_OTP -> {
                            handleResponse(it?.data?.getString("message")!!)
                        }
                        ApiTags.RESEND_OTP -> {
                            AppUtils.showToast(this, it?.data?.getString("message")!!, true)
                            val data = it.data.getJSONObject("data")
                            AppController.resetOTP = data.getInt("verification_code")
                        }
                    }
                }
            }
        })
    }

    private fun handleResponse(message: String) {
        AppUtils.showToast(this, message, true)
        val data = AppUtils.getUserDetails(mContext)
        data.is_email_verified = 1
        AppUtils.saveUserModel(mContext, data)
        var i = Intent(mContext, WorkoutMethodActivity::class.java)
        i.putExtra("isFromRegister", true)
        if(isFromCart){
            i = Intent(mContext, CartActivity::class.java)
            //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(i)
            finish()
        }, 1000)
    }


    private fun listener() {
        binding.btnNextStep.setOnClickListener {
            when (type) {
                "email" -> {
                    apiCall = retrofitClient.verifyOTP(binding.etCode.text.toString())
                    viewModel.verifyOTP(apiCall)
                }
                "resetPasswordByEmail" -> {
                    if (AppController.resetOTP.toString() == binding.etCode.text.toString()) {
                        val intent = Intent(mContext, CreateNewPasswordActivity::class.java)
                        intent.putExtra("email", email)
                        startActivity(intent)
                        finish()
                    } else {
                        AppUtils.showToast(this, "OTP is invalid", false)
                    }
                }
            }
        }
        binding.tvResendCode.setOnClickListener {
            binding.etCode.text = null
            apiCall = retrofitClient.resendOtp(email)
            viewModel.resendOTP(apiCall)
        }
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.etCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null)
                    setOtpNumbers(otp = s.toString())
                else
                    setOtpNumbers(otp = "")
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    private fun setOtpNumbers(otp: String) {
        when {
            otp.isEmpty() -> {
                binding.tvDigit1.text = ""
                binding.tvDigit2.text = ""
                binding.tvDigit3.text = ""
                binding.tvDigit4.text = ""
            }
            otp.length == 1 -> {
                binding.tvDigit1.text = otp
                binding.tvDigit2.text = ""
                binding.tvDigit3.text = ""
                binding.tvDigit4.text = ""
            }
            otp.length == 2 -> {
                binding.tvDigit1.text = otp.substring(0, 1)
                binding.tvDigit2.text = otp.substring(1, 2)
                binding.tvDigit3.text = ""
                binding.tvDigit4.text = ""
            }
            otp.length == 3 -> {
                binding.tvDigit1.text = otp.substring(0, 1)
                binding.tvDigit2.text = otp.substring(1, 2)
                binding.tvDigit3.text = otp.substring(2, 3)
                binding.tvDigit4.text = ""
            }
            otp.length == 4 -> {
                binding.tvDigit1.text = otp.substring(0, 1)
                binding.tvDigit2.text = otp.substring(1, 2)
                binding.tvDigit3.text = otp.substring(2, 3)
                binding.tvDigit4.text = otp.substring(3, 4)
            }
        }
    }

}