package com.cp.brittany.dixon.activities.home.start_workout

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import androidx.core.content.ContextCompat
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivityExerciseRestBinding
import com.cp.brittany.dixon.utils.AppUtils
import com.cp.brittany.dixon.utils.CircularCountDownBar

class ExerciseRestActivity : BaseActivity() {

    private lateinit var binding: ActivityExerciseRestBinding
    private lateinit var mContext: Context

    private var duration: Int = 0 // in secs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseRestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext = this@ExerciseRestActivity

        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()

        if (intent?.getStringExtra("duration") != null) {
            duration = (intent?.getStringExtra("duration") ?: "0").toInt()
        } else {
            AppUtils.showToast(this, "Something's wrong", false)
            finish()
        }


        initCounter(duration * 1000L)
    }

    private fun initCounter(cd: Long) {
        val countDownBar = CircularCountDownBar.Builder(mContext)
            .setMaxProgress(duration) // time seconds count down setting
            .setProgressColor(ContextCompat.getColor(mContext, R.color.rest_progress_color))
            .setStrokeWidth(30)
            .setTextColor(ContextCompat.getColor(mContext, R.color.rest_progress_color))
            .setRoundedCorners(true)
            .setDrawText(true)
            .build()

        binding.restCounter.addView(countDownBar)

        object : CountDownTimer(cd, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countDownBar.setProgress((millisUntilFinished / 1000).toInt())
            }

            override fun onFinish() {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }.start()

    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        finish()
    }
}