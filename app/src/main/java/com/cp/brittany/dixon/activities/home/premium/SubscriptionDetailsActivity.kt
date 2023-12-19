package com.cp.brittany.dixon.activities.home.premium

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.android.billingclient.api.*
import com.astritveliu.boom.Boom
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.HomeActivity
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.base.getAmount
import com.cp.brittany.dixon.databinding.ActivitySubscriptionDetailsBinding
import com.cp.brittany.dixon.databinding.ItemPremiumDetailsViewPagerBinding
import com.cp.brittany.dixon.utils.*

class SubscriptionDetailsActivity : BaseActivity(), OnItemClickListener {

    private lateinit var binding: ActivitySubscriptionDetailsBinding
    private lateinit var premiumItem: SkuDetails
    private var mAdapter: ViewSliderAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySubscriptionDetailsBinding.inflate(layoutInflater)
        window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        window.statusBarColor = resources.getColor(R.color.transparent)
        setContentView(binding.root)

        initAdapter()
        initListeners()

    }


    private fun initListeners() {

        if (intent != null && intent.hasExtra("itemPosition")) {
            val position = intent.getIntExtra("itemPosition", -1)
            if (position != -1 && position < PremiumActivity.skuProductList.size) {
                premiumItem = PremiumActivity.skuProductList[position]
                binding.viewPager.setCurrentItem(position, false)
            }
        }

        BillingClientSetup.setPurchaseUpdatedListener(object : BillingClientSetup.BillingPurchaseInterface {
            override fun onPurchaseUpdated(billingResult: BillingResult, purchases: Purchase) {
                SharedPreference.saveBoolean(this@SubscriptionDetailsActivity, Constants.isComingAfterSubscribing, true)

                val intent = Intent(this@SubscriptionDetailsActivity, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        })

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                //Toast.makeText(context,position.toString(), Toast.LENGTH_SHORT).show()
                if (position < PremiumActivity.skuProductList.size) {
                    premiumItem = PremiumActivity.skuProductList[position]
                }
            }

        })

        binding.ivBack.setOnClickListener {
            AppUtils.preventDoubleClick(binding.ivBack)
            finish()
        }
    }

    private fun initAdapter() {
        mAdapter = ViewSliderAdapter(PremiumActivity.skuProductList, this)
        binding.viewPager.adapter = mAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        BillingClientSetup.setPurchaseUpdatedListener(null)
    }

    override fun onItemClick(view: View) {
        when (view.id) {
            R.id.tvSubscribe -> {
                if (::premiumItem.isInitialized) {
                    val billingFlowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(premiumItem)
                        .build()
                    BillingClientSetup.billingClient.launchBillingFlow(this, billingFlowParams).responseCode
                }
            }
        }
    }

    private inner class ViewSliderAdapter(private val skuProductList: MutableList<SkuDetails>, private val mListener: OnItemClickListener) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private lateinit var mContext: Context

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            mContext = parent.context
            return SliderViewHolder(
                DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_premium_details_view_pager, parent, false), mListener
            )
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is SliderViewHolder)
                holder.onBind(position)
        }

        override fun getItemCount(): Int = skuProductList.size

        inner class SliderViewHolder(val binding: ItemPremiumDetailsViewPagerBinding, val listener: OnItemClickListener) :
            RecyclerView.ViewHolder(binding.root) {

            @SuppressLint("SetTextI18n")
            fun onBind(position: Int) {
                Boom(binding.tvSubscribe)
                binding.rv.adapter = SubscriptionDetailsAdapter()
                binding.tvPrice.text = String.format("%.2f", skuProductList[position].price.getAmount().toDouble())
                binding.currency.text = skuProductList[position].priceCurrencyCode

//                binding.tvTimePeriod.text = when {
//                    premiumItem.subscriptionPeriod.contains("W") -> "Weekly"
//                    premiumItem.subscriptionPeriod.contains("M") -> "Monthly"
//                    else -> "Yearly"
//                }
                binding.tvTimePeriod.text = skuProductList[position].title.replace("(Brittany Dixon Fitness App)", "").trim()
                binding.tvUnit.text = when {
                    skuProductList[position].subscriptionPeriod.contains("W") -> "/w"
                    skuProductList[position].subscriptionPeriod.contains("M") -> "/m"
                    else -> "/y"
                }
                binding.tvSubscribe.setOnClickListener {
                    AppUtils.preventDoubleClick(binding.tvSubscribe)
                    listener.onItemClick(binding.tvSubscribe)
                }
            }
        }
    }
}

interface OnItemClickListener {
    fun onItemClick(view: View)
}