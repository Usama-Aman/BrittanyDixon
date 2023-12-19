package com.cp.brittany.dixon.activities.home.checkout_shipping

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModelProviders
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.OnAlertOneButtonClickListener
import com.cp.brittany.dixon.activities.home.checkout_payment.CheckoutPaymentActivity
import com.cp.brittany.dixon.activities.home.models.*
import com.cp.brittany.dixon.activities.home.shipping_address.AddNewShippingAddressActivity
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.base.AppController
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.base.BaseActivityResult
import com.cp.brittany.dixon.databinding.ActivityCheckoutShippingBinding
import com.cp.brittany.dixon.loader.Loader
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.ApiStatus
import com.cp.brittany.dixon.utils.AppUtils
import com.cp.brittany.dixon.utils.viewGone
import com.cp.brittany.dixon.utils.viewVisible
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call

class CheckoutShippingActivity : BaseActivity() {

    private lateinit var binding: ActivityCheckoutShippingBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>

    private var shippingAddresses: MutableList<ShippingAddresse> = ArrayList()
    private lateinit var shippingAdapter: ShippingAdapter

    private var discountedAmount = 0
    private var orderAmount = 0
    private var selectedAddressPosition = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutShippingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()
        retrofitClient = RetrofitClient.getClient(this).create(Api::class.java)

        initViews()
        initVM()
        initListeners()
        initAdapters()
        observeApiResponse()

