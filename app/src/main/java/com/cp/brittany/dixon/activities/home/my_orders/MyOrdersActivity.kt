package com.cp.brittany.dixon.activities.home.my_orders

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.product_page.ProductPageActivity
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.base.AppController
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivityMyordersBinding
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

class MyOrdersActivity : BaseActivity() {
    private lateinit var binding: ActivityMyordersBinding
    private lateinit var myOrdersAdapter: MyOrdersAdapter


    private lateinit var mContext: Context
    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>

    private var myOrdersData: MutableList<MyOrdersData> = ArrayList()
    private var skip = 0
    private var canLoadMore = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyordersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()
        retrofitClient = RetrofitClient.getClient(this).create(Api::class.java)
        mContext = this

        initVM()
        initListeners()
        initAdapters()
        observeApiResponse()

        getUserOrders()
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
        myOrdersAdapter = MyOrdersAdapter(myOrdersData, object : MyOrdersAdapter.MyOrderInterface {
            override fun onItemClicked(productId: Int) {
                val intent = Intent(mContext, ProductPageActivity::class.java)
                intent.putExtra("productId", productId)
                startActivity(intent)
            }
        })
        binding.rvOrders.adapter = myOrdersAdapter
        binding.rvOrders.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lm = recyclerView.layoutManager as LinearLayoutManager
                if (lm.findLastCompletelyVisibleItemPosition() == myOrdersData.size - 1)
                    if (canLoadMore) {
                        canLoadMore = false
                        getUserOrders()
                    }
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeApiResponse() {
        viewModel.getApiResponse().observe(this, {
            when (it.status) {
                ApiStatus.LOADING -> {
                    if (it.tag != ApiTags.GET_ALL_FOODS)
                        Loader.showLoader(this) {
                            if (this::apiCall.isInitialized)
                                apiCall.cancel()
                        }
                }
                ApiStatus.ERROR -> {
                    Loader.hideLoader()
                    if (!it.message.isNullOrEmpty() && it.message == "No Orders found") {
                        binding.rvOrders.viewGone()
                        binding.llNoData.viewVisible()
                    }
                    else{
                        AppUtils.showToast(this, it.message!!, false)
                    }
                }
                ApiStatus.SUCCESS -> {
                    Loader.hideLoader()
                    when (it.tag) {
                        ApiTags.GET_USER_ORDERS -> {
                            val model = Gson().fromJson(it.data.toString(), MyOrdersModel::class.java)
                            handlerOrderResponse(model.data)
                        }
                    }
                }
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun handlerOrderResponse(data: List<MyOrdersData>) {
//        if (myOrdersData.size > 0)
//            myOrdersData.removeAt(myOrdersData.size - 1)



        if (data.isNullOrEmpty()) {
            binding.rvOrders.viewGone()
            binding.llNoData.viewVisible()
        } else {
            binding.llNoData.viewGone()
            binding.rvOrders.viewVisible()
            myOrdersData.addAll(data)

            if (data.size >= AppController.PAGINATION_COUNT) {
                skip += AppController.PAGINATION_COUNT
//            myOrdersData.add(null)
                canLoadMore = true
            }
            myOrdersAdapter.notifyDataSetChanged()
        }


    }


    private fun getUserOrders() {
        apiCall = retrofitClient.getUserOrders(skip)
        viewModel.getUserOrders(apiCall)
    }

}