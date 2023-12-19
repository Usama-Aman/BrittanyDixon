package com.cp.brittany.dixon.activities.home.podcast

import android.os.Bundle
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.base.transparentStatusBar
import com.cp.brittany.dixon.databinding.ActivityComingPodcastBinding
import io.ak1.pix.helpers.showStatusBar

class ComingPodcastActivity : BaseActivity() {
    private lateinit var binding: ActivityComingPodcastBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusBar()
        blackStatusBarIcons()
        binding = ActivityComingPodcastBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tvDone.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        showStatusBar()
    }
}