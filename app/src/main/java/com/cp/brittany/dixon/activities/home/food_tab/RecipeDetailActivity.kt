package com.cp.brittany.dixon.activities.home.food_tab

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.home_models.FoodDetailsModel
import com.cp.brittany.dixon.activities.home.home_models.Ingredients
import com.cp.brittany.dixon.activities.home.models.InsightBookMarkModel
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.base.AppController
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.base.toPx
import com.cp.brittany.dixon.databinding.ActivityRecipyDetailBinding
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.*
import com.faltenreich.skeletonlayout.Skeleton
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
//import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
//import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.flexbox.FlexboxLayout
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import java.util.*

class RecipeDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityRecipyDetailBinding
    //private lateinit var ingredientsAdapter: IngredientsAdapter
    private var ingredientsList: MutableList<Ingredients> = ArrayList()

    private lateinit var mContext: Context
    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>
    var foodId = ""
    private var img = ""
    private var type = ""
    private var time = ""
    private var calories = ""
    private var unit = ""
    private var title = ""
    private var isBookmarked = 0
    private var videoPath = ""
    private lateinit var httpDataSourceFactory: HttpDataSource.Factory
    private lateinit var defaultDataSourceFactory: DefaultDataSource.Factory
    private lateinit var cacheDataSourceFactory: DataSource.Factory
    private var simpleExoPlayer: ExoPlayer? = null
    private val simpleCache: SimpleCache = AppController.simpleCache
    private var handler: Handler = Handler(Looper.getMainLooper())
    private var isGuest = 0
    private lateinit var skeletonLayout: Skeleton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipyDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        window.statusBarColor = Color.TRANSPARENT

        mContext = this
        retrofitClient = RetrofitClient.getClient(mContext).create(Api::class.java)

        initVM()
        initPlayer()
        initViews()
        observeApiResponse()
        initListener()

    }

    private fun initVM() {
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        binding.lifecycleOwner = this
    }

    private fun observeApiResponse() {
        viewModel.getApiResponse().observe(this, {
            when (it.status) {
                ApiStatus.LOADING -> {
//                    Loader.showLoader(this) {
//                        if (this@RecipeDetailActivity::apiCall.isInitialized)
//                            apiCall.cancel()
//                    }
                }
                ApiStatus.ERROR -> {
                    //Loader.hideLoader()
                    AppUtils.showToast(this, it.message!!, false)
                }
                ApiStatus.SUCCESS -> {
                    //Loader.hideLoader()
                    when (it.tag) {
                        ApiTags.GET_FOOD_DETAILS -> {
                            val model =
                                Gson().fromJson(it.data.toString(), FoodDetailsModel::class.java)
                            setFoodData(model)
                        }
                        ApiTags.BOOKMARK_THINGS -> {
                            val model = Gson().fromJson(it.data.toString(), InsightBookMarkModel::class.java)
                            AppUtils.showToast(this@RecipeDetailActivity, model.message, true)
                            isBookmarked = if (model.message == "Bookmark added") {
                                binding.ivBookmark.setImageResource(R.drawable.ic_heart_selected)
                                1
                            } else {
                                binding.ivBookmark.setImageResource(R.drawable.ic_heart)
                                0
                            }
                        }
                    }
                }
            }
        })
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun setFoodData(model: FoodDetailsModel) {
        binding.ingredientTitle.viewVisible()
        binding.tvRecipeHeading.viewVisible()

//        binding.title.text = model.data.foods.type
//        binding.recipeName.text = model.data.foods.name
//        binding.time.text = "${model.data.foods.time} min"
//        binding.calories.text = "${model.data.foods.time} Calories"
//        if(img.isEmpty())
//            Glide.with(mContext).load(model.data.foods.image_path).centerCrop().into(binding.ivRecipeImage)

        isBookmarked = model.data.foods.is_bookmarked
//        binding.tvRecipeDetails.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            Html.fromHtml(model.data.foods.recipe, Html.FROM_HTML_MODE_COMPACT)
//        } else {
//            Html.fromHtml(model.data.foods.recipe)
//        }
        if (model.data.foods.is_bookmarked == 0)
            binding.ivBookmark.setImageResource(R.drawable.ic_heart)
        else
            binding.ivBookmark.setImageResource(R.drawable.ic_heart_selected)
        //Glide.with(mContext).load(model.data.foods.image_path).centerCrop().into(binding.ivRecipeImage)
        binding.calories.text = "~${model.data.foods.calories} Kcal"
        binding.time.text = "${model.data.foods.time} ${model.data.foods.unit}"
        binding.recipeName.text = model.data.foods.name
        binding.tvFoodType.text = model.data.foods.type
        binding.tvFoodCategory.text = model.data.foods.food_categories.name
        ingredientsList.addAll(model.data.foods.ingredients)
        //ingredientsAdapter.notifyDataSetChanged()
        if(ingredientsList.isNotEmpty()){
            ingredientsList.forEach {
                val textView = TextView(applicationContext)
                val params = FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT)
                params.setMargins(8.toPx.toInt(),8.toPx.toInt(),0,0)
                textView.layoutParams = params
                textView.setPadding(16.toPx.toInt(),8.toPx.toInt(),16.toPx.toInt(),8.toPx.toInt())
                textView.setTextColor(ContextCompat.getColor(applicationContext,R.color.light_blue_a200))
                textView.background = ContextCompat.getDrawable(applicationContext,R.drawable.bg_ingredients)
                textView.textSize = 14f
                textView.text = it.name
                textView.typeface = ResourcesCompat.getFont(applicationContext, R.font.roboto_regular)
                binding.ingredients.addView(textView)
            }
        }
        else{
            binding.ingredients.viewGone()
            binding.ingredientTitle.viewGone()
        }
        if (model.data.foods.video.isNullOrEmpty() && img.isNullOrEmpty()) {
            binding.ivRecipeImage.viewVisible()
            Glide.with(mContext).load(model.data.foods.image_path).centerCrop().into(binding.ivRecipeImage)
            binding.exoplayerView.viewGone()
        }
        if (!model.data.foods.video.isNullOrEmpty() && videoPath.isEmpty()) {
            binding.ivRecipeImage.viewGone()
            binding.exoplayerView.viewVisible()
            playVideo(model.data.foods.video_path)
        }
        binding.webView.webChromeClient = object: WebChromeClient(){
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if(newProgress == 100){
                    binding.skeletonLayout.viewGone()
                    skeletonLayout.showOriginal()
                }
            }
        }
        binding.webView.settings.loadsImagesAutomatically = true
        binding.webView.settings.javaScriptEnabled = true
