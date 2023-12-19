package com.cp.brittany.dixon.activities.auth.forgot_password

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.auth.verify_otp.VerifyOtpActivity
import com.cp.brittany.dixon.activities.view_models.AuthViewModel
import com.cp.brittany.dixon.base.AppController
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivityForgotPasswordBinding
import com.cp.brittany.dixon.loader.Loader
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.*
import okhttp3.ResponseBody
import retrofit2.Call

class ForgotPasswordActivity : BaseActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var mContext: Context
    private lateinit var viewModel: AuthViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>

    private var fromProfile = false
    private var type = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()

        mContext = this
        retrofitClient = RetrofitClient.getClient(this).create(Api::class.java)

        initVM()
        observeApiResponse()
        initViews()
        listener()
        setEditTexts()
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
                        if (this@ForgotPasswordActivity::apiCall.isInitialized)
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
                        ApiTags.FORGOT_PASSWORD -> {
                            AppUtils.showToast(this, it.data?.getString("message")!!, true)
                            val data = it.data.getJSONObject("data")
                            AppController.resetOTP = data.getInt("verification_code")
                            val intent = Intent(mContext, VerifyOtpActivity::class.java)
                            intent.putExtra("email", binding.etEmail.text.toString())
                            intent.putExtra("type", "resetPasswordByEmail")
                            intent.putExtra("fromProfile", fromProfile)
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

    private fun listener() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.btnSubmit.setOnClickListener {
            if (validate())
                forgotPassword()
        }
    }

    private fun forgotPassword() {
        apiCall = retrofitClient.forgotPassword(binding.etEmail.text.trim().toString())
        viewModel.forgotPassword(apiCall)
    }

    private fun initViews() {
        type = intent.getStringExtra("type").toString()
        fromProfile = intent.getBooleanExtra("fromProfile", false)
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
        return true
    }
}