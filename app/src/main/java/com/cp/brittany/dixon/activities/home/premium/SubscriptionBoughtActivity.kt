package com.cp.brittany.dixon.activities.home.premium

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.bumptech.glide.Glide
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.base.getAmount
import com.cp.brittany.dixon.databinding.ActivitySubscriptionBoughtBinding
import com.cp.brittany.dixon.utils.AppUtils
import com.cp.brittany.dixon.utils.Constants
import com.cp.brittany.dixon.utils.viewGone
import com.cp.brittany.dixon.utils.viewVisible
import org.json.JSONObject

class SubscriptionBoughtActivity : BaseActivity() {

    private lateinit var binding: ActivitySubscriptionBoughtBinding
    private lateinit var subscriptionsJson: JSONObject

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubscriptionBoughtBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()

        subscriptionsJson = JSONObject(intent.getStringExtra("subscriptionData") ?: "")


        BillingClientSetup.queryAvailableProducts(this@SubscriptionBoughtActivity) {
            if (it != null) {
                for (i in it.indices) {
                    val jsonObject = JSONObject(it[i].originalJson)
                    Log.d("PP", jsonObject.toString())
                    if (jsonObject.getString("productId") == subscriptionsJson.getString("productId")) {
                        runOnUiThread {
                            binding.tvPrice.text = String.format("%.2f", it[i].price.getAmount().toDouble())
                            binding.currency.text = it[i].priceCurrencyCode
                            binding.tvSubscriptionName.text = jsonObject.getString("name")

                        binding.tvUnit.text = when {
                            it[i].subscriptionPeriod.contains("W") -> "/w"
                            it[i].subscriptionPeriod.contains("M") -> "/m"
                            else -> "/y"
                        }
                        }
                    }
                }
            }
        }

        val userData = AppUtils.getUserDetails(this)
        Glide.with(this)
            .load(userData.image_path)
            .placeholder(R.drawable.ic_placeholder)
            .into(binding.ivProfileImage)

        binding.tvStartWorkout.setOnClickListener {
            finish()
        }

        binding.ivCross.setOnClickListener {
            finish()
        }

    }


}