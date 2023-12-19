package com.cp.brittany.dixon.activities.home.podcast

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivityPodcastPlayBinding
import com.cp.brittany.dixon.loader.Loader
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.ApiStatus
import com.cp.brittany.dixon.utils.AppUtils
import com.frolo.waveformseekbar.WaveformSeekBar
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import java.util.*
import kotlin.collections.ArrayList


class PlayPodcastActivity : BaseActivity() {

    private lateinit var binding: ActivityPodcastPlayBinding
    private var mediaPlayer: MediaPlayer = MediaPlayer()
    private var handler: Handler = Handler(Looper.getMainLooper())

    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>
    private var currentPodcastPosition: Int = 0

    companion object {
        var podcastsList: MutableList<PodcastsData> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPodcastPlayBinding.inflate(layoutInflater)
        retrofitClient = RetrofitClient.getClient(this).create(Api::class.java)
        setContentView(binding.root)
        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()

        initVM()
        observeApiResponse()
        initListeners()
        getAllPodcasts()
    }


    private fun initVM() {
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        binding.lifecycleOwner = this
    }

    private fun initListeners() {
        binding.ivPodcastList.setOnClickListener {
            val podcastListBottomSheet = PodcastListBottomSheet(object : PodcastsAdapter.PodcastInterface {
                override fun onPodcastClicked(position: Int) {
                    handler.removeCallbacks(runnable)
                    podcastsList.filter { it.isPlaying }.forEach {
                        it.isPlaying = false
                    }
                    podcastsList[position].isPlaying = true
//                    currentPodcastPosition = position
//                    initMediaPlayer()
                    binding.seekbar.clearAnimation()
//                    binding.seekbar.setProgressInPercentage(0f)
                    mediaPlayer.stop()
                    mediaPlayer.reset()
                    binding.ivPlayPause.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_podcast_play))
                    currentPodcastPosition = position
                    initMediaPlayer()
                    //Play the audio
                }
            })
            podcastListBottomSheet.show(supportFragmentManager, "PodcastListBottomSheet")
        }

        binding.ivReverse.setOnClickListener {
            if (currentPodcastPosition >= 1) {
                mediaPlayer.stop()
                mediaPlayer.reset()
                binding.ivPlayPause.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_podcast_play))
                currentPodcastPosition--
                initMediaPlayer()
            }
        }

        binding.ivForward.setOnClickListener {
            if (currentPodcastPosition < podcastsList.size-1) {
                mediaPlayer.stop()
                mediaPlayer.reset()
                binding.ivPlayPause.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_podcast_play))
                currentPodcastPosition++
                initMediaPlayer()
            }
        }

        binding.ivPlayPause.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                binding.ivPlayPause.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_podcast_play))
                mediaPlayer.pause()
            } else {
                binding.ivPlayPause.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_pause))
                mediaPlayer.start()
            }
        }

        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initMediaPlayer() {

        Glide.with(this)
            .load(podcastsList[currentPodcastPosition].podcast_thumbnail_path)
            .placeholder(R.drawable.ic_placeholder)
            .into(binding.ivPodcastImage)

        binding.tvPodcastTitle.text = podcastsList[currentPodcastPosition].name
        binding.tvPodcastSingerName.text = "${podcastsList[currentPodcastPosition].artist_name} | ${podcastsList[currentPodcastPosition].album_name}"

        Log.e("Podcast", "After Title Set")


//        ad()

//        lifecycleScope.launch(Dispatchers.IO) {
//            binding.waveformSeekBar1.setSampleFrom(
//                intArrayOf(
//                    1, 2,3,4,1,2,3,1,2,3,1,1,23,423,123,12,2,11,4,5,2,42,1,145,534,1,123,4123,123,123,1123,12, 1, 1
//                )
//            )
//            binding.waveformSeekBar.maxProgress = podcastsList[currentPodcastPosition].duration.toFloat()
//            binding.waveformSeekBar.setSampleFrom(podcastsList[currentPodcastPosition].podcast_audio_path)
//        }

        mediaPlayer.setDataSource(podcastsList[currentPodcastPosition].podcast_audio_path)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            binding.ivPlayPause.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_pause))
            mediaPlayer.start()
            handler.postDelayed(runnable, 1000)
            Log.e("duration", mediaPlayer.duration.toString())
        }
        binding.seekbar.setWaveform(createWaveform(), true)
        mediaPlayer.setOnCompletionListener {
//            binding.seekbar.setProgressInPercentage(1f)
            binding.ivPlayPause.setImageResource(R.drawable.ic_play)
            handler.removeCallbacks(runnable)
        }

        binding.seekbar.setCallback(object : WaveformSeekBar.Callback {
            override fun onProgressChanged(seekBar: WaveformSeekBar?, percent: Float, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo((percent * mediaPlayer.duration.toFloat()).toInt())
                }
//                if(fromUser) {
//                    mediaPlayer.seekTo(((binding.seekbar.progressPercent * mediaPlayer.duration) / 100).toInt())
//                    Toast.makeText(
//                        this@PlayPodcastActivity,
//                        "Tracked: percent=" +percent.toString()+"......."+ ((percent * mediaPlayer.duration) / 100).toString(),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }

            }

            override fun onStartTrackingTouch(seekBar: WaveformSeekBar?) {}
            override fun onStopTrackingTouch(seekBar: WaveformSeekBar) {
                //mediaPlayer.seekTo((seekBar.progressPercent * 100).toInt())

//                Toast.makeText(
//                    this@PlayPodcastActivity,
//                    "Tracked: percent=" + seekBar.progressPercent * 100,
//                    Toast.LENGTH_SHORT
//                ).show()
            }
        })

