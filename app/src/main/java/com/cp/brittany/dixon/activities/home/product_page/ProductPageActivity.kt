package com.cp.brittany.dixon.activities.home.product_page

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.cart.CartActivity
import com.cp.brittany.dixon.activities.home.models.*
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.base.AppController
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivityProductPageBinding
import com.cp.brittany.dixon.databinding.ItemProductImagesBinding
import com.cp.brittany.dixon.loader.Loader
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.ApiStatus
import com.cp.brittany.dixon.utils.AppUtils
import com.bumptech.glide.Glide
import com.cp.brittany.dixon.activities.home.profile_tab.adapters.TYPE_SHOP
import com.cp.brittany.dixon.utils.viewGone
import com.cp.brittany.dixon.utils.viewVisible
import com.faltenreich.skeletonlayout.Skeleton
import com.google.gson.Gson
import com.werb.eventbus.EventBus
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call


class ProductPageActivity : BaseActivity() {

    private lateinit var binding: ActivityProductPageBinding
    private var mAdapter: ViewsSliderAdapter? = null

    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>

    private lateinit var colorsAdapter: ProductColorsAdapter
    private lateinit var sizeAdapter: ProductSizeAdapter

    private lateinit var productDetail: ProductDetailData
    private var colorsList: MutableList<Color> = ArrayList()
    private var sizeList: MutableList<Size> = ArrayList()
    private var imagesList: MutableList<File> = ArrayList()

    private var quantityCount = 1
    private var selectedColorPosition = -1
    private var selectedSizePosition = -1
    private var isGuest = 0

    private var fromEdit = false
    private var productId = 1
    private var editCartId = 1
    private var editColorId = -1
    private var editSizeId = -1
    private var img = ""
    private lateinit var skeletonLayout: Skeleton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        retrofitClient = RetrofitClient.getClient(this).create(Api::class.java)

        postponeEnterTransition()
        initViews()
        initVM()
        initListeners()
        initAdapters()
        observeApiResponse()

