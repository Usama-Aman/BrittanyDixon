package com.cp.brittany.dixon.activities.home.shipping_address

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModelProviders
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.models.ShippingAddress
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.base.BaseActivityResult
import com.cp.brittany.dixon.databinding.ActivityShippingAddressBinding
import com.cp.brittany.dixon.loader.Loader
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.ApiStatus
import com.cp.brittany.dixon.utils.AppUtils
import com.cp.brittany.dixon.utils.viewGone
import com.cp.brittany.dixon.utils.viewVisible
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import java.util.regex.Pattern

class ShippingAddressActivity : BaseActivity() {
    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>
    private lateinit var binding: ActivityShippingAddressBinding
    private var name = ""
    private var streetAddress = ""
    private var postalCode = ""
    private var phoneNumber = ""
    private var city = ""
    private var state = ""
    private var country = ""

    private var selectedCountryId = -1
    private var selectedStateId = -1
    private var selectedCityId = -1
    private var countriesList: MutableList<CountriesData> = ArrayList()
    private var statesList: MutableList<StatesData> = ArrayList()
    private var citiesList: MutableList<CitiesData> = ArrayList()
    private var searchableList: ArrayList<SearchableSpinnerModel> = ArrayList()
    private lateinit var shippingAddress: ShippingAddress
    private var pattern = "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShippingAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()
        retrofitClient = RetrofitClient.getClient(this).create(Api::class.java)

