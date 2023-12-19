package com.cp.brittany.dixon.activities.home.change_password

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.lifecycle.ViewModelProviders
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.auth.login.SignInActivity
import com.cp.brittany.dixon.activities.view_models.AuthViewModel
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivityChangePasswordBinding
import com.cp.brittany.dixon.loader.Loader
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.*
import com.google.android.material.textfield.TextInputLayout
import okhttp3.ResponseBody
import retrofit2.Call
import java.util.*

class ChangePasswordActivity : BaseActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var retrofitClient: Api
    private lateinit var viewModel: AuthViewModel
    private lateinit var apiCall: Call<ResponseBody>
    private lateinit var email: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()
        retrofitClient = RetrofitClient.getClient(this).create(Api::class.java)

        initViews()
        initVM()
        observeApiResponse()
        initListeners()
    }
    private fun initVM() {
        viewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        binding.lifecycleOwner = this
    }
    private fun initViews() {
        email = intent.getStringExtra("email").toString()
    }

    private fun initListeners() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.ivOldPasswordToggle.setOnClickListener {
            hideShowPassword(
                binding.etOldPassword,
                binding.ivOldPasswordToggle
            )
        }
        binding.ivNewPasswordToggle.setOnClickListener {
            hideShowPassword(
                binding.etNewPassword,
                binding.ivNewPasswordToggle
            )
        }
        binding.ivRepeatNewPasswordToggle.setOnClickListener {
            hideShowPassword(
                binding.etRepeatNewPassword,
                binding.ivRepeatNewPasswordToggle
            )
        }
        binding.btnUpdate.setOnClickListener {
            if(validate()){
                updatePassword()
            }
        }
        validateEditText(binding.etOldPassword,binding.tilOldPassword)
        validateEditText(binding.etNewPassword,binding.tilNewPassword)
        validateEditText(binding.etRepeatNewPassword,binding.tilConfirmPassword)
    }

    private fun validate(): Boolean {
        if (binding.etOldPassword.text.toString().trim().isBlank()) {
            binding.etOldPassword.setBackgroundResource(R.drawable.auth_edit_text_error)
            binding.tilOldPassword.error = "Please enter your old password"
            binding.tilOldPassword.requestFocus()
            return false
        }
        if (binding.etOldPassword.text.toString().trim().length < 8) {
            binding.etOldPassword.setBackgroundResource(R.drawable.auth_edit_text_error)
            binding.tilOldPassword.error = "Password length must be at least 8 characters"
            binding.tilOldPassword.requestFocus()
            return false
        }


        if (binding.etNewPassword.text.toString().trim().isBlank()) {
            binding.etNewPassword.setBackgroundResource(R.drawable.auth_edit_text_error)
            binding.tilNewPassword.error = "Please enter your new password"
            binding.etNewPassword.requestFocus()
            return false
        }
        if (binding.etNewPassword.text.toString().trim().length < 8) {
            binding.etNewPassword.setBackgroundResource(R.drawable.auth_edit_text_error)
            binding.tilNewPassword.error = "Password length must be at least 8 characters"
            binding.etNewPassword.requestFocus()
            return false
        }

        if(binding.etNewPassword.text.toString().trim().equals(binding.etOldPassword.text.toString().trim())){
            binding.etNewPassword.setBackgroundResource(R.drawable.auth_edit_text_error)
            binding.tilNewPassword.error = "New password can't be same as old password"
            binding.etNewPassword.requestFocus()
            return false
        }

        if (binding.etRepeatNewPassword.text.toString().trim().isBlank()) {
            binding.etRepeatNewPassword.setBackgroundResource(R.drawable.auth_edit_text_error)
            binding.tilConfirmPassword.error = "Please enter your new password again"
            binding.etRepeatNewPassword.requestFocus()
            return false
        }

        if (binding.etRepeatNewPassword.text.toString().trim().length < 8) {
            binding.etRepeatNewPassword.setBackgroundResource(R.drawable.auth_edit_text_error)
            binding.tilConfirmPassword.error = "Password length must be at least 8 characters"
            binding.etRepeatNewPassword.requestFocus()
            return false
        }

        if(!binding.etNewPassword.text.toString().trim().equals(binding.etRepeatNewPassword.text.toString().trim())){
            binding.etRepeatNewPassword.setBackgroundResource(R.drawable.auth_edit_text_error)
            binding.tilConfirmPassword.error = "Password is not matched"
            binding.etRepeatNewPassword.requestFocus()
            return false
        }

        return true
    }

    private fun validateEditText(editText: EditText, layout: TextInputLayout) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                editText.setBackgroundResource(R.drawable.auth_edit_text_drawable)
                layout.error = null
                layout.isErrorEnabled = false
            }
        })
    }
    private fun updatePassword() {
        apiCall = retrofitClient.changePassword(
            email,
            binding.etOldPassword.text.toString(),
            binding.etNewPassword.text.toString(),
            binding.etRepeatNewPassword.text.toString()
        )
        viewModel.updatePassword(apiCall)
    }

    private fun observeApiResponse() {
        viewModel.getApiResponse().observe(this, {
            when (it.status) {
                ApiStatus.LOADING -> {
                    Loader.showLoader(this) {
                        if (this@ChangePasswordActivity::apiCall.isInitialized)
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
                        ApiTags.UPDATE_PASSWORD -> {
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
}