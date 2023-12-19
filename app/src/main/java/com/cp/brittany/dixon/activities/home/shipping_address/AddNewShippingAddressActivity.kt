package com.cp.brittany.dixon.activities.home.shipping_address

import android.app.Activity
import android.content.Intent
import android.os.Bundle

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModelProviders
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.base.BaseActivityResult
import com.cp.brittany.dixon.databinding.ActivityAddNewShippingAddressBinding
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

class AddNewShippingAddressActivity : BaseActivity() {

    private lateinit var binding: ActivityAddNewShippingAddressBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>

    private var fromEdit = false
    private var name = ""
    private var streetAddress = ""
    private var postalCode = ""
    private var phoneNumber = ""
    private var city = ""
    private var state = ""
    private var country = ""
    private var pattern = "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$"

    private var selectedCountryId = -1
    private var selectedStateId = -1
    private var selectedCityId = -1
    private var countriesList: MutableList<CountriesData> = ArrayList()
    private var statesList: MutableList<StatesData> = ArrayList()
    private var citiesList: MutableList<CitiesData> = ArrayList()
    private var searchableList: ArrayList<SearchableSpinnerModel> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewShippingAddressBinding.inflate(layoutInflater)

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
        fromEdit = intent?.getBooleanExtra("fromEdit", false) ?: false
        name = intent?.getStringExtra("name") ?: ""
        streetAddress = intent?.getStringExtra("street") ?: ""
        state = intent?.getStringExtra("state") ?: ""
        city = intent?.getStringExtra("city") ?: ""
        postalCode = intent?.getStringExtra("postalCode") ?: ""
        phoneNumber = intent?.getStringExtra("phone") ?: ""
        country = intent?.getStringExtra("country") ?: ""
        if (fromEdit) {
            binding.etName.setText(name)
            binding.etStreetAddress.setText(streetAddress)
            binding.etState.text = state
            binding.etCity.text = city
            binding.etPostalCode.setText(postalCode)
            binding.etPhoneNumber.setText(phoneNumber)
            binding.etCountry.text = country
        }
        if (fromEdit) {
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
        binding.btnContinue.setOnClickListener {
            if (validate()) {
                val intentResult = Intent()
                intentResult.putExtra("fromEdit", fromEdit)
                intentResult.putExtra("name", binding.etName.text.toString())
                intentResult.putExtra("street", binding.etStreetAddress.text.toString())
                intentResult.putExtra("state", binding.etState.text.toString())
                intentResult.putExtra("city", binding.etCity.text.toString())
                intentResult.putExtra("postalCode", binding.etPostalCode.text.toString())
                intentResult.putExtra("phone", binding.etPhoneNumber.text.toString())
                intentResult.putExtra("country", binding.etCountry.text.toString())
                setResult(Activity.RESULT_OK, intentResult)
                finish()
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
            binding.tilCountry.viewVisible()
            binding.tilCountry.error = "Please enter your Country"
            binding.etCountry.requestFocus()
            return false
        }

        if (binding.etState.text.toString().trim().isBlank()) {
            binding.etState.setBackgroundResource(R.drawable.auth_edit_text_error)
            binding.tilState.viewVisible()
            binding.tilState.error = "Please enter Your State"
            binding.etState.requestFocus()
            return false
        }

        if (binding.etCity.text.toString().trim().isBlank()) {
            binding.etCity.setBackgroundResource(R.drawable.auth_edit_text_error)
            binding.tilCity.viewVisible()
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

    private fun getCountries() {
        apiCall = retrofitClient.getCountries()
        viewModel.getCountries(apiCall)
    }

    private fun observeApiResponse() {
        viewModel.getApiResponse().observe(this, {
            when (it.status) {
                ApiStatus.LOADING -> {
                    Loader.showLoader(this) {
                        if (this@AddNewShippingAddressActivity::apiCall.isInitialized)
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
                        ApiTags.GET_STATES -> {
                            val model = Gson().fromJson(it.data.toString(), CountriesModel::class.java)
                            statesList.clear()
                            statesList.addAll(model.data.states)
                        }
                        ApiTags.GET_CITIES -> {
                            val model = Gson().fromJson(it.data.toString(), CountriesModel::class.java)
                            citiesList.clear()
                            citiesList.addAll(model.data.cities)
                        }

                    }
                }
            }
        })
    }
}