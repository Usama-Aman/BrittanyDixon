package com.cp.brittany.dixon.activities.home.invite_friend

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.auth.models.UserDetailData
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.databinding.ActivityInviteFriendBinding
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.webkit.MimeTypeMap

import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.cp.brittany.dixon.activities.home.home_models.AllFoodsModel
import com.cp.brittany.dixon.activities.home.home_models.FoodSimpleSearchModel
import com.cp.brittany.dixon.activities.home.models.ChangeFoodStatusModel
import com.cp.brittany.dixon.activities.home.models.InsightBookMarkModel
import com.cp.brittany.dixon.activities.view_models.HomeViewModel
import com.cp.brittany.dixon.loader.Loader
import com.cp.brittany.dixon.network.Api
import com.cp.brittany.dixon.network.ApiTags
import com.cp.brittany.dixon.network.RetrofitClient
import com.cp.brittany.dixon.utils.*
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call

class InviteFriendActivity : BaseActivity() {
    private lateinit var binding: ActivityInviteFriendBinding
    private lateinit var userData: UserDetailData
    private lateinit var viewModel: HomeViewModel
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<ResponseBody>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInviteFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor(R.color.white)
        blackStatusBarIcons()
        retrofitClient = RetrofitClient.getClient(applicationContext).create(Api::class.java)
        initViews()
        initVM()
        initListeners()
        observeApiResponse()
    }

    private fun initViews() {
        userData = AppUtils.getUserDetails(applicationContext)

    }
    private fun initVM() {
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        binding.lifecycleOwner = this
    }
    @SuppressLint("QueryPermissionsNeeded")
    private fun initListeners() {
        binding.ivBack.setOnClickListener {
            AppUtils.preventDoubleClick(binding.ivBack)
            onBackPressed()
        }
        binding.ivOther.setOnClickListener {
            AppUtils.preventDoubleClick(binding.ivOther)
            try {
                shareOthers()
            }catch (e: Exception){

            }
        }
        binding.ivFacebook.setOnClickListener {
            AppUtils.preventDoubleClick(binding.ivFacebook)
            try {
                shareFacebook()
            }catch (e: Exception){

            }
        }
        binding.ivInstagram.setOnClickListener {
            AppUtils.preventDoubleClick(binding.ivInstagram)
            try {
                createInstagramIntent()
            }catch (e: Exception){

            }
        }
        binding.ivMessenger.setOnClickListener {
            AppUtils.preventDoubleClick(binding.ivMessenger)
            try {
                shareMessenger()
            }catch (e: Exception){

            }
        }
        binding.ivSMS.setOnClickListener {
            AppUtils.preventDoubleClick(binding.ivSMS)
            try {
                shareSms()
            }catch (e: Exception){

            }
        }
        binding.ivCopyLink.setOnClickListener {
            try {
                AppUtils.preventDoubleClick(binding.ivOther)
                val builder: Uri.Builder = Uri.Builder()
                builder.scheme(resources.getString(R.string.web_link_url_scheme))
                    .authority(resources.getString(R.string.web_link_url))
                val myUrl: String = builder.build().toString()

                val mIntentShare = Intent(Intent.ACTION_SEND)
                val shareBody = "Here is the link to BrittanyDixon app \n\n$myUrl"
                mIntentShare.type = "text/plain"
                mIntentShare.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                mIntentShare.putExtra(Intent.EXTRA_TEXT, shareBody)
                startActivity(Intent.createChooser(mIntentShare, "Please select one option"))
                // Gets a handle to the clipboard service.
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                // Creates a new text clip to put on the clipboard
                val clip: ClipData = ClipData.newPlainText("simple text", shareBody)
                // Set the clipboard's primary clip.
                clipboard.setPrimaryClip(clip)
            }catch (e: Exception){

            }

        }
        binding.btnSendInvitation.setOnClickListener {
            AppUtils.preventDoubleClick(binding.btnSendInvitation)
            if(binding.etEmail.text.toString().isValidEmail()){
                sendEmailInvitation()
            }
            else
                AppUtils.showToast(this, "Please Enter a valid email address", false)
        }
    }
    fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

    private fun shareOthers() {
        AppUtils.preventDoubleClick(binding.ivOther)
        val builder: Uri.Builder = Uri.Builder()
        builder.scheme(resources.getString(R.string.web_link_url_scheme))
            .authority(resources.getString(R.string.web_link_url))
        val myUrl: String = builder.build().toString()

        val mIntentShare = Intent(Intent.ACTION_SEND)
        val shareBody = "Here is the link to BrittanyDixon app \n\n$myUrl"
        mIntentShare.type = "text/plain"
        mIntentShare.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
        mIntentShare.putExtra(Intent.EXTRA_TEXT, shareBody)
        startActivity(Intent.createChooser(mIntentShare, "Please select one option"))
    }

    private fun shareSms() {
        val builder: Uri.Builder = Uri.Builder()
        builder.scheme(resources.getString(R.string.web_link_url_scheme))
            .authority(resources.getString(R.string.web_link_url))
        val myUrl: String = builder.build().toString()
        val shareBody = "Here is the link to BrittanyDixon app \n\n$myUrl"
        val mIntentShare = Intent(Intent.ACTION_SEND).apply {
            data = Uri.parse("smsto:")  // This ensures only SMS apps respond
            //type = "vnd.android-dir/mms-sms"
            putExtra("sms_body", shareBody)
        }
        if (mIntentShare.resolveActivity(packageManager) != null) {
            startActivity(mIntentShare)
        }
    }

    private fun shareFacebook() {
        val builder: Uri.Builder = Uri.Builder()
        builder.scheme(resources.getString(R.string.web_link_url_scheme))
            .authority(resources.getString(R.string.web_link_url))
        val myUrl: String = builder.build().toString()
        val shareBody = "Here is the link to BrittanyDixon app \n\n$myUrl"

        val application = "com.facebook.katana"
        val installed = checkAppInstall(application)
        if (installed) {
            val mIntentShare = Intent(Intent.ACTION_SEND)
//            val mStrExtension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(mFileImagePath).toString())
//            val mStrMimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mStrExtension)
//            if (mStrExtension.equals("", ignoreCase = true) || mStrMimeType == null) {
//                // if there is no extension or there is no definite mimetype, still try to open the file
//                mIntentShare.type = "text*//*"
//            } else {
//                mIntentShare.type = mStrMimeType
//            }
            mIntentShare.type = "text/plain"
            //mIntentShare.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(mFileImagePath))
            mIntentShare.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            mIntentShare.putExtra(Intent.EXTRA_TEXT, shareBody)
            mIntentShare.putExtra(Intent.EXTRA_TITLE, "Here is the link to BrittanyDixon app")
            mIntentShare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            mIntentShare.setPackage(application)
            startActivity(mIntentShare)
        } else {
            Toast.makeText(applicationContext, "Facebook have not been installed.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareMessenger() {
        val builder: Uri.Builder = Uri.Builder()
        builder.scheme(resources.getString(R.string.web_link_url_scheme))
            .authority(resources.getString(R.string.web_link_url))
        val myUrl: String = builder.build().toString()
        val shareBody = "Here is the link to BrittanyDixon app \n\n$myUrl"

        val application = "com.facebook.orca"
        val installed = checkAppInstall(application)
        if (installed) {
            val mIntentShare = Intent(Intent.ACTION_SEND)
//            val mStrExtension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(mFileImagePath).toString())
//            val mStrMimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mStrExtension)
//            if (mStrExtension.equals("", ignoreCase = true) || mStrMimeType == null) {
//                // if there is no extension or there is no definite mimetype, still try to open the file
//                mIntentShare.type = "text*//*"
//            } else {
//                mIntentShare.type = mStrMimeType
//            }
            mIntentShare.type = "text/plain"
            //mIntentShare.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(mFileImagePath))
            mIntentShare.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            mIntentShare.putExtra(Intent.EXTRA_TEXT, shareBody)
            mIntentShare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            mIntentShare.setPackage(application)
            startActivity(mIntentShare)
        } else {
            Toast.makeText(applicationContext, "Messenger have not been installed.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createInstagramIntent() {
        val builder: Uri.Builder = Uri.Builder()
        builder.scheme(resources.getString(R.string.web_link_url_scheme))
            .authority(resources.getString(R.string.web_link_url))
        val myUrl: String = builder.build().toString()
        val shareBody = "Here is the link to BrittanyDixon app \n\n$myUrl"
        // Create the new Intent using the 'Send' action.
        val share = Intent(Intent.ACTION_SEND)

        val application = "com.instagram.android"
        val installed = checkAppInstall(application)
        if (installed) {
            // Set the MIME type
            share.type = "text/plain"

            share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            share.putExtra(Intent.EXTRA_TEXT, shareBody)
            share.setPackage(application)
            // Broadcast the Intent.
            startActivity(share)
        } else {
            Toast.makeText(applicationContext, "Instagram have not been installed.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkAppInstall(uri: String): Boolean {
        val pm = packageManager
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            //Error
        }
        return false
    }

    private fun sendEmailInvitation() {
        apiCall = retrofitClient.sendEmailInvitation(binding.etEmail.text.toString(),"http://www.brittany.com")
        viewModel.sendEmailInvitation(apiCall)
    }
    private fun observeApiResponse() {
        viewModel.getApiResponse().observe(this) {
            when (it.status) {
                ApiStatus.LOADING -> {
                    //if (it.tag != ApiTags.GET_ALL_FOODS)
                    Loader.showLoader(this) {
//                            if (this::apiCall.isInitialized)
//                                apiCall.cancel()
                    }
                }
                ApiStatus.ERROR -> {
                    Loader.hideLoader()
                    AppUtils.showToast(this, it.message ?: "error", false)
                }
                ApiStatus.SUCCESS -> {
                    Loader.hideLoader()
                    when (it.tag) {
                        ApiTags.EMAIL_INVITATION -> {
                            AppUtils.showToast(this, it.message ?: "Invitation sent", true)
                        }
                    }
                }
            }
        }
    }
}
