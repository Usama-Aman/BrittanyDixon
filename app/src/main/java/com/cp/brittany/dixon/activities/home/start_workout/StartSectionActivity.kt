package com.cp.brittany.dixon.activities.home.start_workout

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Vibrator
import android.view.View
import androidx.core.content.ContextCompat
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivityStartSectionBinding
import com.cp.brittany.dixon.utils.CircularCountDownBar
import com.bumptech.glide.Glide

class StartSectionActivity : BaseActivity() {

    private lateinit var binding: ActivityStartSectionBinding
    private lateinit var player: MediaPlayer
    private lateinit var vibrator: Vibrator
    private lateinit var counter: CountDownTimer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartSectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        window.statusBarColor = android.graphics.Color.TRANSPARENT

        initViews()
        initCounter(4000L)
    }

    private fun initViews() {
        vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        player = MediaPlayer.create(this, R.raw.count_down)
        binding.tvSectionTitle.text = intent?.getStringExtra("sectionName") ?: ""
        binding.tvWorkoutTitle.text = intent?.getStringExtra("workoutName") ?: ""

        Glide.with(this)
            .load(intent?.getStringExtra("sectionImage") ?: "")
//            .placeholder(R.drawable.img_workout_videos)
            .into(binding.ivSectionImage)
    }

    private fun initCounter(cd: Long) {
        val countDownBar = CircularCountDownBar.Builder(this)
            .setMaxProgress(5) // time seconds count down setting
            .setProgressColor(ContextCompat.getColor(this, R.color.red))
            .setStrokeWidth(30)
            .setTextColor(ContextCompat.getColor(this, R.color.red))
            .setRoundedCorners(true)
            .setDrawText(true)
            .build()

        binding.restCounter.addView(countDownBar)

        counter = object : CountDownTimer(cd, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countDownBar.setProgress((millisUntilFinished / 1000).toInt())
                vibrator.vibrate(3)
                if (!player.isPlaying) {
                    player.start()
                }
            }

            override fun onFinish() {
                player.stop()
                player.release()
                setResult(Activity.RESULT_OK)
                finish()
            }
        }.start()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        counter.cancel()
        player.stop()
        player.release()
    }

}