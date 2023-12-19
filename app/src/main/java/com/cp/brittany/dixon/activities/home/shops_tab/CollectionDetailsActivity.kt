package com.cp.brittany.dixon.activities.home.shops_tab

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.models.NewArrivalsProductsItem
import com.cp.brittany.dixon.activities.home.models.NewArrivalsProductsModel
import com.cp.brittany.dixon.activities.home.models.SectionProduct
import com.cp.brittany.dixon.activities.home.models.SectionProductsModel
import com.cp.brittany.dixon.activities.home.product_page.ProductPageActivity
import com.cp.brittany.dixon.activities.home.shops_tab.adapters.CollectionDetailsAdapter
import com.cp.brittany.dixon.activities.home.shops_tab.adapters.NewArrivalAdapter
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivityCollectionDetailsBinding
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

class CollectionDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityCollectionDetailsBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>
    private var products: MutableList<SectionProduct> = ArrayList()
    private var categoryId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollectionDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        retrofitClient = RetrofitClient.getClient(this).create(Api::class.java)
        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()
        categoryId = intent.getIntExtra("id", -1)
        initVM()
        initListeners()
        initAdapters()
        observeApiResponse()
        getSectionProducts(categoryId)
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
        binding.recyclerView.adapter = CollectionDetailsAdapter(products, object : CollectionDetailsAdapter.SectionProductInterface {
            override fun onItemClicked(position: Int) {
                val i = Intent(applicationContext, ProductPageActivity::class.java)
                //intent.putExtra("productId", products[position].id)
                i.putExtra("productId", products[position].id)
                i.putExtra("title", products[position].name)
                i.putExtra("price", products[position].minimum_price.compare_at_price)
                i.putExtra("discountedPrice", products[position].minimum_price.price)
                if (products[position].sub_category_name != null)
                    i.putExtra("category", products[position].sub_category_name.name)
                else
                    i.putExtra("category", products[position].category_name.name)
                startActivity(i)
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
                        ApiTags.GET_SECTION_PRODUCTS -> {
                            val model = Gson().fromJson(it.data.toString(), SectionProductsModel::class.java)
                            //AppUtils.showToast(this, model.message, true)
                            products.clear()
                            if (!model.data.isNullOrEmpty())
                                products.addAll(model.data[0].section_products)
                            binding.recyclerView.adapter?.notifyDataSetChanged()
                        }
                    }
                }
            }
        })
    }

    private fun getSectionProducts(id: Int) {
        apiCall = retrofitClient.getSectionProductsById(id)
        viewModel.getSectionProductsById(apiCall)
    }
}