//        binding.webView.settings.loadWithOverviewMode = true
//        binding.webView.settings.useWideViewPort = true
        binding.webView.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
        //binding.webView.settings.minimumFontSize = 40
        binding.webView.settings.builtInZoomControls = true
        binding.webView.settings.displayZoomControls = false
        binding.webView.settings.setSupportZoom(true)
        //binding.webView.settings.displayZoomControls = true
        if (Build.VERSION.SDK_INT < 8) {
            binding.webView.settings.mediaPlaybackRequiresUserGesture = true
        } else {
            binding.webView.settings.pluginState = WebSettings.PluginState.ON;
        }
        binding.webView.loadUrl("http://178.128.29.7/brittney-v02/api/getHtml/foods/$foodId")
        //binding.webView.loadData("http://178.128.29.7/brittney-v02/api/getHtml/foods/25", "text/html", "UTF-8")
    }

    private fun initPlayer() {
        httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)

        defaultDataSourceFactory = DefaultDataSource.Factory(mContext, httpDataSourceFactory)

        cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(simpleCache)
            .setUpstreamDataSourceFactory(httpDataSourceFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

        simpleExoPlayer = ExoPlayer.Builder(mContext)
            .setMediaSourceFactory(DefaultMediaSourceFactory(cacheDataSourceFactory)).build()

        binding.exoplayerView.player = simpleExoPlayer
        binding.exoplayerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        binding.exoplayerView.setShutterBackgroundColor(Color.WHITE)
        simpleExoPlayer?.playWhenReady = false
        //simpleExoPlayer?.addListener(exoPlayerEventListener)

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun playVideo(videoPath: String) {
        val videoUri = Uri.parse(videoPath)
        val mediaItem = MediaItem.fromUri(videoUri)
        val mediaSource = ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(mediaItem)

        simpleExoPlayer?.seekTo(0, 0)
        simpleExoPlayer?.repeatMode = Player.REPEAT_MODE_OFF
        simpleExoPlayer?.setMediaSource(mediaSource, true)
        simpleExoPlayer?.prepare()

    }


    @SuppressLint("SetTextI18n")
    private fun initViews() {
        skeletonLayout = binding.skeletonLayout
        binding.skeletonLayout.viewVisible()
        skeletonLayout.showSkeleton()
        val data = AppUtils.getUserDetails(applicationContext)
        isGuest = data.is_guest
        foodId = intent.getStringExtra("foodId").toString()
        img = intent.getStringExtra("image").toString()
        time = intent.getStringExtra("time").toString()
        type = intent.getStringExtra("type").toString()
        calories = intent.getStringExtra("calories").toString()
        unit = intent.getStringExtra("unit").toString()
        title = intent.getStringExtra("title").toString()
        if (intent.hasExtra("videoPath")) {
            videoPath = intent.getStringExtra("videoPath").toString()
        }
        isBookmarked = intent.getIntExtra("isBookmarked", 0)
        if (img.isNotEmpty() && videoPath == "") {
            binding.ivRecipeImage.viewVisible()
            binding.exoplayerView.viewGone()
            Glide.with(mContext).load(img).centerCrop().into(binding.ivRecipeImage)
        }
        if (videoPath != "") {
            binding.ivRecipeImage.viewGone()
            binding.exoplayerView.viewVisible()
            playVideo(videoPath)
        }

        binding.calories.text = "~$calories Kcal"
        binding.time.text = "$time $unit"
        binding.recipeName.text = title
        binding.tvFoodType.text = type

        getFoodDetails()
    }


    private fun initListener() {
        binding.ivBack.setOnClickListener {
            AppUtils.preventDoubleClick(binding.ivBack)
            setIntentData()
            onBackPressed()
        }
        binding.ivBookmark.setOnClickListener {
            AppUtils.preventDoubleClick(binding.ivBookmark)
            if (isGuest == 0)
                bookmark()
            else
                showLoginDialog()
        }
    }

    private fun bookmark() {
        apiCall = retrofitClient.bookmarkThings("foods", foodId.toInt())
        viewModel.bookmarkThings(apiCall)
    }

    private fun getFoodDetails() {
        apiCall = retrofitClient.getFoodDetails(foodId.toInt())
        viewModel.getFoodDetails(apiCall)
    }

    private fun setIntentData() {
        val intentResult = Intent()
        intentResult.putExtra("bookmark", isBookmarked)
        intentResult.putExtra("id", foodId.toInt())
        setResult(RESULT_OK, intentResult)
    }

    override fun onBackPressed() {
        binding.webView.viewGone()
        setIntentData()
        supportFinishAfterTransition()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPlayer()
        simpleExoPlayer = null
    }

    private fun stopPlayer() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer?.release()
        }
    }

}