        initViews()
        initVM()
        getIntentData()
        initListeners()
        observeApiResponse()
        getCountries()
    }


    private fun initViews() {
        onSetupViewGroup(binding.content)
    }

    private fun initVM() {
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        binding.lifecycleOwner = this
    }

    private fun getIntentData() {

        if (intent != null) {
            if (intent.hasExtra("address")) {
                shippingAddress = intent.getParcelableExtra("address")!!
                streetAddress = shippingAddress.street_address
                state = shippingAddress.state
                city = shippingAddress.city
                postalCode = shippingAddress.zip_code
                phoneNumber = shippingAddress.phone_number
                country = shippingAddress.country

            }
            else
                binding.btnUpdate.text = "Add"
            name = intent.getStringExtra("name") ?: ""
            binding.etName.setText(name)
            binding.etStreetAddress.setText(streetAddress)
            binding.etState.text = state
            binding.etCity.text = city
            binding.etPostalCode.setText(postalCode)
            binding.etPhoneNumber.setText(phoneNumber)
            binding.etCountry.text = country
        }

    }

    private fun initListeners() {
        binding.ivBack.setOnClickListener {
            setResult(Activity.RESULT_CANCELED, null)
            onBackPressed()
        }
        binding.btnUpdate.setOnClickListener {
            if (validate()) {
                updateShippingAddress()
            }
        }

        setEditText(binding.etName, binding.tilName)
        setEditText(binding.etPostalCode, binding.tilPostalCode)
        setEditText(binding.etStreetAddress, binding.tilStreetAddress)
        setEditText(binding.etPhoneNumber, binding.tilPhoneNumber)

        setTextView(binding.etCountry, binding.tilCountry)
        setTextView(binding.etState, binding.tilState)
        setTextView(binding.etCity, binding.tilCity)

        binding.etCountry.setOnClickListener {
            searchableList.clear()
            countriesList.forEachIndexed { _, countriesData ->
                searchableList.add(
                    SearchableSpinnerModel(
                        countriesData.id,
                        countriesData.name,
                        countriesData.id == selectedCountryId
                    )
                )
            }

            val i = Intent(this, SearchableSpinnerActivity::class.java)
            i.putParcelableArrayListExtra("listData", searchableList)
            i.putExtra("title", "Select Country")
            activityLauncher.launch(
                i,
                object : BaseActivityResult.OnActivityResult<ActivityResult> {
                    override fun onActivityResult(result: ActivityResult) {
                        if (result.resultCode == Activity.RESULT_OK) {
                            selectedCountryId = result.data?.getIntExtra("selectedId", -1) ?: -1
                            binding.etCountry.text = result.data?.getStringExtra("selectedValue")

                            selectedStateId = -1
                            binding.etState.text = ""
                            selectedCityId = -1
                            binding.etCity.text = ""
                            //getStates()
                        }
                    }
                })
        }

        binding.etState.setOnClickListener {
            searchableList.clear()
            statesList.forEachIndexed { _, statesData ->
                if (statesData.country_id == selectedCountryId) {
                    searchableList.add(
                        SearchableSpinnerModel(
                            statesData.id, statesData.name, statesData.id == selectedStateId
                        )
                    )
                }
            }
            if (binding.etCountry.text.toString().isNotBlank()) {
                val i = Intent(applicationContext, SearchableSpinnerActivity::class.java)
                i.putParcelableArrayListExtra("listData", searchableList)
                i.putExtra("title", "Select State")
                activityLauncher.launch(
                    i,
                    object : BaseActivityResult.OnActivityResult<ActivityResult> {
                        override fun onActivityResult(result: ActivityResult) {
                            if (result.resultCode == Activity.RESULT_OK) {
                                selectedStateId =
                                    result.data?.getIntExtra("selectedId", -1) ?: -1
                                binding.etState.text = result.data?.getStringExtra("selectedValue")

                                selectedCityId = -1
                                binding.etCity.text = ""
                                //getCities()
                            }
                        }
                    })
            } else {
                binding.etCountry.setBackgroundResource(R.drawable.auth_edit_text_error)
                binding.tilCountry.viewVisible()
                binding.tilCountry.error = "Select Country first!"
            }
        }

        binding.etCity.setOnClickListener {
            searchableList.clear()
            citiesList.forEachIndexed { _, citiesData ->
                if (citiesData.state_id == selectedStateId) {
                    searchableList.add(
                        SearchableSpinnerModel(
                            citiesData.id, citiesData.name, citiesData.id == selectedCityId
                        )
                    )
                }
            }
            if (binding.etState.text.toString().isNotBlank()) {
                val i = Intent(applicationContext, SearchableSpinnerActivity::class.java)
                i.putParcelableArrayListExtra("listData", searchableList)
                i.putExtra("title", "Select City")
                activityLauncher.launch(
                    i,
                    object : BaseActivityResult.OnActivityResult<ActivityResult> {
                        override fun onActivityResult(result: ActivityResult) {
                            if (result.resultCode == Activity.RESULT_OK) {
                                selectedCityId =
                                    result.data?.getIntExtra("selectedId", -1) ?: -1
                                binding.etCity.text = result.data?.getStringExtra("selectedValue")
                            }
                        }
                    })
            } else {
                binding.etState.setBackgroundResource(R.drawable.auth_edit_text_error)
                binding.tilState.viewVisible()
                binding.tilState.error = "Select State first!"
            }
        }
    }

    private fun validate(): Boolean {
        if (binding.etName.text.toString().trim().isBlank()) {
            binding.etName.setBackgroundResource(R.drawable.auth_edit_text_error)
            binding.tilName.error = "Please enter your Name!"
            binding.tilName.requestFocus()
            return false
        }

        if (binding.etCountry.text.toString().trim().isBlank()) {
            binding.etCountry.setBackgroundResource(R.drawable.auth_edit_text_error)
            binding.tilCountry.error = "Please enter your Country"
            binding.etCountry.requestFocus()
            return false
        }

        if (binding.etState.text.toString().trim().isBlank()) {
            binding.etState.setBackgroundResource(R.drawable.auth_edit_text_error)
            binding.tilState.error = "Please enter Your State"
            binding.etState.requestFocus()
            return false
        }

        if (binding.etCity.text.toString().trim().isBlank()) {
            binding.etCity.setBackgroundResource(R.drawable.auth_edit_text_error)
            binding.tilCity.error = "Please enter Your City"
            binding.etCity.requestFocus()
            return false
        }

        if (binding.etPostalCode.text.toString().trim().isBlank()) {
            binding.etPostalCode.setBackgroundResource(R.drawable.auth_edit_text_error)
            binding.tilPostalCode.error = "Please enter Your Postal code"
            binding.etPostalCode.requestFocus()
            return false
        }

        if (binding.etStreetAddress.text.toString().trim().isBlank()) {
            binding.etStreetAddress.setBackgroundResource(R.drawable.auth_edit_text_error)
            binding.tilStreetAddress.error = "Please enter Your Street Address"
            binding.etStreetAddress.requestFocus()
            return false
        }

        if (binding.etPhoneNumber.text.toString().trim().isBlank()) {
            binding.etPhoneNumber.setBackgroundResource(R.drawable.auth_edit_text_error)
            binding.tilPhoneNumber.error = "Please enter Your Phone Number"
            binding.etPhoneNumber.requestFocus()
            return false
        }
        if (!isPhoneNumber()) {
            binding.etPhoneNumber.setBackgroundResource(R.drawable.auth_edit_text_error)
            binding.tilPhoneNumber.error = "Please enter a valid Phone Number"
            binding.etPhoneNumber.requestFocus()
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

    private fun setTextView(textView: TextView, layout: TextInputLayout) {
        textView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                textView.setBackgroundResource(R.drawable.auth_edit_text_drawable)
                layout.error = null
                layout.viewGone()
                layout.isErrorEnabled = false
            }
        })
    }

    private fun isPhoneNumber(): Boolean {
        val regex = Pattern.compile(pattern)
        val matcher = regex.matcher(binding.etPhoneNumber.text.toString().trim())
        return matcher.find()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeApiResponse() {
        viewModel.getApiResponse().observe(this, {
            when (it.status) {
                ApiStatus.LOADING -> {
                    if (it.tag != ApiTags.GET_INSIGHT_CATEGORIES)
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
                        ApiTags.UPDATE_SHIPPING_ADDRESSES -> {
                            AppUtils.showToast(this, it?.data?.getString("message")!!, true)
                            Handler(Looper.getMainLooper()).postDelayed({
                                finish()
                            }, 1000)
                        }
                        ApiTags.ADD_SHIPPING_ADDRESS -> {
                            AppUtils.showToast(this, it?.data?.getString("message")!!, true)
                            Handler(Looper.getMainLooper()).postDelayed({
                                finish()
                            }, 1000)
                        }
                        ApiTags.GET_COUNTRIES -> {
                            val model =
                                Gson().fromJson(it.data.toString(), CountriesModel::class.java)
                            countriesList.clear()
                            countriesList.addAll(model.data.countries)
                            statesList.clear()
                            statesList.addAll(model.data.states)
                            citiesList.clear()
                            citiesList.addAll(model.data.cities)
                        }
                    }
                }
            }
        })
    }

    private fun updateShippingAddress() {
        if(binding.btnUpdate.text == "Add"){
            apiCall = retrofitClient.addShippingAddress(
                binding.etStreetAddress.text.toString(),
                binding.etCity.text.toString(),
                binding.etState.text.toString(),
                binding.etCountry.text.toString(),
                binding.etPostalCode.text.toString(),
                binding.etPhoneNumber.text.toString(),
                1
            )
            viewModel.addShippingAddress(apiCall)
        }
        else {
            apiCall = retrofitClient.updateShippingAddress(
                shippingAddress.id,
                binding.etStreetAddress.text.toString(),
                binding.etCity.text.toString(),
                binding.etState.text.toString(),
                binding.etCountry.text.toString(),
                binding.etPostalCode.text.toString(),
                binding.etPhoneNumber.text.toString(),
                1
            )
            viewModel.updateShippingAddress(apiCall)
        }
    }

    private fun getCountries() {
        apiCall = retrofitClient.getCountries()
        viewModel.getCountries(apiCall)
    }
}