        getProductDetail(productId)

    }

    @SuppressLint("SetTextI18n")
    private fun initViews() {
        binding.tvCartCount.text = AppController.cartCount.toString()
        skeletonLayout = binding.skeletonLayout
        val data = AppUtils.getUserDetails(applicationContext)
        isGuest = data.is_guest
        productId = intent?.getIntExtra("productId", -1) ?: -1
        fromEdit = intent?.getBooleanExtra("fromEdit", false) ?: false
        if (fromEdit) {
            quantityCount = intent?.getIntExtra("quantity", -1) ?: -1
            editCartId = intent?.getIntExtra("cartId", -1) ?: -1
            editColorId = intent?.getIntExtra("colorId", -1) ?: -1
            editSizeId = intent?.getIntExtra("sizeId", -1) ?: -1

            binding.tvCount.text = quantityCount.toString()
            binding.tvCartButtonText.text = "Update Cart"
        } else {
            binding.tvCartButtonText.text = "Add to Cart"
        }

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
        binding.ivCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
        binding.btnPlus.setOnClickListener {
            quantityCount++
            binding.tvCount.text = quantityCount.toString()
        }
        binding.btnMinus.setOnClickListener {
            if (quantityCount > 1) {
                quantityCount--
                binding.tvCount.text = quantityCount.toString()
            }
        }
        binding.btnAddToCart.setOnClickListener {
            addProductToCart()
        }
        binding.ivHeart.setOnClickListener {
            if (isGuest == 0)
                addToWishList()
            else
                showLoginDialog()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initAdapters() {
        mAdapter = ViewsSliderAdapter(imagesList)
        binding.viewPager.adapter = mAdapter

        sizeAdapter = ProductSizeAdapter(sizeList, object : ProductSizeAdapter.ProductSizeInterface {
            override fun onSizeSelected(position: Int) {
                sizeList.filter { it.isSelected }.forEach { it.isSelected = false }
                sizeList[position].isSelected = true
                sizeAdapter.notifyDataSetChanged()
                colorsList.clear()
                colorsList.addAll(sizeList[position].colors)
                colorsAdapter.notifyDataSetChanged()
            }
        })
        binding.rvSize.adapter = sizeAdapter


        colorsAdapter = ProductColorsAdapter(colorsList, object : ProductColorsAdapter.ProductColorInterface {
            override fun onColorSelected(position: Int) {
                colorsList.filter { it.isSelected }.forEach { it.isSelected = false }
                colorsList[position].isSelected = true
                colorsAdapter.notifyDataSetChanged()
            }
        })
        binding.rvColors.adapter = colorsAdapter
    }

    private fun addToWishList() {
        apiCall = retrofitClient.bookmarkThings("products", productId)
        viewModel.bookmarkThings(apiCall)
    }

    private fun observeApiResponse() {
        viewModel.getApiResponse().observe(this, {
            when (it.status) {
                ApiStatus.LOADING -> {
//                    if (it.tag != ApiTags.GET_CART_ITEMS)
//                        Loader.showLoader(this) {
//                            if (this::apiCall.isInitialized)
//                                apiCall.cancel()
//                        }
                }
                ApiStatus.ERROR -> {
                    //Loader.hideLoader()
                    AppUtils.showToast(this, it.message!!, false)
                }
                ApiStatus.SUCCESS -> {
                    when (it.tag) {
                        ApiTags.GET_PRODUCT_BY_ID -> {
                            //Loader.hideLoader()
                            val model = Gson().fromJson(it.data.toString(), ProductDetailModel::class.java)
                            productDetail = model.data
                            setupData()
                        }
                        ApiTags.ADD_PRODUCT_TO_CART -> {
                            if (!fromEdit) {
                                val model = Gson().fromJson(it.data.toString(), ProductDetailModel::class.java)
                                AppUtils.showToast(this@ProductPageActivity, model.message, true)
                            } else {
                                val json = JSONObject(it.data.toString())
                                AppUtils.showToast(this, json.getString("message"), true)
                            }

                            getCartItems()
                        }
                        ApiTags.GET_CART_ITEMS -> {
                            Loader.hideLoader()
                            val model = Gson().fromJson(it.data.toString(), CartModel::class.java)
                            AppController.cartCount = model.data.items.size
                            binding.tvCartCount.text = AppController.cartCount.toString()
                        }
                        ApiTags.BOOKMARK_THINGS -> {
                            Loader.hideLoader()
                            val model = Gson().fromJson(it.data.toString(), InsightBookMarkModel::class.java)
                            AppUtils.showToast(this@ProductPageActivity, model.message, true)
                            if (model.message == "Bookmark added") {
                                binding.ivHeart.setImageResource(R.drawable.ic_heart_selected)
                            } else {
                                binding.ivHeart.setImageResource(R.drawable.ic_heart)
                            }
                        }
                    }
                }
            }
        })
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun setupData() {
        if (!(this::productDetail).isInitialized || productDetail == null) {
            return
        }
        binding.productConstraint.viewVisible()
        binding.skeletonLayout.viewGone()
        binding.layoutMain.viewVisible()
        skeletonLayout.showOriginal()
        binding.tvTitle.text = productDetail.categories.name
        binding.tvNameOfProduct.text = productDetail.name
        if (productDetail.minimum_price.compare_at_price.toDouble() == 0.0) {
            binding.tvDiscountedPrice.text = "$${productDetail.minimum_price.price}"
            binding.tvActualPrice.viewGone()
        } else {
            binding.tvDiscountedPrice.text = "$${productDetail.minimum_price.price}"
            binding.tvActualPrice.text = "$${productDetail.minimum_price.compare_at_price}"
        }
        binding.rbProduct.rating = productDetail.avg_rating.toFloat() ?: 0.0f
        binding.tvRating.text = String.format("%.1f",(productDetail.avg_rating.toFloat() ?: 0.0))
        binding.tvDetail.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(productDetail.description, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(productDetail.description)
        }
        if (productDetail.files.isEmpty()) {
            productDetail.files.add(File("", productDetail.first_image, 0, productDetail.id))
        }
        imagesList.addAll(productDetail.files)
        binding.dotsIndicator.attachToPager(binding.viewPager)
        mAdapter?.notifyDataSetChanged()


        if (productDetail.is_bookmarked == 1) {
            binding.ivHeart.setImageResource(R.drawable.ic_heart_selected)
        } else {
            binding.ivHeart.setImageResource(R.drawable.ic_heart)
        }
        sizeList.addAll(productDetail.sizes)

        if (fromEdit) {
            if (sizeList.isNotEmpty()) {

                for (i in sizeList.indices)
                    if (editSizeId == sizeList[i].id)
                        colorsList.addAll(sizeList[i].colors)

                sizeList.filter { it.id == editSizeId }.forEach { it.isSelected = true }
                colorsList.filter { it.id == editColorId }.forEach { it.isSelected = true }
            }
        } else {
            if (sizeList.isNotEmpty()) {
                sizeList[0].isSelected = true
                colorsList.addAll(sizeList[0].colors)
                colorsAdapter.notifyDataSetChanged()
            }
        }
        sizeAdapter.notifyDataSetChanged()
        colorsAdapter.notifyDataSetChanged()
        startPostponedEnterTransition()
    }

    private fun getProductDetail(id: Int) {
        binding.skeletonLayout.viewVisible()
        binding.layoutMain.viewGone()
        skeletonLayout.showSkeleton()
        apiCall = retrofitClient.getProductById(id)
        viewModel.getProductById(apiCall)
    }

    private fun addProductToCart() {
        if (sizeList.none { it.isSelected }) {
            AppUtils.showToast(this, "Please select Size!", false)
            return
        }
        if (colorsList.none { it.isSelected }) {
            AppUtils.showToast(this, "Please select Color!", false)
            return
        }

        apiCall = if (!fromEdit)
            retrofitClient.addProductToCart(
                productDetail.id, sizeList.filter { it.isSelected }[0].id,
                colorsList.filter { it.isSelected }[0].id, quantityCount
            )
        else
            retrofitClient.updateCartItem(
                productDetail.id, sizeList.filter { it.isSelected }[0].id,
                colorsList.filter { it.isSelected }[0].id, quantityCount,
                editCartId
            )

        viewModel.addProductToCart(apiCall)
    }

    private fun getCartItems() {
        apiCall = retrofitClient.getCartItems()
        viewModel.getCartItems(apiCall)
    }


    inner class ViewsSliderAdapter(val imagesList: MutableList<File>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private lateinit var mContext: Context

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            mContext = parent.context
            return SliderViewHolder(
                DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_product_images, parent, false)
            )
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is SliderViewHolder)
                holder.onBind(position)
        }

        override fun getItemCount(): Int {
            return imagesList.size
        }

        inner class SliderViewHolder(val binding: ItemProductImagesBinding) : RecyclerView.ViewHolder(binding.root) {

            fun onBind(position: Int) {

                Glide.with(mContext)
                    .load(imagesList[position].file_path)
                    .into(binding.ivProductImage)
            }
        }
    }

    override fun onBackPressed() {
        supportFinishAfterTransition()
    }
}