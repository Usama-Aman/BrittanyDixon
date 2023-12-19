package com.cp.brittany.dixon.activities.home.insight_rec_detail

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.home.models.*
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivityInsightRecommendationDetailBinding
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.bumptech.glide.Glide
import com.cp.brittany.dixon.base.getAbbreviatedFromDateTime
import com.cp.brittany.dixon.utils.*
import com.faltenreich.skeletonlayout.Skeleton
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call

class InsightRecommendationDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityInsightRecommendationDetailBinding

    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>

    private lateinit var insightDetail: InsightRecommendationDetailData
    private var insightImage: String = ""
    private var insightId = -1
    private var isRemoved = false
    private var isFromBookmark = false

    private var isBookmarked = 0
    private var isLiked = false
    private var totalLiked = 0
    private var date = ""
    private var title = ""
    private var time = ""
    private var details = ""
    private var unit = ""
    private lateinit var skeletonLayout: Skeleton

    private var mediaPlayer: MediaPlayer? = null
    private var handler: Handler = Handler(Looper.getMainLooper())
    private var isGuest = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsightRecommendationDetailBinding.inflate(layoutInflater)
        retrofitClient = RetrofitClient.getClient(this).create(Api::class.java)
        window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        window.statusBarColor = resources.getColor(R.color.transparent)
        setContentView(binding.root)
        initViews()
        initVM()
        initListeners()
        observeApiResponse()
        //if (isFromBookmark)
        getInsightDetail()

    }

    private fun initViews() {
        skeletonLayout = binding.skeletonLayout
        binding.skeletonLayout.viewVisible()
        skeletonLayout.showSkeleton()
        val data = AppUtils.getUserDetails(applicationContext)
        isGuest = data.is_guest
        insightImage = intent.getStringExtra("insightImage") ?: ""
        insightId = intent?.getIntExtra("id", -1) ?: -1
        insightImage = intent.getStringExtra("insightImage") ?: ""
        isFromBookmark = intent.getBooleanExtra("isFromBookmark", false)
        date = intent?.getStringExtra("date") ?: ""
        date = getAbbreviatedFromDateTime(date)!!

        title = intent?.getStringExtra("title") ?: ""
        time = intent?.getStringExtra("time") ?: ""
        isBookmarked = intent.getIntExtra("isBookmarked", 0)
        isLiked = intent.getBooleanExtra("isLiked", false)
        totalLiked = intent.getIntExtra("totalLikes", 0)
        details = intent?.getStringExtra("details") ?: ""
        unit = intent?.getStringExtra("unit") ?: ""
        setUpData()
    }


    private fun initVM() {
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        binding.lifecycleOwner = this
    }

    private fun initListeners() {
        binding.ivBack.setOnClickListener {
            AppUtils.preventDoubleClick(binding.ivBack)
            setIntentData()
            onBackPressed()
        }
        binding.tvShare.setOnClickListener {
            AppUtils.preventDoubleClick(binding.tvShare)
            val builder: Uri.Builder = Uri.Builder()
            builder.scheme(resources.getString(R.string.web_link_url_scheme))
                .authority(resources.getString(R.string.web_link_url))
                .appendQueryParameter(Constants.insightCode, insightId.toString())
            val myUrl: String = builder.build().toString()

            val mIntentShare = Intent(Intent.ACTION_SEND)
            val shareBody = "Here is the link to BrittanyDixon app \n\n$myUrl"
            mIntentShare.type = "text/plain"
            mIntentShare.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            mIntentShare.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(mIntentShare, "Please select one option"))
        }
        binding.tvLike.setOnClickListener {
            AppUtils.preventDoubleClick(binding.tvLike)
            if (isGuest == 0)
                likeInsight()
            else
                showLoginDialog()
        }

        binding.ivBookmark.setOnClickListener {
            AppUtils.preventDoubleClick(binding.ivBookmark)
            if (isGuest == 0)
                bookmarkInsight()
            else
                showLoginDialog()
        }
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun observeApiResponse() {
        viewModel.getApiResponse().observe(this, {
            when (it.status) {
//                ApiStatus.LOADING -> {
//                    Loader.showLoader((this as AppCompatActivity)) {
//                        if (this::apiCall.isInitialized)
//                            apiCall.cancel()
//                    }
//                }
                ApiStatus.ERROR -> {
                    //Loader.hideLoader()
                    AppUtils.showToast(this, it.message!!, false)
                }
                ApiStatus.SUCCESS -> {
                    //Loader.hideLoader()
                    when (it.tag) {
                        ApiTags.GET_INSIGHT_DETAIL -> {
                            val model = Gson().fromJson(it.data.toString(), InsightRecommendationDetailModel::class.java)
                            insightDetail = model.data

                            setUpDataFromBookmarkTab()
                        }
//                        ApiTags.INSIGHT_LIKE -> {
//                            val model = Gson().fromJson(it.data.toString(), InsightLikeModel::class.java)
//                            if (model.data != null) {
//                                insightDetail.total_likes++
//                                insightDetail.is_liked = true
//                            } else {
//                                insightDetail.total_likes--
//                                insightDetail.is_liked = false
//                            }
//
//                            binding.tvLike.text = "Like (${insightDetail.total_likes})"
//                            if (insightDetail.is_liked) {
//                                binding.tvLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_liked_filled, 0, 0, 0)
//                                binding.tvLike.setTextColor(ContextCompat.getColor(this, R.color.light_green_900))
//                            } else {
//                                binding.tvLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like, 0, 0, 0)
//                                binding.tvLike.setTextColor(ContextCompat.getColor(this, R.color.home_heading_text))
//                            }
//                        }
                        ApiTags.INSIGHT_LIKE -> {
                            if (isFromBookmark) {
                                val model = Gson().fromJson(it.data.toString(), InsightLikeModel::class.java)
                                if (model.message == "Like Added") {
                                    insightDetail.total_likes++
                                    insightDetail.is_liked = true
                                } else {
                                    insightDetail.total_likes--
                                    insightDetail.is_liked = false
                                }

                                binding.tvLike.text = "Like (${insightDetail.total_likes})"
                                if (insightDetail.is_liked) {
                                    binding.tvLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_liked_filled, 0, 0, 0)
                                    binding.tvLike.setTextColor(ContextCompat.getColor(this, R.color.light_green_900))
                                } else {
                                    binding.tvLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like, 0, 0, 0)
                                    binding.tvLike.setTextColor(ContextCompat.getColor(this, R.color.home_heading_text))
                                }
                            } else {
                                val model = Gson().fromJson(it.data.toString(), InsightLikeModel::class.java)
                                if (model.message == "Like Added") {
                                    totalLiked++
                                    isLiked = true
                                } else {
                                    totalLiked--
                                    isLiked = false
                                }

                                binding.tvLike.text = "Like ($totalLiked)"
                                if (isLiked) {
                                    binding.tvLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_liked_filled, 0, 0, 0)
                                    binding.tvLike.setTextColor(ContextCompat.getColor(this, R.color.light_green_900))
                                } else {
                                    binding.tvLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like, 0, 0, 0)
                                    binding.tvLike.setTextColor(ContextCompat.getColor(this, R.color.home_heading_text))
                                }
                            }
                        }
                        ApiTags.BOOKMARK_THINGS -> {
                            val model = Gson().fromJson(it.data.toString(), InsightBookMarkModel::class.java)
                            AppUtils.showToast(this@InsightRecommendationDetailActivity, model.message, true)
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

    @SuppressLint("SetTextI18n", "ObsoleteSdkInt")
    private fun setUpDataFromBookmarkTab() {
        if(insightImage.isEmpty()) {
            Glide.with(this)
                .load(insightDetail.image_path)
                .centerCrop()
                .into(binding.ivRecommendation)
        }

//        binding.tvDetail.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            Html.fromHtml(insightDetail.detail, Html.FROM_HTML_MODE_COMPACT)
//        } else {
//            Html.fromHtml(insightDetail.detail)
//        }

        binding.tvTime.viewVisible()
        binding.ivBookmark.viewVisible()
        binding.tvCategory.viewVisible()
        binding.constraintLikes.viewVisible()
        isBookmarked = insightDetail.is_bookmarked
        if (insightDetail.is_bookmarked == 0)
            binding.ivBookmark.setImageResource(R.drawable.ic_heart)
        else
            binding.ivBookmark.setImageResource(R.drawable.ic_heart_selected)

        binding.tvLike.text = "Like (${insightDetail.total_likes})"
        totalLiked = insightDetail.total_likes
        binding.tvDate.text = getAbbreviatedFromDateTime(insightDetail.date)!!
        binding.tvCategory.text = insightDetail.insight_category.name
        if (insightDetail.is_liked) {
            binding.tvLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_liked_filled, 0, 0, 0)
            binding.tvLike.setTextColor(ContextCompat.getColor(this, R.color.light_green_900))
        } else {
            binding.tvLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like, 0, 0, 0)
            binding.tvLike.setTextColor(ContextCompat.getColor(this, R.color.home_heading_text))
        }

        binding.tvTitle.text = insightDetail.name
        binding.tvTime.text = "${insightDetail.duration} ${insightDetail.unit}"
        if(details.isEmpty()) {
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
//            binding.webView.settings.loadWithOverviewMode = true
//            binding.webView.settings.useWideViewPort = true
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
//            binding.webView.loadData(insightDetail.detail, "text/html", "UTF-8")
            binding.webView.loadUrl("http://178.128.29.7/brittney-v02/api/getHtml/insights/$insightId")
        }
    }

    @SuppressLint("SetTextI18n", "SetJavaScriptEnabled")
    private fun setUpData() {
        Glide.with(this)
            .load(insightImage)
            .centerCrop()
            .into(binding.ivRecommendation)

//        binding.tvDetail.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            Html.fromHtml(details, Html.FROM_HTML_MODE_COMPACT)
//        } else {
//            Html.fromHtml(details)
//        }

        binding.tvTime.viewVisible()
        binding.ivBookmark.viewVisible()
        binding.tvCategory.viewVisible()
        binding.constraintLikes.viewVisible()

        if (isBookmarked == 0)
            binding.ivBookmark.setImageResource(R.drawable.ic_heart)
        else
            binding.ivBookmark.setImageResource(R.drawable.ic_heart_selected)

        binding.tvLike.text = "Like ($totalLiked)"
        binding.tvDate.text = date

        if (isLiked) {
            binding.tvLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_liked_filled, 0, 0, 0)
            binding.tvLike.setTextColor(ContextCompat.getColor(this, R.color.light_green_900))
        } else {
            binding.tvLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like, 0, 0, 0)
            binding.tvLike.setTextColor(ContextCompat.getColor(this, R.color.home_heading_text))
        }
        binding.tvTitle.text = title
        binding.tvTime.text = "$time $unit"
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
        binding.webView.loadUrl("http://178.128.29.7/brittney-v02/api/getHtml/insights/$insightId")
//        binding.webView.loadData(details, "text/html", "UTF-8")

    }

    private fun getInsightDetail() {
        apiCall = retrofitClient.getInsightDetail(insightId)
        viewModel.getInsightDetail(apiCall)
    }

    private fun likeInsight() {
        apiCall = retrofitClient.likeInsight(insightId)
        viewModel.likeInsight(apiCall)
    }

    private fun bookmarkInsight() {
        apiCall = retrofitClient.bookmarkThings("insights", insightId)
        viewModel.bookmarkThings(apiCall)
    }

    private fun setIntentData() {
        val intentResult = Intent()
        intentResult.putExtra("bookmark", isBookmarked)
        intentResult.putExtra("id", insightId)
        setResult(Activity.RESULT_OK, intentResult)
    }

    override fun onBackPressed() {
        binding.webView.viewGone()
        setIntentData()
        supportFinishAfterTransition()
    }
}