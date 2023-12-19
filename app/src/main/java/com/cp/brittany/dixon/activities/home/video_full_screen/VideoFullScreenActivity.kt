package com.cp.brittany.dixon.activities.home.video_full_screen

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivityVideoFullScreenBinding
import com.google.android.exoplayer2.ui.PlayerView

class VideoFullScreenActivity : BaseActivity() {
    private lateinit var binding: ActivityVideoFullScreenBinding
    private lateinit var playerView: PlayerView
    private var videoUrl: Uri? = Uri.parse("https://imgur.com/7bMqysJ.mp4")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoFullScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        playerView = binding.exoplayerView
        window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.statusBarColor = Color.TRANSPARENT

        initViews()
        initListeners()
        initVideoView()
    }

    private fun initViews() {

    }

    private fun initListeners() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun initVideoView() {

    }
}