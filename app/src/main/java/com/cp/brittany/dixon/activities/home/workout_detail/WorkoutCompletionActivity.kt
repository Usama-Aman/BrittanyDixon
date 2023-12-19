package com.cp.brittany.dixon.activities.home.workout_detail

import android.media.MediaPlayer
import android.os.Bundle
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivityWorkoutCompletionBinding


class WorkoutCompletionActivity : BaseActivity() {

    private lateinit var binding: ActivityWorkoutCompletionBinding
    private lateinit var player: MediaPlayer
    private var list: ArrayList<ConfettiModel> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkoutCompletionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        blackStatusBarIcons()
        createConfetti()
        changeStatusBarColor(R.color.white)
        player = MediaPlayer.create(this, R.raw.workout_completed)
        player.start()
        binding.tvDone.setOnClickListener {
            finish()
        }

    }
    /*
    Once a workout is completed, The user needs a visual reward... like Great Job, You Rock, Killing it, Keep it Up, Crushing it, Awesome Work, etc, with Confetti
     */

    private fun createConfetti(){
        list.add(ConfettiModel("You Rock","love"))
        list.add(ConfettiModel("Great Job","great_animation"))
        list.add(ConfettiModel("Keep it Up","confetti"))
        list.add(ConfettiModel("Killing it","fist_bump"))
        list.add(ConfettiModel("Awesome Work","great_animation"))
        list.add(ConfettiModel("Crushing it","star_success"))

        val rnds = (0..5).random()
        binding.tvTitle.text = list[rnds].name
        binding.doneAnimation.setAnimation("${list[rnds].animation}.json")
    }

    override fun onDestroy() {
        super.onDestroy()
        player.stop()
        player.release()
    }
}