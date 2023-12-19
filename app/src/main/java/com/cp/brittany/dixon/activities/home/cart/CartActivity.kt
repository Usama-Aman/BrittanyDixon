package com.cp.brittany.dixon.activities.home.cart

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.checkout_shipping.CheckoutShippingActivity
import com.cp.brittany.dixon.activities.home.models.ApplyPromoModel
import com.cp.brittany.dixon.activities.home.models.CartData
import com.cp.brittany.dixon.activities.home.models.CartItem
import com.cp.brittany.dixon.activities.home.models.CartModel
import com.cp.brittany.dixon.activities.home.product_page.ProductPageActivity
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.base.AppController
import com.cp.brittany.dixon.base.AppController.Companion.cartCheckoutData
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivityCartBinding
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
import org.json.JSONObject
import retrofit2.Call

class CartActivity : BaseActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var cartAdapter: CartAdapter

    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>

    private lateinit var cartData: CartData
    private var cartItems: MutableList<CartItem> = ArrayList()

    private var selectedCartItem = -1
    private var discountedAmount = 0
    private var isGuest = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        retrofitClient = RetrofitClient.getClient(this).create(Api::class.java)

        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()
        val data = AppUtils.getUserDetails(applicationContext)
        isGuest = data.is_guest
        initVM()
        initListeners()
        initAdapters()
        observeApiResponse()
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

        binding.btnCheckout.setOnClickListener {
            if(isGuest == 0) {
                cartCheckoutData.items.clear()
                cartCheckoutData.items.addAll(cartItems)
                cartCheckoutData.total_price = cartData.total_price

                val intent = Intent(this, CheckoutShippingActivity::class.java)
                startActivity(intent)
            }
            else{
                showLoginDialog(true)
            }
        }

        binding.btnApply.setOnClickListener {
            applyPromo()
        }
    }

    private fun initAdapters() {
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
                val intent = Intent(this@CartActivity, ProductPageActivity::class.java)
                intent.putExtra("fromEdit", true)
                intent.putExtra("productId", cartItems[position].product_id)
                intent.putExtra("quantity", cartItems[position].quantity.toInt())
                intent.putExtra("cartId", cartItems[position].cart_id)
                intent.putExtra("colorId", cartItems[position].color.color_id)
                intent.putExtra("sizeId", cartItems[position].size.size_id)
                startActivity(intent)
            }

            override fun onRemove(position: Int) {
                removeCartItem(cartItems[position].cart_id)
            }
        })
        binding.rvCart.adapter = cartAdapter
    }

    @SuppressLint("SetTextI18n")
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
                    AppUtils.showToast(this, it.message!!, false)
                }
                ApiStatus.SUCCESS -> {
                    when (it.tag) {
                        ApiTags.GET_CART_ITEMS -> {
                            Loader.hideLoader()
                            val model = Gson().fromJson(it.data.toString(), CartModel::class.java)
                            cartData = model.data

                            AppController.cartCount = cartData.items.size
                            setUpData()

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
                        ApiTags.REMOVE_CART_ITEM -> {
                            val json = JSONObject(it.data.toString())
                            AppUtils.showToast(this, json.getString("message"), true)
                            getCartItems()
                        }
                        ApiTags.APPLY_PROMO_CODE -> {
                            Loader.hideLoader()
                            val model = Gson().fromJson(it.data.toString(), ApplyPromoModel::class.java)
                            if (model.data != null) {

                                discountedAmount = model.data.amount.toInt()
                                cartCheckoutData.discountedPrice = discountedAmount
                                binding.tvDiscountAmount.text = cartCheckoutData.discountedPrice.toString()
                                val p = cartData.total_price - cartCheckoutData.discountedPrice
                                binding.tvTotalPrice.text = "$$p"
                            }
                        }
                    }
                }
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun setUpData() {

        if (!cartData.items.isNullOrEmpty()) {
            cartItems.clear()

            binding.cartConstraint.viewVisible()
            binding.noData.viewGone()

            binding.tvTitle.text = "Shopping Cart (${cartData.items.size})"
            cartItems.addAll(cartData.items)
            cartAdapter.notifyDataSetChanged()

            binding.tvSubtotalPrice.text = "$${cartData.total_price}"
            binding.tvShippingPrice.text = cartData.shipping_fee
            binding.tvTaxesPrice.text = cartData.tax

            binding.tvTotalPrice.text = "$${cartData.total_price}"

            if (cartCheckoutData.discountedPrice > 0) {
                if (cartData.total_price < 500) {
                    AppUtils.showToast(
                        this@CartActivity,
                        "Order minimum amount should be 500 to redeem this promo code ",
                        false
                    )
                    cartCheckoutData.discountedPrice = 0
                }
                val p = cartData.total_price - cartCheckoutData.discountedPrice
                binding.tvTotalPrice.text = "$$p"
                binding.tvDiscountAmount.text = cartCheckoutData.discountedPrice.toString()
            }
        } else {
            binding.cartConstraint.viewGone()
            binding.noData.viewVisible()
            binding.tvTitle.text = "Shopping Cart"
        }

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

    private fun removeCartItem(cartId: Int) {
        apiCall = retrofitClient.removeCartItem(cartId)
        viewModel.removeCartItem(apiCall)
    }

    private fun applyPromo() {
        if (binding.etDiscountCode.text.toString().isBlank()) {
            AppUtils.showToast(this, "Please add Discount Code first!", false)
            return
        }

        apiCall = retrofitClient.applyPromo(binding.etDiscountCode.text.toString())
        viewModel.applyPromo(apiCall)
    }

    override fun onResume() {
        super.onResume()
        getCartItems()
    }

}