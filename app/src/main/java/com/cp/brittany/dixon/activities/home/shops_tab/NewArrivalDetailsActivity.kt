package com.cp.brittany.dixon.activities.home.shops_tab

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.models.NewArrivalsProductsItem
import com.cp.brittany.dixon.activities.home.models.NewArrivalsProductsModel
import com.cp.brittany.dixon.activities.home.product_page.ProductPageActivity
import com.cp.brittany.dixon.activities.home.shops_tab.adapters.NewArrivalAdapter
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivityNewArrivalDetailsBinding
import com.cp.brittany.dixon.loader.Loader
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.ApiStatus
import com.cp.brittany.dixon.utils.AppUtils
import com.cp.brittany.dixon.utils.EqualSpaceItemDecoration
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call

class NewArrivalDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityNewArrivalDetailsBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>
    private var newArrivalProducts: MutableList<NewArrivalsProductsItem> = ArrayList()
    private var categoryId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewArrivalDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        retrofitClient = RetrofitClient.getClient(this).create(Api::class.java)
        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()
        //binding.recyclerView.layoutManager = GridLayoutManager(applicationContext,2)
        categoryId = intent.getIntExtra("id", -1)
        initVM()
        initListeners()
        initAdapters()
        observeApiResponse()
        getNewArrivals(categoryId)
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
    }

    private fun initAdapters() {
        binding.recyclerView.adapter = NewArrivalAdapter(newArrivalProducts, object : NewArrivalAdapter.NewArrivalProductInterface {
            override fun onItemClicked(position: Int) {
                val intent = Intent(applicationContext, ProductPageActivity::class.java)
                intent.putExtra("productId", newArrivalProducts[position].product_id)
                startActivity(intent)
            }

        })
        binding.recyclerView.addItemDecoration(EqualSpaceItemDecoration(30))
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
                        ApiTags.GET_NEW_ARRIVALS -> {
                            val model = Gson().fromJson(it.data.toString(), NewArrivalsProductsModel::class.java)
                            //AppUtils.showToast(this, model.message, true)
                            newArrivalProducts.clear()
                            newArrivalProducts.addAll(model.data.items)
                            binding.recyclerView.adapter?.notifyDataSetChanged()
                        }
                    }
                }
            }
        })
    }

    private fun getNewArrivals(id: Int) {
        apiCall = retrofitClient.getNewArrivals(id)
        viewModel.getNewArrivals(apiCall)
    }
}