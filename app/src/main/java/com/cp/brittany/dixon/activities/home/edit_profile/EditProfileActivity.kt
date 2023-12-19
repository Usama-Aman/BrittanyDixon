package com.cp.brittany.dixon.activities.home.edit_profile

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.*
import android.text.Editable
import android.text.TextWatcher
import android.webkit.URLUtil
import android.widget.EditText
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.models.TodayTasksModel
import com.cp.brittany.dixon.activities.home.models.UserData
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.base.BaseActivityResult
import com.cp.brittany.dixon.base.ImagePickerActivity
import com.cp.brittany.dixon.databinding.ActivityEditProfileBinding
import com.cp.brittany.dixon.loader.Loader
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.*
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.theartofdev.edmodo.cropper.CropImage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import java.util.*


class EditProfileActivity : BaseActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var vibrator: Vibrator
    private var myCalendar: Calendar = Calendar.getInstance()

    private var dob = ""
    private var filePath = ""
    private var heightUnit = "feet"
    private var weightUnit = "kg"
    private var genderType = ""

    private var isFeetSelected = true
    private var isMetersSelected = false
    private var isKgSelected = true
    private var isPoundsSelected = false


    private var userData: UserData? = null

    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        retrofitClient = RetrofitClient.getClient(this).create(Api::class.java)
        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()

        initViews()
        initVM()
        initListeners()
        observeApiResponse()
        if (userData != null) {
            setupData()
        }
    }

    private fun initViews() {
        userData = intent.getParcelableExtra("profileData")

        vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        binding.buttonGroup.setOnPositionChangedListener {
            genderType = if (it == 0) "Male" else "Female"
        }
    }

    private fun initVM() {
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        binding.lifecycleOwner = this
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupData() {

        Glide.with(this)
            .load(userData?.image_path)
            .placeholder(R.drawable.ic_placeholder)
            .into(binding.ivProfileImage)

        binding.etFullName.setText(userData?.name)
        binding.tvDateOfBirth.text = convertDateFormat(userData?.date_of_birth ?: "")

        genderType = userData?.gender ?: ""
        dob = userData?.date_of_birth ?: ""
        heightUnit = userData?.height?.height_unit ?: ""
        weightUnit = userData?.weight?.weight_unit ?: ""

        if (userData?.gender == "Male")
            binding.buttonGroup.setPosition(0, true)
        else
            binding.buttonGroup.setPosition(1, true)

        binding.etHeight.setText(userData?.height?.height)
        binding.etWeight.setText(userData?.weight?.weight)
        if (userData?.user_height_unit == "feet") {
            heightUnit = "feet"
            isFeetSelected = true
            isMetersSelected = false
            binding.ivMeters.setImageResource(R.drawable.ic_unchecked)
            binding.ivFeet.setImageResource(R.drawable.ic_checked)
        }
        if (userData?.user_height_unit == "meter") {
            heightUnit = "meter"
            isMetersSelected = true
            isFeetSelected = false
            binding.ivFeet.setImageResource(R.drawable.ic_unchecked)
            binding.ivMeters.setImageResource(R.drawable.ic_checked)
        }
        if (userData?.user_weight_unit == "kg") {
            weightUnit = "kg"
            isKgSelected = true
            isPoundsSelected = false
            binding.ivPounds.setImageResource(R.drawable.ic_unchecked)
            binding.ivKg.setImageResource(R.drawable.ic_checked)
        }
        if (userData?.user_weight_unit == "pound") {
            weightUnit = "pound"
            isPoundsSelected = true
            isKgSelected = false
            binding.ivKg.setImageResource(R.drawable.ic_unchecked)
            binding.ivPounds.setImageResource(R.drawable.ic_checked)
        }
    }

    private fun initListeners() {
        binding.metersLayout.setOnClickListener {
            heightUnit = "meter"
            isFeetSelected = false
            isMetersSelected = true
            binding.ivMeters.setImageResource(R.drawable.ic_checked)
            binding.ivFeet.setImageResource(R.drawable.ic_unchecked)
        }
        binding.feetLayout.setOnClickListener {
            heightUnit = "feet"
            isFeetSelected = true
            isMetersSelected = false
            binding.ivMeters.setImageResource(R.drawable.ic_unchecked)
            binding.ivFeet.setImageResource(R.drawable.ic_checked)
        }

        binding.kgLayout.setOnClickListener {
            weightUnit = "kg"
            isKgSelected = true
            isPoundsSelected = false
            binding.ivPounds.setImageResource(R.drawable.ic_unchecked)
            binding.ivKg.setImageResource(R.drawable.ic_checked)
        }
        binding.poundsLayout.setOnClickListener {
            weightUnit = "pound"
            isKgSelected = false
            isPoundsSelected = true
            binding.ivKg.setImageResource(R.drawable.ic_unchecked)
            binding.ivPounds.setImageResource(R.drawable.ic_checked)
        }

        binding.ivBack.setOnClickListener {
            AppUtils.preventDoubleClick(binding.ivBack)
            onBackPressed()
        }
        binding.tvDateOfBirth.setOnClickListener {
            val dpd = DatePickerDialog(
                this@EditProfileActivity,
                { _, year, month, dayOfMonth ->
                    myCalendar.set(Calendar.YEAR, year)
                    myCalendar.set(Calendar.MONTH, month)
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    dob = "$dayOfMonth-${month + 1}-$year"
                    binding.tvDateOfBirth.text = convertDateFormat(dob)

                }, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )

            val cal = Calendar.getInstance()
            /*val cal1 = Calendar.getInstance()
            cal1.add(Calendar.YEAR, -5)*/
            dpd.datePicker.maxDate = cal.timeInMillis
            /*dpd.datePicker.minDate = cal1.timeInMillis*/
            dpd.show()
        }

        binding.ivPickCamera.setOnClickListener {
            AppUtils.preventDoubleClick(binding.ivPickCamera)
            val intent = Intent(this@EditProfileActivity, ImagePickerActivity::class.java)
            activityLauncher.launch(intent, object : BaseActivityResult.OnActivityResult<ActivityResult> {
                override fun onActivityResult(result: ActivityResult) {
                    if (result.resultCode == Activity.RESULT_OK) {
                        val path = result.data?.getStringExtra("filePath") ?: ""
                        if (path != "") {
                            filePath = path

                            Glide.with(this@EditProfileActivity)
                                .load(filePath)
                                .into(binding.ivProfileImage)
                        }
                    }
                }
            })
        }

        binding.btnSaveProfile.setOnClickListener {
            AppUtils.preventDoubleClick(binding.btnSaveProfile)
            if (validate()) {
                updateProfile()
            }
        }
        setEditText(binding.etFullName, binding.tilFullName)
        binding.tvDateOfBirth.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                binding.tvDateOfBirth.setBackgroundResource(R.drawable.auth_edit_text_drawable)
                binding.tilDateOfBirth.error = null
                binding.tilDateOfBirth.isErrorEnabled = false
            }

        })
    }

    private fun selectImageWithCrop() {
        CropImage.activity()
            .start(this@EditProfileActivity)
    }

    private fun observeApiResponse() {
        viewModel.getApiResponse().observe(this, {
            when (it.status) {
                ApiStatus.LOADING -> {
                    if (it.tag != ApiTags.GET_INSIGHT_CATEGORIES)
                        Loader.showLoader((this)) {
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
                        ApiTags.UPDATE_PROFILE -> {
                            val model = Gson().fromJson(it.data.toString(), TodayTasksModel::class.java)
                            AppUtils.showToast(this, model.message, true)
                            Handler(Looper.getMainLooper()).postDelayed({
                                finish()
                            }, 2000)

                        }
                    }
                }
            }
        })
    }

    private fun updateProfile() {
        val multipartBody: MultipartBody.Part? = if (!URLUtil.isValidUrl(filePath)) {
            val file = java.io.File(filePath)
            MultipartBody.Part.createFormData(
                "img", file.name, file.asRequestBody("image/*".toMediaTypeOrNull())
            )
        } else
            null

        if (binding.etHeight.text.isEmpty()) {
            AppUtils.showToast(this, "Please select height!", false)
            return
        }
        if (binding.etWeight.text.isEmpty()) {
            AppUtils.showToast(this, "Please select weight!", false)
            return
        }
        if (filePath.isEmpty()) {
            apiCall = retrofitClient.updateProfileData(
                binding.etFullName.text.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                genderType.toRequestBody("text/plain".toMediaTypeOrNull()),
                dob.toRequestBody("text/plain".toMediaTypeOrNull()),
                binding.etHeight.text.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                binding.etWeight.text.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                heightUnit.toRequestBody("text/plain".toMediaTypeOrNull()),
                weightUnit.toRequestBody("text/plain".toMediaTypeOrNull())
            )
        } else {
            apiCall = retrofitClient.updateProfile(
                binding.etFullName.text.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                genderType.toRequestBody("text/plain".toMediaTypeOrNull()),
                dob.toRequestBody("text/plain".toMediaTypeOrNull()),
                binding.etHeight.text.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                binding.etWeight.text.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                heightUnit.toRequestBody("text/plain".toMediaTypeOrNull()),
                weightUnit.toRequestBody("text/plain".toMediaTypeOrNull()),
                multipartBody
            )
        }
        viewModel.updateProfile(apiCall)
    }

    private fun validate(): Boolean {
        if (binding.etFullName.text.toString().trim().isBlank()) {
            binding.etFullName.setBackgroundResource(R.drawable.auth_edit_text_error)
            binding.tilFullName.error = "Please enter your full Name!"
            binding.tilFullName.requestFocus()
            return false
        }

        if (binding.tvDateOfBirth.text.toString().trim().isBlank()) {
            binding.tvDateOfBirth.setBackgroundResource(R.drawable.auth_edit_text_error)
            binding.tilDateOfBirth.error = "Please enter your Date of Birth"
            binding.tvDateOfBirth.requestFocus(R.drawable.auth_edit_text_error)
            return false
        }


        return true
    }

    private fun setEditText(editText: EditText, layout: TextInputLayout) {
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

}