package com.cp.brittany.dixon.activities.auth.create_new_password

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
import com.cp.brittany.dixon.activities.auth.login.SignInActivity
import com.cp.brittany.dixon.activities.view_models.AuthViewModel
import com.cp.brittany.dixon.base.AppController
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivityCreateNewPasswordBinding
import com.cp.brittany.dixon.loader.Loader
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.*
import com.astritveliu.boom.Boom
import okhttp3.ResponseBody
import retrofit2.Call

class CreateNewPasswordActivity : BaseActivity() {
    private lateinit var binding: ActivityCreateNewPasswordBinding
    private lateinit var mContext: Context
    private lateinit var viewModel: AuthViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>

    var email = ""
    var code = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateNewPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()

        mContext = this
        retrofitClient = RetrofitClient.getClient(this).create(Api::class.java)

        initVM()
        observeApiResponse()
        listener()
        initViews()
        setEditTexts()
    }

    private fun initViews() {
        Boom(binding.btnResetPassword)
        email = intent.getStringExtra("email").toString()
        code = AppController.resetOTP.toString()
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
                        if (this@CreateNewPasswordActivity::apiCall.isInitialized)
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
                        ApiTags.CREATE_NEW_PASSWORD -> {
                            AppUtils.showToast(this, it.data?.getString("message")!!, true)
                            SharedPreference.saveBoolean(this, Constants.isUserLoggedIn, false)
                            val intent = Intent(this, SignInActivity::class.java)
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

    private fun listener() {
        binding.ivPasswordToggle.setOnClickListener {
            hideShowPassword(
                binding.etNewPassword,
                binding.ivPasswordToggle
            )
        }
        binding.ivRepeatPasswordToggle.setOnClickListener {
            hideShowPassword(
                binding.etRepeatPassword,
                binding.ivRepeatPasswordToggle
            )
        }
        binding.btnResetPassword.setOnClickListener {
            if (validate())
                resetPassword()
        }
        binding.ivBack.setOnClickListener {
            super.onBackPressed()
        }
    }

    private fun resetPassword() {
        apiCall = retrofitClient.resetPassword(
            email,
            code,
            binding.etNewPassword.text.toString(),
            binding.etRepeatPassword.text.toString()
        )
        viewModel.createNewPassword(apiCall)
    }

    private fun setEditTexts() {

        binding.etNewPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.etNewPassword.backgroundTintList =
                    ContextCompat.getColorStateList(mContext, R.color.view_line_color)
                binding.tilNewPassword.error = null
                binding.tilNewPassword.viewGone()
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
        if (binding.etNewPassword.text.toString().isBlank()) {
            binding.etNewPassword.backgroundTintList =
                ContextCompat.getColorStateList(mContext, R.color.red)
            binding.tilNewPassword.viewVisible()
            binding.tilNewPassword.error = "Enter Password!"
            binding.etNewPassword.requestFocus()
            return false
        }
        if (binding.etNewPassword.text.toString().length < 8) {
            binding.etNewPassword.backgroundTintList =
                ContextCompat.getColorStateList(mContext, R.color.red)
            binding.tilNewPassword.viewVisible()
            binding.tilNewPassword.error = "Password must be at least 8 characters long!"
            binding.etNewPassword.requestFocus()
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
        if (binding.etRepeatPassword.text.toString() != binding.etNewPassword.text.toString()) {
            binding.etRepeatPassword.backgroundTintList =
                ContextCompat.getColorStateList(mContext, R.color.red)
            binding.tilRepeatPassword.viewVisible()
            binding.tilRepeatPassword.error = "Repeat Password doesn't match!"
            binding.etRepeatPassword.requestFocus()
            return false
        }
        return true
    }
}