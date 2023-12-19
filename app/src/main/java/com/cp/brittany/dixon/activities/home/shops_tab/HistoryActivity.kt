package com.cp.brittany.dixon.activities.home.shops_tab

import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProviders
import com.cp.brittany.dixon.activities.home.shops_tab.adapters.HistoryAdapter
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.models.*
import com.cp.brittany.dixon.activities.home.product_page.ProductPageActivity
import com.cp.brittany.dixon.activities.home.shops_tab.adapters.WishListAdapter
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivityHistoryBinding
import com.cp.brittany.dixon.loader.Loader
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.*
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call

class HistoryActivity : BaseActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>
    private var historyProducts: MutableList<HistoryProductsItem> = ArrayList()
    private var categoryId = 0
    private var skip = 0
    private var isWishlist = false
    private var wishList: MutableList<WishListData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        retrofitClient = RetrofitClient.getClient(this).create(Api::class.java)
        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()
        //binding.recyclerView.layoutManager = GridLayoutManager(applicationContext,2)
        categoryId = intent.getIntExtra("id", -1)
        skip = intent.getIntExtra("skip", 0)
        isWishlist = intent.getBooleanExtra("isWishlist", false)
        binding.title.text = intent.getStringExtra("title")
        initVM()
        initListeners()
        initAdapters()
        observeApiResponse()
        if (!isWishlist) {
            getHistoryProducts(categoryId)
        } else {
            getWishListProducts()
        }
    }

    private fun initListeners() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initAdapters() {
        if (isWishlist) {
            binding.recyclerView.adapter =
                WishListAdapter(wishList as ArrayList<WishListData>, false, object : WishListAdapter.WishListProductsInterface {
                    override fun onItemClick(position: Int, option: ActivityOptionsCompat) {
                        val intent = Intent(applicationContext, ProductPageActivity::class.java)
                        intent.putExtra("productId", wishList[position].bookmarked_item.id)
                        startActivity(intent)
                    }

                })
            binding.recyclerView.addItemDecoration(EqualSpaceItemDecoration(30))
        } else {
            binding.recyclerView.adapter = HistoryAdapter(historyProducts, object : HistoryAdapter.HistoryProductInterface {
                override fun onItemClicked(position: Int) {
                    val intent = Intent(applicationContext, ProductPageActivity::class.java)
                    intent.putExtra("productId", historyProducts[position].product_data.product_id)
                    startActivity(intent)
                }

            })
            binding.recyclerView.addItemDecoration(EqualSpaceItemDecoration(30))
        }
    }

    private fun initVM() {
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        binding.lifecycleOwner = this
    }


    private fun observeApiResponse() {
        viewModel.getApiResponse().observe(this, {
            when (it.status) {
                ApiStatus.LOADING -> {
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
                        ApiTags.GET_HISTORY_PRODUCTS -> {
                            val model = Gson().fromJson(it.data.toString(), HistoryProductsModel::class.java)
                            //AppUtils.showToast(this, model.message, true)
                            historyProducts.clear()
                            historyProducts.addAll(model.data.items)
                            binding.recyclerView.adapter?.notifyDataSetChanged()
                        }
                        ApiTags.GET_BOOKMARK -> {
                            wishList.clear()
                            val model = Gson().fromJson(it.data.toString(), WishListModel::class.java)
                            wishList.addAll(model.data)
                            binding.recyclerView.adapter?.notifyDataSetChanged()
                        }
                    }
                }
            }
        })
    }

    private fun getHistoryProducts(id: Int) {
        apiCall = retrofitClient.getHistoryProducts(id, skip)
        viewModel.getHistoryProducts(apiCall)
    }

    private fun getWishListProducts() {
        apiCall = retrofitClient.getWorkoutBookmark("products")
        viewModel.getWorkoutBookmarks(apiCall)
    }
}