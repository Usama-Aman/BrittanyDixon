package com.cp.brittany.dixon.activities.home.order_done

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.HomeActivity
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivityOrderDoneBinding

class OrderDoneActivity : BaseActivity() {
    private lateinit var binding: ActivityOrderDoneBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDoneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()

        initListeners()
    }

    private fun initListeners() {
        binding.ivCross.setOnClickListener {
            gotoHome()
        }

        binding.btnTrackOrder.setOnClickListener {
            gotoHome()
        }
    }

    private fun gotoHome() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@OrderDoneActivity, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }, 100)
    }
}