//        binding.waveformSeekBar.onProgressChanged = object : SeekBarOnProgressChanged {
//            override fun onProgressChanged(waveformSeekBar: WaveformSeekBar, progress: Float, fromUser: Boolean) {
//                if (fromUser)
//                    mediaPlayer.seekTo((progress * 1000).toInt())
//            }
//        }

    }

//    private fun ad() {
//        Log.e("Podcast", "Init AD")
//
//        WaveFormData.Factory(podcastsList[currentPodcastPosition].podcast_audio_path)
//            .build(object : WaveFormData.Factory.Callback {
//                //When Complete, you can receive data and set to the view
//                override fun onComplete(waveFormData: WaveFormData) {
//                    Log.e("Podcast", "WaveForm Complete")
//
//                    binding.waveformSeekBar.data = waveFormData
//
//                    //Initialize MediaPlayer
//                    mediaPlayer.setDataSource(podcastsList[currentPodcastPosition].podcast_audio_path)
//                    mediaPlayer.prepareAsync()
//                    mediaPlayer.setOnPreparedListener {
//                        Log.e("Podcast", "Media Player Prepared")
//                        binding.ivPlayPause.setImageDrawable(ContextCompat.getDrawable(this@PlayPodcastActivity, R.drawable.ic_pause))
//                        mediaPlayer.start()
//
//                        lifecycleScope.launch(Dispatchers.Main) {
//                            handler.postDelayed(runnable, 1000)
//                        }
//                    }
//
//                    mediaPlayer.setOnCompletionListener {
//                        lifecycleScope.launch {
//                            binding.ivPlayPause.setImageDrawable(ContextCompat.getDrawable(this@PlayPodcastActivity, R.drawable.ic_podcast_play))
//                            handler.removeCallbacks(runnable)
//                        }
//                    }
//
//                    //Synchronize with MediaPlayer using WaveFormView.Callback
//                    binding.waveformSeekBar.callback = object : WaveFormView.Callback {
//                        override fun onPlayPause() {
//                            if (mediaPlayer.isPlaying)
//                                mediaPlayer.pause()
//                            else
//                                mediaPlayer.start()
//                        }
//
//                        override fun onSeek(pos: Long) {
//                            mediaPlayer.seekTo(pos.toInt())
//                        }
//                    }
//
//                }
//
//                override fun onProgress(progress: Float) {
//                    binding.progressBar.progress = (progress * 10).toInt()
//                }
//            })
//    }

    private fun createWaveform(): IntArray {
        val random = Random(System.currentTimeMillis())
        val length = 100

//        val length = if (podcastsList[currentPodcastPosition].duration < 100) 100 else podcastsList[currentPodcastPosition].duration
        val values = IntArray(length)
        var maxValue = 0
        for (i in 0 until length) {
            val newValue: Int = 5 + random.nextInt(50)
            if (newValue > maxValue) {
                maxValue = newValue
            }
            values[i] = newValue
        }
        return values
    }

    private val runnable by lazy {
        object : Runnable {
            override fun run() {
//                binding.waveformSeekBar.progress = (mediaPlayer.currentPosition.div(1000)).toFloat()
//                handler.postDelayed(this, 1000)

//                binding.waveformSeekBar.position = mediaPlayer.currentPosition.toLong()
//                handler.postDelayed(this, 20)

                //binding.seekbar.setProgressInPercentage(mediaPlayer.currentPosition / 1000f)
                val mCurrentPosition: Float =
                    (mediaPlayer.currentPosition.toFloat() ) / mediaPlayer.duration
                binding.seekbar.setProgressInPercentage(mCurrentPosition)
                Log.e("current duration", mediaPlayer.currentPosition.toString()+"......"+mCurrentPosition)
                handler.postDelayed(this, 1000)
            }
        }
    }

    private fun observeApiResponse() {
        viewModel.getApiResponse().observe(this, {
            when (it.status) {
                ApiStatus.LOADING -> {
                    Loader.showLoader(this) {
                        apiCall.cancel()
                    }
                }
                ApiStatus.ERROR -> {
                    Loader.hideLoader()
                    AppUtils.showToast(this, it.message!!, false)
                }
                ApiStatus.SUCCESS -> {
                    Loader.hideLoader()
                    when (it.tag) {
                        ApiTags.GET_ALL_PODCASTS -> {
                            val model = Gson().fromJson(it.data.toString(), PodcastsModel::class.java)
                            podcastsList.clear()
                            podcastsList.addAll(model.data)

                            if (podcastsList.isNotEmpty())
                                podcastsList[currentPodcastPosition].isPlaying = true //currentPodcastPosition will be zero at start

                            Log.e("Podcast", "Podcasts Response")
                            initMediaPlayer()
                        }
                    }
                }
            }
        })
    }


    private fun getAllPodcasts() {
        apiCall = retrofitClient.getAllPodcasts()
        viewModel.getAllPodcasts(apiCall)
    }

    override fun onDestroy() {
        handler.removeCallbacks(runnable)
        binding.waveformSeekBar.data
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.reset()
            mediaPlayer.release()
        }
        super.onDestroy()
    }
}