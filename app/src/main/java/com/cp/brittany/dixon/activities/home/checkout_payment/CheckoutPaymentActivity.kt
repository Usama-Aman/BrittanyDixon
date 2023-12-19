package com.cp.brittany.dixon.activities.home.checkout_payment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModelProviders
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.OnAlertOneButtonClickListener
import com.cp.brittany.dixon.activities.home.models.*
import com.cp.brittany.dixon.activities.home.order_review.OrderReviewActivity
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.base.AppController
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.base.BaseActivityResult
import com.cp.brittany.dixon.databinding.ActivityCheckoutPaymentBinding
import com.cp.brittany.dixon.loader.Loader
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.ApiStatus
import com.cp.brittany.dixon.utils.AppUtils
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call

class CheckoutPaymentActivity : BaseActivity() {
    private lateinit var binding: ActivityCheckoutPaymentBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>

    private var cardList: MutableList<CardData> = ArrayList()
    private lateinit var paymentAdapter: PaymentAdapter

    private var discountedAmount = 0
    private var orderAmount = 0
    private var selectedCardPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()
        retrofitClient = RetrofitClient.getClient(this).create(Api::class.java)

        initViews()
        initVM()
        initListeners()
        initAdapter()
        observeApiResponse()

        getUserCards()
    }


    private fun initAdapter() {
        paymentAdapter = PaymentAdapter(cardList, object : PaymentAdapter.PaymentAdapterInterface {

            override fun onItemDeleteClicked(position: Int) {
                showDialog("Confirmation","Do you want to delete this card",false, object : OnAlertOneButtonClickListener {
                    override fun okClickListener() {
                        deleteCard(cardList[position].card_id)
                    }

                    override fun cancelClickListener() {
                    }
                })
            }

            override fun makeCardDefaultClicked(position: Int) {
                if (cardList[position].is_default != 1) {
                    showDialog("Confirmation", "Do you want to make it default card", false, object : OnAlertOneButtonClickListener {
                        override fun okClickListener() {
                            if (selectedCardPosition != -1) {
                                cardList[selectedCardPosition].is_selected = false
                            }
                            cardList[position].is_selected = true
                            selectedCardPosition = position
                            setDefaultCard(cardList[position].card_id)
                        }

                        override fun cancelClickListener() {
                        }

                    })
                }
            }
        })
        binding.rvPayment.adapter = paymentAdapter


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
        binding.btnAddCard.setOnClickListener {

            val intent = Intent(this, AddNewCardActivity::class.java)
            activityLauncher.launch(intent, object : BaseActivityResult.OnActivityResult<ActivityResult> {
                override fun onActivityResult(result: ActivityResult) {
                    if (result.resultCode == Activity.RESULT_OK) {
                        if (result.data != null) {
                            addCard(result.data?.getStringExtra("cardToken") ?: "")
                        }
                    }
                }
            })
        }

        binding.btnContinue.setOnClickListener {
            AppController.cartCheckoutData.defaultCard = CardData()
            if (cardList.any { it.is_default == 1 })
                AppController.cartCheckoutData.defaultCard = cardList.filter { it.is_default == 1 }[0]

            val intentData = Intent(this, OrderReviewActivity::class.java)
            startActivity(intentData)
        }
    }

    private fun addCard(cardToken: String) {
        apiCall = retrofitClient.addUserCard(cardToken)
        viewModel.addUserCard(apiCall)
    }

    private fun deleteCard(cardId: Int) {
        apiCall = retrofitClient.deleteUserCard(cardId)
        viewModel.deletePaymentCard(apiCall)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeApiResponse() {
        viewModel.getApiResponse().observe(this) {
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
                        ApiTags.GET_PAYMENT_CARDS -> {
                            val model = Gson().fromJson(it.data.toString(), UserCardModel::class.java)
                            cardList.clear()
                            cardList.addAll(model.data)
                            cardList.forEachIndexed { index, item ->
                                if (item.is_default == 1) {
                                    item.is_selected = true
                                    selectedCardPosition = index
                                }
                            }

                            paymentAdapter.notifyDataSetChanged()
                        }
                        ApiTags.DELETE_PAYMENT_CARD -> {
                            AppUtils.showToast(this, it?.data?.getString("message")!!, true)

                            Handler(Looper.getMainLooper()).postDelayed({
                                getUserCards()
                            }, 500)
                        }
                        ApiTags.Make_CARD_Default -> {
                            val model = Gson().fromJson(it.data.toString(), DefaultCardModel::class.java)
                            for (item in cardList) {
                                if (item.is_default == 1) {
                                    item.is_default = 0
                                }
                            }
                            if (selectedCardPosition != -1 && model.data.is_default == 1) {
                                cardList[selectedCardPosition].is_default = 1
                            }
                            paymentAdapter.notifyDataSetChanged()
                        }
                        ApiTags.ADD_PAYMENT_CARD -> {
                            AppUtils.showToast(this, it?.data?.getString("message")!!, true)

                            Handler(Looper.getMainLooper()).postDelayed({
                                getUserCards()
                            }, 500)
                        }
                    }
                }
            }
        }
    }

    private fun getUserCards() {
        apiCall = retrofitClient.getUserCards()
        viewModel.getPaymentCards(apiCall)
    }

    private fun setDefaultCard(cardId: Int) {
        apiCall = retrofitClient.makeCardDefault(cardId)
        viewModel.makeCardDefault(apiCall)
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        if (AppController.gotoEditCard)
            AppController.gotoEditCard = false

        if (AppController.gotoEditAddress) {
            AppController.gotoEditAddress = false
            finish()
        }

        binding.tvOrderAmount.text = "$${AppController.cartCheckoutData.total_price}"
        binding.tvDiscountAmount.text = "-$${AppController.cartCheckoutData.discountedPrice}"

    }

}