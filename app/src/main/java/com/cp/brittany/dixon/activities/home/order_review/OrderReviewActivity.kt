package com.cp.brittany.dixon.activities.home.order_review

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModelProviders
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.OnAlertOneButtonClickListener
import com.cp.brittany.dixon.activities.home.HomeActivity
import com.cp.brittany.dixon.activities.home.cart.CartAdapter
import com.cp.brittany.dixon.activities.home.cart.CartCheckoutModel
import com.cp.brittany.dixon.activities.home.models.CartItem
import com.cp.brittany.dixon.activities.home.models.CartModel
import com.cp.brittany.dixon.activities.home.order_done.OrderDoneActivity
import com.cp.brittany.dixon.activities.home.product_page.ProductPageActivity
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.base.AppController
import com.cp.brittany.dixon.base.AppController.Companion.cartCheckoutData
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.base.BaseActivityResult
import com.cp.brittany.dixon.databinding.ActivityOrderReviewBinding
import com.cp.brittany.dixon.loader.Loader
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.ApiStatus
import com.cp.brittany.dixon.utils.AppUtils
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call

class OrderReviewActivity : BaseActivity() {
    private lateinit var binding: ActivityOrderReviewBinding

    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>

    private lateinit var cartAdapter: CartAdapter
    private var cartItems: MutableList<CartItem> = ArrayList()
    private var selectedCartItem = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()
        retrofitClient = RetrofitClient.getClient(this).create(Api::class.java)

