package com.cp.brittany.dixon.activities.home.premium

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import com.android.billingclient.api.SkuDetails
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.models.InsightBookMarkModel
import com.cp.brittany.dixon.activities.home.models.Subscription
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivitySubscriptionBinding
import com.cp.brittany.dixon.utils.AppUtils
import com.cp.brittany.dixon.utils.viewGone
import com.cp.brittany.dixon.utils.viewVisible
import com.faltenreich.skeletonlayout.Skeleton
import com.google.gson.Gson

class PremiumActivity : BaseActivity() {

    private lateinit var binding: ActivitySubscriptionBinding
    private lateinit var skeletonLayout: Skeleton
    companion object {
        var skuProductList: MutableList<SkuDetails> = ArrayList()
    }

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        window.statusBarColor = resources.getColor(R.color.transparent)
        binding = ActivitySubscriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        skeletonLayout = binding.skeletonLayout
        binding.rv.viewGone()
        binding.skeletonLayout.viewVisible()
        skeletonLayout.showSkeleton()
        initListeners()
        getAvailableProducts()
    }

    private fun initListeners(){
        binding.ivBack.setOnClickListener {
            AppUtils.preventDoubleClick(binding.ivBack)
            finish()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getAvailableProducts() {
        skuProductList.clear()
        BillingClientSetup.queryAvailableProducts(this@PremiumActivity) {
            if (it != null) {

                Handler(Looper.getMainLooper()).postDelayed({
                    binding.skeletonLayout.viewGone()
                    skeletonLayout.showOriginal()
                    binding.rv.viewVisible()
                    skuProductList.addAll(it)
                    initAdapter()
                }, 1500)
            }
        }
    }

    private fun initAdapter() {
        binding.rv.adapter = SubscriptionAdapter(skuProductList as ArrayList<SkuDetails>, object : SubscriptionAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val i = Intent(this@PremiumActivity, SubscriptionDetailsActivity::class.java)
                i.putExtra("itemPosition", position)
                startActivity(i)
            }
        })
    }
}