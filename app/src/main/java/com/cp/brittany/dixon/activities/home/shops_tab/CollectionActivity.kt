package com.cp.brittany.dixon.activities.home.shops_tab

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.models.ProductSectionsData
import com.cp.brittany.dixon.activities.home.models.ProductSectionsModel
import com.cp.brittany.dixon.activities.home.product_page.ProductPageActivity
import com.cp.brittany.dixon.activities.home.shops_tab.adapters.CollectionAdapter
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivityCollectionBinding
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

class CollectionActivity : BaseActivity() {
    private lateinit var binding: ActivityCollectionBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>
    private var productSectionsList: MutableList<ProductSectionsData> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        retrofitClient = RetrofitClient.getClient(this).create(Api::class.java)
        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()
        postponeEnterTransition()
        //binding.recyclerView.layoutManager = GridLayoutManager(applicationContext,2)
        initVM()
        initListeners()
        initAdapters()
        observeApiResponse()
        getProductsSections()
    }

    private fun initListeners() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initAdapters() {
        binding.recyclerView.adapter = CollectionAdapter(productSectionsList, object : CollectionAdapter.CollectionInterface {
            override fun onItemClicked(position: Int) {
                val i = Intent(applicationContext, CollectionDetailsActivity::class.java)
                i.putExtra("id", productSectionsList[position].id)
                startActivity(i)
            }

        })
        binding.recyclerView.addItemDecoration(EqualSpaceItemDecoration(30))
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
                        ApiTags.GET_PRODUCTS_SECTIONS -> {
                            val model = Gson().fromJson(it.data.toString(), ProductSectionsModel::class.java)
                            productSectionsList.clear()
                            productSectionsList.addAll(model.data)
                            startPostponedEnterTransition()
                            binding.recyclerView.adapter?.notifyDataSetChanged()
                        }
                    }
                }
            }
        })
    }

    private fun getProductsSections() {
        apiCall = retrofitClient.getProductSections()
        viewModel.getProductSections(apiCall)
    }
}