        initViews()
        initVM()
        initListeners()
        observeApiResponse()
    }

    @SuppressLint("SetTextI18n")
    private fun initViews() {
        initCartAdapter()
        binding.tvShippingAddress.text = cartCheckoutData.shippingAddresses.street_address
        binding.tvPaymentMethod.text = "${cartCheckoutData.defaultCard.card_type} **** **** **** ${cartCheckoutData.defaultCard.last4}"
        binding.tvOrderAmount.text = "$${cartCheckoutData.total_price}"
        binding.tvDiscountAmount.text = "-$${cartCheckoutData.discountedPrice}"
    }

    private fun initVM() {
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        binding.lifecycleOwner = this
    }

    private fun initCartAdapter() {
        cartItems.addAll(cartCheckoutData.items)
        cartAdapter = CartAdapter(cartItems, object : CartAdapter.CartItemInterface {
            override fun onPlus(position: Int) {
                selectedCartItem = position
                increaseQuantity(cartItems[position].cart_id)
            }

            override fun onMinus(position: Int) {
                selectedCartItem = position
                decreaseQuantity(cartItems[position].cart_id)
            }

            override fun onEdit(position: Int) {
                val intent = Intent(this@OrderReviewActivity, ProductPageActivity::class.java)
                intent.putExtra("fromEdit", true)
                intent.putExtra("productId", cartItems[position].product_id)
                intent.putExtra("quantity", cartItems[position].quantity.toInt())
                intent.putExtra("cartId", cartItems[position].cart_id)
                intent.putExtra("colorId", cartItems[position].color.color_id)
                intent.putExtra("sizeId", cartItems[position].size.size_id)
                activityLauncher.launch(intent, object : BaseActivityResult.OnActivityResult<ActivityResult> {
                    override fun onActivityResult(result: ActivityResult) {
                        getCartItems()
                    }
                })
            }

            override fun onRemove(position: Int) {
                removeCartItem(cartItems[position].cart_id)
            }
        })
        binding.rvCartItems.adapter = cartAdapter
    }

    private fun initListeners() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.btnContinue.setOnClickListener {
            finish()
            startActivity(Intent(this, OrderDoneActivity::class.java))
        }

        binding.btnEditShippingAddress.setOnClickListener {
            AppController.gotoEditAddress = true
            finish()
        }

        binding.btnEditPaymentMethod.setOnClickListener {
            AppController.gotoEditCard = true
            finish()
        }

        binding.btnContinue.setOnClickListener {
            placeOrder()
        }
    }


    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun observeApiResponse() {
        viewModel.getApiResponse().observe(this, {
            when (it.status) {
                ApiStatus.LOADING -> {
                    if (it.tag != ApiTags.INCREASE_QUANTITY || it.tag != ApiTags.DECREASE_QUANTITY || it.tag != ApiTags.REMOVE_CART_ITEM)
                        Loader.showLoader(this) {
                            if (this::apiCall.isInitialized)
                                apiCall.cancel()
                        }
                }
                ApiStatus.ERROR -> {
                    Loader.hideLoader()
                    if (it.tag == ApiTags.PLACE_ORDER) {
                        showDialog("Available Stock Details", it.message.toString(), true, object : OnAlertOneButtonClickListener {
                            override fun okClickListener() {

                            }

                            override fun cancelClickListener() {

                            }

                        })
                    } else
                        AppUtils.showToast(this, it.message!!, false)
                }
                ApiStatus.SUCCESS -> {
                    Loader.hideLoader()
                    when (it.tag) {
                        ApiTags.GET_CART_ITEMS -> {
                            val model = Gson().fromJson(it.data.toString(), CartModel::class.java)
                            cartItems.clear()
                            cartItems.addAll(model.data.items)

                            AppController.cartCount = cartItems.size
                            cartCheckoutData.items.clear()
                            cartCheckoutData.items.addAll(cartItems)

                            if (cartCheckoutData.discountedPrice > 0) {
                                if (model.data.total_price < 500) {
                                    AppUtils.showToast(
                                        this@OrderReviewActivity,
                                        "Order minimum amount should be 500 to redeem this promo code ",
                                        false
                                    )
                                    cartCheckoutData.discountedPrice = 0
                                }
                            }

                            cartCheckoutData.total_price = model.data.total_price
                            binding.tvOrderAmount.text = "$${cartCheckoutData.total_price}"
                            binding.tvDiscountAmount.text = "-$${cartCheckoutData.discountedPrice}"

                            if (!cartItems.isNullOrEmpty()) {
                                cartCheckoutData = CartCheckoutModel()
                                cartAdapter.notifyDataSetChanged()
                            } else {
                                AppUtils.showToast(this@OrderReviewActivity, "Cart Empty", true)
                                Handler(Looper.getMainLooper()).postDelayed({
                                    val intent = Intent(this@OrderReviewActivity, HomeActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    finish()
                                }, 2000)
                            }
                        }
                        ApiTags.REMOVE_CART_ITEM -> {
//                            val json = JSONObject(it.data.toString())
//                            AppUtils.showToast(this, json.getString("message"), true)
                            getCartItems()
                        }
                        ApiTags.INCREASE_QUANTITY -> {
                            cartItems[selectedCartItem].quantity = (cartItems[selectedCartItem].quantity.toInt() + 1).toString()
                            getCartItems()
                        }
                        ApiTags.DECREASE_QUANTITY -> {
                            if (cartItems[selectedCartItem].quantity != "1")
                                cartItems[selectedCartItem].quantity = (cartItems[selectedCartItem].quantity.toInt() - 1).toString()
                            getCartItems()
                        }
                        ApiTags.PLACE_ORDER -> {
                            startActivity(Intent(this@OrderReviewActivity, OrderDoneActivity::class.java))
                        }
                    }
                }
            }
        })
    }

    private fun removeCartItem(cartId: Int) {
        apiCall = retrofitClient.removeCartItem(cartId)
        viewModel.removeCartItem(apiCall)
    }

    private fun getCartItems() {
        apiCall = retrofitClient.getCartItems()
        viewModel.getCartItems(apiCall)
    }

    private fun increaseQuantity(cartId: Int) {
        apiCall = retrofitClient.increaseQuantity(cartId)
        viewModel.increaseQuantity(apiCall)
    }

    private fun decreaseQuantity(cartId: Int) {
        apiCall = retrofitClient.decreaseQuantity(cartId)
        viewModel.decreaseQuantity(apiCall)
    }

    private fun placeOrder() {
        apiCall = retrofitClient.placeOrder(cartCheckoutData.shippingAddresses.id, cartCheckoutData.defaultCard.card_id)
        viewModel.placeOrder(apiCall)
    }
}