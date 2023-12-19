package com.cp.brittany.dixon.activities.home.start_workout

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import androidx.activity.result.ActivityResult
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.workout_detail.WorkoutCompletionActivity
import com.cp.brittany.dixon.activities.home.workout_detail.WorkoutDetailActivity.Companion.videosListToPlay
import com.cp.brittany.dixon.base.AppController
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.base.BaseActivityResult
import com.cp.brittany.dixon.databinding.ActivityPlayWorkoutVideoBinding
import com.cp.brittany.dixon.loader.Loader
import com.cp.brittany.dixon.utils.AppUtils
import com.cp.brittany.dixon.utils.BrittanyEnums
import com.cp.brittany.dixon.utils.Constants
import com.cp.brittany.dixon.utils.VideoPreLoadingService
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class PlayWorkoutVideoActivity : BaseActivity() {

    companion object {
        private const val TAG = "PlayWorkoutVideoActivity"
    }

    private lateinit var binding: ActivityPlayWorkoutVideoBinding
    private lateinit var httpDataSourceFactory: HttpDataSource.Factory
    private lateinit var defaultDataSourceFactory: DefaultDataSource.Factory
    private lateinit var cacheDataSourceFactory: DataSource.Factory
    private var simpleExoPlayer: ExoPlayer? = null
    private val simpleCache: SimpleCache = AppController.simpleCache

    private lateinit var mContext: Context

    private var videoCacheIndex: Int = 0
    private var videoPlayIndex: Int = 0
    private var handler: Handler = Handler(Looper.getMainLooper())
    private var currentVideoDuration = 0L
    private var sectionFound = false

    private lateinit var tvVideoTimer: TextView
    private lateinit var tvVideoTitle: TextView


    //Minimum Video you want to buffer while Playing
    private val MIN_BUFFER_DURATION = 2000

    //Max Video you want to buffer during PlayBack
    private val MAX_BUFFER_DURATION = 5000

    //Min Video you want to buffer before start Playing it
    private val MIN_PLAYBACK_START_BUFFER = 1500

    //Min video You want to buffer when user resumes video
    private val MIN_PLAYBACK_RESUME_BUFFER = 2000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayWorkoutVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext = this@PlayWorkoutVideoActivity

        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()

        initViews()

        AppController.socket.on("update_progress") {
            println("Testing $it")
        }

    }

    private fun initViews() {
        tvVideoTimer = binding.playerControls.findViewById(R.id.exo_remaining_time)
        tvVideoTitle = binding.playerControls.findViewById(R.id.tvVideoTitle)
        initPlayer()
        binding.playerControls.player = binding.exoplayerView.player
        initCheckForVideo()
    }

    private fun initCheckForVideo() {

        if (videoPlayIndex > 0 && videoPlayIndex < videosListToPlay.size) {
            if (videosListToPlay[if (sectionFound) videoPlayIndex else videoPlayIndex - 1].section_name == videosListToPlay[videoPlayIndex].section_name) {
                checkAndPlayVideo()
            } else {
                sectionFound = true
                showStartSectionScreen()
            }
        } else {
            checkAndPlayVideo()
        }
    }

    private fun checkAndPlayVideo() {
        sectionFound = false
        if (videoPlayIndex < videosListToPlay.size) {
            if (videosListToPlay[videoPlayIndex].is_rest == 0) {
                playVideo()
            } else {
                val intent = Intent(this@PlayWorkoutVideoActivity, ExerciseRestActivity::class.java)
                intent.putExtra("duration", videosListToPlay[videoPlayIndex].duration)
                activityLauncher.launch(intent, object : BaseActivityResult.OnActivityResult<ActivityResult> {
                    override fun onActivityResult(result: ActivityResult) {
                        //hitSocket(true)
                        videoPlayIndex++
                        initCheckForVideo()
                    }
                })
            }
        } else {
            startActivity(Intent(this@PlayWorkoutVideoActivity, WorkoutCompletionActivity::class.java))
            this@PlayWorkoutVideoActivity.finish()
        }
    }

    private fun showStartSectionScreen() {
        val intent = Intent(this@PlayWorkoutVideoActivity, StartSectionActivity::class.java)
        intent.putExtra("workoutName", videosListToPlay[videoPlayIndex].workout_name)
        intent.putExtra("sectionName", videosListToPlay[videoPlayIndex].section_name)
        intent.putExtra("sectionImage", videosListToPlay[videoPlayIndex].thumbnail_path)
        activityLauncher.launch(intent, object : BaseActivityResult.OnActivityResult<ActivityResult> {
            override fun onActivityResult(result: ActivityResult) {
                initCheckForVideo()
            }
        })
    }

    private fun initPlayer() {
        val loadControl: LoadControl = DefaultLoadControl.Builder()
            .setAllocator(DefaultAllocator(true, 16))
            .setBufferDurationsMs(
                MIN_BUFFER_DURATION,
                MAX_BUFFER_DURATION,
                MIN_PLAYBACK_START_BUFFER,
                MIN_PLAYBACK_RESUME_BUFFER
            )
            .setTargetBufferBytes(-1)
            .setPrioritizeTimeOverSizeThresholds(true).build()

        httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)

        defaultDataSourceFactory = DefaultDataSource.Factory(mContext, httpDataSourceFactory)

        cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(simpleCache)
            .setUpstreamDataSourceFactory(httpDataSourceFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

        val trackSelector: TrackSelector = DefaultTrackSelector(mContext)
        simpleExoPlayer = ExoPlayer.Builder(mContext)
            .setMediaSourceFactory(DefaultMediaSourceFactory(cacheDataSourceFactory))
            .setLoadControl(loadControl)
            .setTrackSelector(trackSelector)
            .build()

        binding.exoplayerView.player = simpleExoPlayer
        binding.exoplayerView.resizeMode = RESIZE_MODE_FIT
        binding.exoplayerView.setShutterBackgroundColor(Color.WHITE)
        simpleExoPlayer?.playWhenReady = true
        simpleExoPlayer?.addListener(exoPlayerEventListener)

        binding.ivClose.setOnClickListener {
            finish()
        }

    }

    private fun playVideo() {
        tvVideoTitle.text = videosListToPlay[videoPlayIndex].video_name
        currentVideoDuration = videosListToPlay[videoPlayIndex].duration.toLong() * 1000L
        val videoUri = Uri.parse(videosListToPlay[videoPlayIndex].video_path)
        val mediaItem = MediaItem.fromUri(videoUri)
        val mediaSource = ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(mediaItem)

        simpleExoPlayer?.seekTo(0, 0)
        simpleExoPlayer?.repeatMode = Player.REPEAT_MODE_OFF
        simpleExoPlayer?.setMediaSource(mediaSource, true)
        simpleExoPlayer?.prepare()

        startCachingVideo()
    }

    private fun startCachingVideo() {

        if (videoCacheIndex < videosListToPlay.size) {
            // Caching video
            if (videoCacheIndex == 0) {
                if (videosListToPlay[videoCacheIndex].is_rest == 0) {
                    Log.e("VideoIndexPath", "$videoCacheIndex ${videosListToPlay[videoCacheIndex].video_path}")
                    if (videoCacheIndex < videosListToPlay.size)
                        startPreLoadingService(videoCacheIndex)
                    videoCacheIndex++
                }
                else{
                    var found = false
                    while (!found){
                        if(videosListToPlay[videoCacheIndex].is_rest == 1){
                            videoCacheIndex++
                        }
                        else{
                            found = true
                            if (videoCacheIndex < videosListToPlay.size)
                                startPreLoadingService(videoCacheIndex)
                            videoCacheIndex++
                            break
                        }
                    }
                }
            }

            if ( videoCacheIndex < videosListToPlay.size && videosListToPlay[videoCacheIndex].is_rest == 0) {
                Log.e("VideoIndexPath", "$videoCacheIndex ${videosListToPlay[videoCacheIndex].video_path}")
                if (videoCacheIndex < videosListToPlay.size)
                    startPreLoadingService(videoCacheIndex)
            }
            else{
                var found = false
                while (!found){
                    if( videoCacheIndex < videosListToPlay.size && videosListToPlay[videoCacheIndex].is_rest == 1){
                        videoCacheIndex++
                    }
                    else{
                        found = true
                        if (videoCacheIndex < videosListToPlay.size)
                            startPreLoadingService(videoCacheIndex)
                        videoCacheIndex++
                        break
                    }
                }
            }
        }
    }

    private val runnable by lazy {
        object : Runnable {
            @SuppressLint("SimpleDateFormat")
            override fun run() {
                println("Duration -> $currentVideoDuration")
                val date = Date((currentVideoDuration)) //260000 milliseconds
                val sdf = SimpleDateFormat("mm : ss")
                val result: String = sdf.format(date)
                tvVideoTimer.text = result
                currentVideoDuration -= 1000
                handler.postDelayed(this, 1000)
            }
        }
    }

    private fun hitSocket(isEnd: Boolean = false) {
        if (videoPlayIndex < videosListToPlay.size) {
            val jsonObject = JSONObject()
            jsonObject.put("workout_section_id", videosListToPlay[videoPlayIndex].workout_section_id)
            jsonObject.put("user_id", AppUtils.getUserDetails(mContext).id)

            if (videosListToPlay[videoPlayIndex].type == BrittanyEnums.SINGLE_TYPE.type) {
                jsonObject.put("video_id", videosListToPlay[videoPlayIndex].video_id)
                jsonObject.put("type", videosListToPlay[videoPlayIndex].type)
                jsonObject.put("total_time", videosListToPlay[videoPlayIndex].duration)
                if (videosListToPlay[videoPlayIndex].is_rest == 0)
                    jsonObject.put("elapsed_time", simpleExoPlayer?.currentPosition?.div((1000)))
                else
                    jsonObject.put("elapsed_time", videosListToPlay[videoPlayIndex].duration)

            } else {
                jsonObject.put("no_of_set", videosListToPlay[videoPlayIndex].no_of_sets)
                jsonObject.put("type", BrittanyEnums.SET_TYPE.type)
                jsonObject.put("video_id", videosListToPlay[videoPlayIndex].set_id)

                if (isEnd) {
                    if (videoPlayIndex + 1 < videosListToPlay.size && videosListToPlay[videoPlayIndex].current_set_count != videosListToPlay[videoPlayIndex + 1].current_set_count) {
                        jsonObject.put("elapsed_set", videosListToPlay[videoPlayIndex].current_set_count + 1)
                    } else if (videoPlayIndex + 1 < videosListToPlay.size && videosListToPlay[videoPlayIndex + 1].isNewSet) {
                        jsonObject.put("elapsed_set", videosListToPlay[videoPlayIndex].current_set_count + 1)
                    } else if (videoPlayIndex + 1 >= videosListToPlay.size) {
                        jsonObject.put("elapsed_set", videosListToPlay[videoPlayIndex].current_set_count + 1)
                    } else {
                        jsonObject.put("elapsed_set", videosListToPlay[videoPlayIndex].current_set_count)
                    }
                } else {
                    jsonObject.put("elapsed_set", videosListToPlay[videoPlayIndex].current_set_count)
                }

                val jsonObject2 = JSONObject()
                jsonObject2.put("video_id", videosListToPlay[videoPlayIndex].video_id)
                jsonObject2.put("type", videosListToPlay[videoPlayIndex].type)
                jsonObject2.put("total_time", videosListToPlay[videoPlayIndex].duration)
                if (videosListToPlay[videoPlayIndex].is_rest == 0)
                    jsonObject2.put("elapsed_time", simpleExoPlayer?.currentPosition?.div((1000)))
                else
                    jsonObject2.put("elapsed_time", videosListToPlay[videoPlayIndex].duration)
                jsonObject2.put("workout_section_id", videosListToPlay[videoPlayIndex].workout_section_id)
                jsonObject2.put("user_id", AppUtils.getUserDetails(mContext).id)
                AppController.socket.emit("update_progress", jsonObject2)

                println("Socket Sent  JSON object 2  :  $jsonObject2")
            }
            println("Socket Sent  JSON object 1  :  $jsonObject")
            AppController.socket.emit("update_progress", jsonObject)
        }
    }

    private val exoPlayerEventListener = object : Player.Listener {
/*
* Player.STATE_IDLE: This is the initial state, the state when the player is stopped, and when playback failed.
* Player.STATE_BUFFERING: The player is not able to immediately play from its current position. This mostly happens because more data needs to be loaded.
* Player.STATE_READY: The player is able to immediately play from its current position.
* Player.STATE_ENDED: The player finished playing all media.
*/

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            println("Is Video playing : $isPlaying")
            hitSocket()
            if (isPlaying)
                handler.postDelayed(runnable, 1000)
            else {
                handler.removeCallbacks(runnable)
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            when (playbackState) {
                Player.STATE_ENDED -> {
                    hitSocket()
                    videoPlayIndex++
                    tvVideoTimer.text = ""
                    handler.removeCallbacks(runnable)
                    initCheckForVideo()
                }
                Player.STATE_BUFFERING -> {
                    Loader.showLoader(this@PlayWorkoutVideoActivity) {}
                }
                Player.STATE_READY -> {
                    Loader.hideLoader()
                }
                Player.STATE_IDLE -> {}
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        hitSocket()
        stopPlayer()
        simpleExoPlayer?.removeListener(exoPlayerEventListener)
        simpleExoPlayer = null
        stopPreLoadingService()
    }

    private fun stopPlayer() {
        handler.removeCallbacks(runnable)
        if (simpleExoPlayer != null) {
            simpleExoPlayer?.release()
        }
    }

    private fun startPreLoadingService(videoCacheIndex: Int) {
        val preloadingServiceIntent = Intent(mContext, VideoPreLoadingService::class.java)
        preloadingServiceIntent.putExtra(Constants.VIDEO_LIST, videosListToPlay[videoCacheIndex].video_path)
        startService(preloadingServiceIntent)
    }

    private fun stopPreLoadingService() {
        stopService(Intent(mContext, VideoPreLoadingService::class.java))
    }

}