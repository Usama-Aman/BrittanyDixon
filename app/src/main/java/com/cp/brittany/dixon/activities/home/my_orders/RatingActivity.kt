package com.cp.brittany.dixon.activities.home.my_orders

import android.os.Bundle
import android.view.View
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivityRatingBinding

class RatingActivity : BaseActivity() {
    private lateinit var binding: ActivityRatingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        window.statusBarColor = resources.getColor(R.color.transparent)
        binding = ActivityRatingBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}