        getUserShippingAddresses()
    }

    @SuppressLint("SetTextI18n")
    private fun initViews() {
        onSetupViewGroup(binding.content)
    }

    private fun initVM() {
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        binding.lifecycleOwner = this
    }

    private fun initListeners() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnContinue.setOnClickListener {
            AppController.cartCheckoutData.shippingAddresses = ShippingAddresse()
            if (shippingAddresses.any { it.is_default == 1 })
                AppController.cartCheckoutData.shippingAddresses = shippingAddresses.filter { it.is_default == 1 }[0]
            val intentData = Intent(this, CheckoutPaymentActivity::class.java)
            intentData.putExtra("orderAmount", orderAmount)
            intentData.putExtra("discountedAmount", discountedAmount)
            startActivity(intentData)
        }

        binding.btnAddNewShippingAddress.setOnClickListener {
            //selectedAddressPosition = -1
            val intent = Intent(this, AddNewShippingAddressActivity::class.java)
            intent.putExtra("fromEdit", false)
            activityLauncher.launch(intent, object : BaseActivityResult.OnActivityResult<ActivityResult> {
                override fun onActivityResult(result: ActivityResult) {
                    if (result.data != null) {
                        setShippingData(result.data!!)
                    }
                }
            })
        }

    }

    private fun initAdapters() {
        shippingAdapter = ShippingAdapter(shippingAddresses, object : ShippingAdapter.ShippingAdapterInterface {
            override fun onItemEditClicked(position: Int) {
                selectedAddressPosition = position
                val intent = Intent(this@CheckoutShippingActivity, AddNewShippingAddressActivity::class.java)
                intent.putExtra("fromEdit", true)
                intent.putExtra("name", "")
                intent.putExtra("street", shippingAddresses[position].street_address)
                intent.putExtra("state", shippingAddresses[position].state)
                intent.putExtra("city", shippingAddresses[position].city)
                intent.putExtra("postalCode", shippingAddresses[position].zip_code)
                intent.putExtra("country", shippingAddresses[position].country)
                intent.putExtra("phone", shippingAddresses[position].phone_number)
                activityLauncher.launch(intent, object : BaseActivityResult.OnActivityResult<ActivityResult> {
                    override fun onActivityResult(result: ActivityResult) {
                        if (result.data != null) {
                            setShippingData(result.data!!)
                        }
                    }
                })
            }

            override fun onItemDeleteClicked(position: Int) {
                showDialog("Confirmation","Do you want to delete this address",false, object : OnAlertOneButtonClickListener {
                    override fun okClickListener() {
                        deleteShippingAddress(shippingAddresses[position].id)
                    }

                    override fun cancelClickListener() {
                    }

                })
            }

            override fun makeCardDefault(position: Int) {
                if(shippingAddresses[position].is_default !=1) {
                    showDialog("Confirmation", "Do you want to make it default address", false, object : OnAlertOneButtonClickListener {
                        override fun okClickListener() {
                            if (selectedAddressPosition != -1) {
                                shippingAddresses[selectedAddressPosition].is_selected = false
                                /*addShippingAddress(
                                true, "", shippingAddresses[selectedAddressPosition].street_address,
                                shippingAddresses[selectedAddressPosition].state, shippingAddresses[selectedAddressPosition].city,
                                shippingAddresses[selectedAddressPosition].zip_code, shippingAddresses[selectedAddressPosition].phone_number,
                                shippingAddresses[selectedAddressPosition].country,1
                            )*/
                            }
                            shippingAddresses[position].is_selected = true
                            selectedAddressPosition = position
                            makeShippingAddressDefault(shippingAddresses[position].id)
                            shippingAdapter.notifyDataSetChanged()
                        }

                        override fun cancelClickListener() {
                        }

                    })
                }


            }
        })
        binding.rvShipping.adapter = shippingAdapter
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
                        ApiTags.GET_SHIPPING_ADDRESSES -> {
                            val model = Gson().fromJson(it.data.toString(), ShippingAddressModel::class.java)
                            shippingAddresses.clear()
                            shippingAddresses.addAll(model.data.shipping_addresses)

                            if (!shippingAddresses.isNullOrEmpty()) {
                                shippingAddresses.forEachIndexed { index, item ->
                                    if (item.is_default == 1) {
                                        item.is_selected = true
                                        selectedAddressPosition = index
                                    }
                                }
                                binding.rvShipping.viewVisible()
                                binding.noData.viewGone()
                            } else {
                                binding.rvShipping.viewGone()
                                binding.noData.viewVisible()
                            }
                            binding.btnAddNewShippingAddress.viewVisible()
                            shippingAdapter.notifyDataSetChanged()
                        }
                        ApiTags.ADD_SHIPPING_ADDRESS -> {
                            val model = Gson().fromJson(it.data.toString(), AddShippingAddressModel::class.java)
                            AppUtils.showToast(this, model.message, true)

                            Handler(Looper.getMainLooper()).postDelayed({
                                selectedAddressPosition = -1
                                getUserShippingAddresses()
                            }, 500)
                        }
                        ApiTags.Make_SHIPPING_ADDRESSES_Default -> {
                            val model = Gson().fromJson(it.data.toString(), AddShippingAddressModel::class.java)
                            for (item in shippingAddresses) {
                                if (item.is_default == 1) {
                                    item.is_default = 0
                                }
                            }
                            if (selectedAddressPosition != -1 && model.data.is_default == 1) {
                                shippingAddresses[selectedAddressPosition].is_default = 1
                            }
                            shippingAdapter.notifyDataSetChanged()
                        }
                        ApiTags.DELETE_SHIPPING_ADDRESS -> {
                            AppUtils.showToast(this, it?.data?.getString("message")!!, true)

                            Handler(Looper.getMainLooper()).postDelayed({
                                selectedAddressPosition = -1
                                getUserShippingAddresses()
                            }, 500)
                        }
                    }
                }
            }
        })
    }

    private fun getUserShippingAddresses() {
        apiCall = retrofitClient.getUserShippingAddresses()
        viewModel.getUserShippingAddresses(apiCall)
    }

    private fun setShippingData(intent: Intent, isDefault: Int = 0) {
        addShippingAddress(
            intent.getBooleanExtra("fromEdit", false) ?: false,
            intent.getStringExtra("name") ?: "",
            intent.getStringExtra("street") ?: "",
            intent.getStringExtra("state") ?: "",
            intent.getStringExtra("city") ?: "",
            intent.getStringExtra("postalCode") ?: "",
            intent.getStringExtra("phone") ?: "",
            intent.getStringExtra("country") ?: "",
            isDefault
        )
    }

    private fun addShippingAddress(
        fromEdit: Boolean, name: String, street: String,
        state: String, city: String, postalCode: String, phone: String, country: String, isDefault: Int
    ) {
        apiCall = if (!fromEdit)
            retrofitClient.addShippingAddress(street, city, state, country, postalCode, phone, isDefault)
        else
            retrofitClient.updateShippingAddress(
                shippingAddresses[selectedAddressPosition].id, street, city,
                state, country, postalCode, phone, isDefault
            )/*retrofitClient.updateShippingAddress(
                shippingAddresses[selectedAddressPosition].id, name, street,
                state, city, postalCode, phone, isDefault
            )*/
        viewModel.addShippingAddress(apiCall)
    }

    private fun deleteShippingAddress(id: Int) {
        apiCall = retrofitClient.deleteShippingAddress(id)
        viewModel.deleteShippingAddress(apiCall)
    }

    private fun makeShippingAddressDefault(id: Int) {
        apiCall = retrofitClient.makeShippingAddressDefault(id)
        viewModel.makeShippingAddressDefault(apiCall)
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        binding.tvOrderAmount.text = "$${AppController.cartCheckoutData.total_price}"
        binding.tvDiscountAmount.text = "-$${AppController.cartCheckoutData.discountedPrice}"
    }

}