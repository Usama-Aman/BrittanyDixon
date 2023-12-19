package com.cp.brittany.dixon.activities

import android.os.Bundle
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivityZoomImageBinding
import com.bumptech.glide.Glide

class ZoomImageActivity : BaseActivity() {
    private lateinit var binding: ActivityZoomImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityZoomImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()

        var imageUrl = intent.getStringExtra("imageUrl")
        Glide.with(this).load(imageUrl).into(binding.ivZoomable);

        initListeners()
    }


    private fun initListeners() {
        binding.ivClose.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        supportFinishAfterTransition()
    }
}